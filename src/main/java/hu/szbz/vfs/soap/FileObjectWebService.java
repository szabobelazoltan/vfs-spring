package hu.szbz.vfs.soap;

import hu.szbz.vfs.operationhandler.MutableOperationParameter;
import hu.szbz.vfs.operationhandler.OperationInvoker;
import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.services.CreatorService;
import hu.szbz.vfs.services.DeletionService;
import hu.szbz.vfs.services.ManipulationService;
import hu.szbz.vfs.services.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;

@Endpoint
public class FileObjectWebService implements FileObjectPortType {
    private final OperationInvoker operationInvoker;
    private final QueryService queryService;
    private final CreatorService creatorService;
    private final ManipulationService manipulationService;
    private final DeletionService deletionService;

    @Autowired
    public FileObjectWebService(OperationInvoker operationInvoker, QueryService queryService, CreatorService creatorService, ManipulationService manipulationService, DeletionService deletionService) {
        this.operationInvoker = operationInvoker;
        this.queryService = queryService;
        this.creatorService = creatorService;
        this.manipulationService = manipulationService;
        this.deletionService = deletionService;
    }

    @Override
    public CreateDirResponseType createDir(CreateDirRequestType parameters) {
        var operationParameter = MutableOperationParameter.fromRequest(parameters, parameters.getBody());
        return operationInvoker.invoke(creatorService::createDir, Permission.WRITE, operationParameter, CreateDirResponseType::new);
    }

    @Override
    public CreateFileResponseType createFile(CreateFileRequestType parameters) {
        var operationParameter = MutableOperationParameter.fromRequest(parameters, parameters.getBody());
        return operationInvoker.invoke(creatorService::createFile, Permission.WRITE, operationParameter, CreateFileResponseType::new);
    }

    @Override
    public RenameFileObjectResponseType renameFileObject(RenameFileObjectRequestType parameters) {
        var operationParameter = MutableOperationParameter.fromRequest(parameters, parameters.getBody());
        return operationInvoker.invoke(manipulationService::rename, Permission.RENAME, operationParameter, RenameFileObjectResponseType::new);
    }

    @Override
    public MoveFileObjectResponseType moveFileObject(MoveFileObjectRequestType parameters) {
        var operationParameter = MutableOperationParameter.fromRequest(parameters, parameters.getBody());
        return operationInvoker.invoke(manipulationService::move, Permission.MOVE, operationParameter, MoveFileObjectResponseType::new);
    }

    @Override
    public DeleteFileObjectResponseType deleteFileObject(DeleteFileObjectRequestType parameters) {
        var operationParameter = MutableOperationParameter.fromRequest(parameters, parameters.getBody());
        return operationInvoker.invoke(deletionService::delete, Permission.DELETE, operationParameter, DeleteFileObjectResponseType::new);
    }

    @Override
    public GetFileObjectDetailsResponseType getFileObjectDetails(GetFileObjectDetailsRequestType parameters) {
        var operationParameter = MutableOperationParameter.fromRequest(parameters, parameters.getBody());
        return operationInvoker.invoke(queryService::getFileObjectDetails, Permission.READ, operationParameter, GetFileObjectDetailsResponseType::new);
    }

    @Override
    public SearchFileObjectsResponseType searchFileObjects(SearchFileObjectsRequestType parameters) {
        var operationParameter = MutableOperationParameter.fromRequest(parameters, parameters.getBody());
        return operationInvoker.invoke(queryService::search, Permission.READ, operationParameter, SearchFileObjectsResponseType::new);
    }

    @Override
    public GetSpecialDirectoryResponseType getSpecialDirectory(GetSpecialDirectoryRequestType parameters) {
        return queryService.getSpecialDirectory(parameters);
    }
}
