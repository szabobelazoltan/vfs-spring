package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.Collections;
import java.util.List;

public final class ParentFilter extends FileObjectFilter<List<FileObjectEntity>> {
    public ParentFilter(List<FileObjectEntity> parameter) {
        super(parameter);
    }

    public ParentFilter(FileObjectEntity parameter) {
        super(Collections.singletonList(parameter));
    }

    @Override
    public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> fileObjectEntityPath) {
        var cond = criteriaBuilder.in(fileObjectEntityPath.get(FileObjectEntity_.parent));
        this.parameter.forEach(cond::value);
        return cond;
    }
}
