package hu.szbz.vfs.services;

import hu.szbz.vfs.components.FileObjectMapper;
import hu.szbz.vfs.components.SpecialDirectoryLookUp;
import hu.szbz.vfs.components.TreeTraversal;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.operationhandler.OperationParameter;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectStatus;
import hu.szbz.vfs.persistence.model.FileObjectType;
import hu.szbz.vfs.persistence.parameters.ApplicationFilter;
import hu.szbz.vfs.persistence.parameters.DateTimeFilter;
import hu.szbz.vfs.persistence.parameters.FileObjectFilter;
import hu.szbz.vfs.persistence.parameters.FileObjectSort;
import hu.szbz.vfs.persistence.parameters.NameFilter;
import hu.szbz.vfs.persistence.parameters.ParentFilter;
import hu.szbz.vfs.persistence.parameters.StatusFilter;
import hu.szbz.vfs.persistence.parameters.TypeFilter;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.soap.FilterFieldEnumType;
import hu.szbz.vfs.soap.GetFileObjectDetailsRequestBody;
import hu.szbz.vfs.soap.GetFileObjectDetailsResponseBody;
import hu.szbz.vfs.soap.GetFileObjectDetailsResponseType;
import hu.szbz.vfs.soap.GetSpecialDirectoryRequestType;
import hu.szbz.vfs.soap.GetSpecialDirectoryResponseBody;
import hu.szbz.vfs.soap.GetSpecialDirectoryResponseType;
import hu.szbz.vfs.soap.ResponseHeader;
import hu.szbz.vfs.soap.SearchFileObjectsRequestBody;
import hu.szbz.vfs.soap.SearchFileObjectsResponseBody;
import hu.szbz.vfs.soap.SearchFileObjectsResponseType;
import hu.szbz.vfs.soap.SearchFilterType;
import hu.szbz.vfs.soap.SortParameterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String EMPTY_DATETIME_MARKER = "-";

    private final SpecialDirectoryLookUp specialDirectoryLookUp;
    private final TreeTraversal treeTraversal;
    private final FileObjectMapper mapper;
    private final FileObjectEntityRepository fileObjectEntityRepository;

    @Autowired
    public QueryService(SpecialDirectoryLookUp specialDirectoryLookUp, TreeTraversal treeTraversal, FileObjectMapper mapper, FileObjectEntityRepository fileObjectEntityRepository) {
        this.specialDirectoryLookUp = specialDirectoryLookUp;
        this.treeTraversal = treeTraversal;
        this.mapper = mapper;
        this.fileObjectEntityRepository = fileObjectEntityRepository;
    }

    public GetSpecialDirectoryResponseType getSpecialDirectory(GetSpecialDirectoryRequestType request) {
        var rp = new GetSpecialDirectoryResponseType();
        rp.setHeader(new ResponseHeader());
        try {
            var opParam = specialDirectoryLookUp.lookUp(request.getHeader().getApplicationId(), request.getHeader().getActorId(), request.getBody().getKey());
            rp.getHeader().setSuccess(true);
            rp.setBody(new GetSpecialDirectoryResponseBody());
            rp.getBody().setFileObjectBasicInfo(mapper.mapToBasicInfo(opParam.getFileObject(), opParam.getCalculatedPermissions()));
        } catch (VirtualFileSystemException ex) {
            rp.getHeader().setSuccess(false);
            rp.getHeader().setErrorCode(ex.getErrorCode().name());
            rp.getHeader().setErrorMessage(ex.getMessage());
        }
        return rp;
    }

    public GetFileObjectDetailsResponseType getFileObjectDetails(OperationParameter<GetFileObjectDetailsRequestBody> param) {
        var path = treeTraversal.traverseUp(param.getFileObject(), new TreeTraversal.TreeTraversalProcessor<List<FileObjectEntity>>() {
            @Override
            public List<FileObjectEntity> prepareResult() {
                return new LinkedList<>();
            }

            @Override
            public List<FileObjectEntity> processNode(FileObjectEntity node, List<FileObjectEntity> oldResultValue) {
                oldResultValue.add(node);
                return oldResultValue;
            }

            @Override
            public List<FileObjectEntity> postProcessResult(List<FileObjectEntity> result) {
                Collections.reverse(result);
                return result;
            }
        });
        var rp = new GetFileObjectDetailsResponseType();
        rp.setBody(new GetFileObjectDetailsResponseBody());
        rp.getBody().setFileObjectDetailsInfo(mapper.mapToDetailsInfo(param.getFileObject(), param.getCalculatedPermissions(), path));
        return rp;
    }

    public SearchFileObjectsResponseType search(OperationParameter<SearchFileObjectsRequestBody> param) {
        var parents = collectSearchDirectories(param.getFileObject(), param.getRequestBody().getFilters().getFilter());
        var filters = createFilters(param, parents);
        var sorts = createSorts(param.getRequestBody().getSort().getSort());
        var result = fileObjectEntityRepository.listByFilters(
                param.getActor(),
                param.getRequestBody().getPageSize(),
                param.getRequestBody().getPageIndex(),
                filters,
                sorts);
        SearchFileObjectsResponseType rp = new SearchFileObjectsResponseType();
        rp.setBody(new SearchFileObjectsResponseBody());
        rp.getBody().setTotalCount(result.totalMatches());
        rp.getBody().getItems().addAll(mapper.mapResultItemList(result.items()));
        return rp;
    }

    private List<FileObjectEntity> collectSearchDirectories(FileObjectEntity startNode, List<SearchFilterType> filters) {
        var isRecursive = filters.stream()
                .anyMatch(f -> FilterFieldEnumType.RECURSIVE.equals(f.getField()) && Boolean.TRUE.toString().equals(f.getValues().getValue().get(0)));
        if (isRecursive) {
            return treeTraversal.traverseDown(startNode, new TreeTraversal.SubTreeCollector());
        } else {
            return Collections.singletonList(startNode);
        }
    }

    private List<FileObjectFilter<?>> createFilters(OperationParameter<SearchFileObjectsRequestBody> param, List<FileObjectEntity> parents) {
        List<FileObjectFilter<?>> filters = new ArrayList<>();
        filters.add(new ParentFilter(parents));
        for (SearchFilterType rqFilter : param.getRequestBody().getFilters().getFilter()) {
            var values = rqFilter.getValues().getValue();
            var filter = switch (rqFilter.getField()) {
                case NAME -> createNameFilter(values);
                case TYPE -> createTypeFilter(values);
                case STATUS -> createStatusFilter(values);
                case APPLICATION -> createApplicationFilter(values);
                case CREATION_DATETIME -> createCreationDateTimeFilter(values);
                default -> throw new UnsupportedOperationException();
            };
            filters.add(filter);
        }
        return filters;
    }

    private NameFilter createNameFilter(List<String> values) {
        final int idxText = 0;
        final int idxCompareMode = 1;
        var text = values.get(idxText);
        var compareMode = NameFilter.CompareMode.valueOf(values.get(idxCompareMode));
        return new NameFilter(new NameFilter.Parameter(text, compareMode));
    }

    private TypeFilter createTypeFilter(List<String> values) {
        var types = values.stream()
                .map(FileObjectType::valueOf)
                .collect(Collectors.toSet());
        return new TypeFilter(types);
    }

    private StatusFilter createStatusFilter(List<String> values) {
        var statuses = values.stream()
                .map(FileObjectStatus::valueOf)
                .collect(Collectors.toSet());
        return new StatusFilter(statuses);
    }

    private ApplicationFilter createApplicationFilter(List<String> values) {
        var applicationIdList = new HashSet<String>(values.size() + 1);
        applicationIdList.addAll(values);
        return new ApplicationFilter(applicationIdList);
    }

    private DateTimeFilter createCreationDateTimeFilter(List<String> values) {
        var params = new LocalDateTime[2];
        for (int i = 0; i < values.size(); i++) {
            var value = values.get(i);
            params[i] = EMPTY_DATETIME_MARKER.equals(value) ? null : LocalDateTime.parse(value, DATETIME_FORMAT);
        }
        DateTimeFilter.Range range = new DateTimeFilter.Range(params[0], params[1]);
        return DateTimeFilter.forCreationDateTime(range);
    }

    private List<FileObjectSort> createSorts(List<SortParameterType> rq) {
        List<FileObjectSort> sorts = new ArrayList<>(rq.size());
        for (SortParameterType param : rq) {
            var sort = switch (param.getField()) {
                case NAME -> FileObjectSort.byName(param.isAscending());
                case TYPE -> FileObjectSort.byType(param.isAscending());
                case CREATION_DATETIME -> FileObjectSort.byCreationDateTime(param.isAscending());
                case MODIFICATION_DATETIME -> FileObjectSort.byModificationDateTime(param.isAscending());
                case DELETION_DATETIME -> FileObjectSort.byDeletionDateTime(param.isAscending());
                default -> throw new IllegalArgumentException();
            };
            sorts.add(sort);
        }
        return sorts;
    }
}
