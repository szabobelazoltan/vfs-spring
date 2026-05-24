package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.EntityBase;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.SingularAttribute;

import java.time.LocalDateTime;

public class DateTimeFilter extends FileObjectFilter<DateTimeFilter.Range> {
    private final SingularAttribute<EntityBase, LocalDateTime> dateTimeAttribute;

    private DateTimeFilter(Range parameter, SingularAttribute<EntityBase, LocalDateTime> dateTimeAttribute) {
        super(parameter);
        this.dateTimeAttribute = dateTimeAttribute;
    }

    @Override
    public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> fileObjectEntityPath) {
        LocalDateTime start = parameter.lowerBound;
        LocalDateTime end = parameter.upperBound;
        if (start != null && end != null) return criteriaBuilder.between(fileObjectEntityPath.get(dateTimeAttribute), start, end);
        else if (start != null) return criteriaBuilder.greaterThanOrEqualTo(fileObjectEntityPath.get(dateTimeAttribute), start);
        else if (end != null) return criteriaBuilder.lessThanOrEqualTo(fileObjectEntityPath.get(dateTimeAttribute), end);
        else throw new IllegalArgumentException("Either the lower or the upper bound of the range must be set!");
    }

    public record Range(LocalDateTime lowerBound, LocalDateTime upperBound) {}

    public static DateTimeFilter forCreationDateTime(Range parameter) {
        return new DateTimeFilter(parameter, FileObjectEntity_.creationDateTime);
    }
}
