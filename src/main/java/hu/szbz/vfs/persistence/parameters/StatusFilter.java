package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity_;
import hu.szbz.vfs.persistence.model.FileObjectStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.Set;

public class StatusFilter extends FileObjectFilter<Set<FileObjectStatus>> {
    public StatusFilter(Set<FileObjectStatus> parameter) {
        super(parameter);
    }

    @Override
    public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> fileObjectEntityPath) {
        var cond = criteriaBuilder.in(fileObjectEntityPath.get(FileObjectEntity_.status));
        parameter.forEach(cond::value);
        return cond;
    }
}
