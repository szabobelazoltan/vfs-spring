package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity_;
import hu.szbz.vfs.persistence.model.FileObjectType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.Set;

public class TypeFilter extends FileObjectFilter<Set<FileObjectType>> {
    public TypeFilter(Set<FileObjectType> parameter) {
        super(parameter);
    }

    @Override
    public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> fileObjectEntityPath) {
        var cond = criteriaBuilder.in(fileObjectEntityPath.get(FileObjectEntity_.type));
        this.parameter.forEach(cond::value);
        return cond;
    }
}
