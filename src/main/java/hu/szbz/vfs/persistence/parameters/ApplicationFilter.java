package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity_;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.Set;

public class ApplicationFilter extends FileObjectFilter<Set<String>> {
    public ApplicationFilter(Set<String> parameter) {
        super(parameter);
    }

    @Override
    public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> fileObjectEntityPath) {
        var appPath = fileObjectEntityPath.get(FileObjectEntity_.application);
        var cond = criteriaBuilder.in(appPath.get(ApplicationEntity_.externalId));
        parameter.forEach(cond::value);
        return cond;
    }
}
