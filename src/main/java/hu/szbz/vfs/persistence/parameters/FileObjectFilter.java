package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.FileObjectEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public abstract class FileObjectFilter<P> {
    protected final P parameter;

    protected FileObjectFilter(P parameter) {
        this.parameter = parameter;
    }

    public abstract Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> fileObjectEntityPath);
}
