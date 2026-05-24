package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class NameFilter extends FileObjectFilter<NameFilter.Parameter> {
    public NameFilter(Parameter parameter) {
        super(parameter);
    }

    @Override
    public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> fileObjectEntityPath) {
        Path<String> namePath = fileObjectEntityPath.get(FileObjectEntity_.name);
        return switch (this.parameter.compareMode) {
            case EQUALS -> criteriaBuilder.equal(namePath, this.parameter.name);
            case STARTS_WITH -> criteriaBuilder.like(namePath, this.parameter.name + "%");
            case CONTAINS -> criteriaBuilder.like(namePath, "%" + this.parameter.name + "%");
        };
    }

    public enum CompareMode { CONTAINS, STARTS_WITH, EQUALS }

    public record Parameter(String name, CompareMode compareMode) { }
}
