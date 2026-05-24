package hu.szbz.vfs.persistence.parameters;

import hu.szbz.vfs.persistence.model.EntityBase_;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

public class FileObjectSort {
    private final String attributeName;
    private final boolean ascending;

    private FileObjectSort(String attributeName, boolean ascending) {
        this.attributeName = attributeName;
        this.ascending = ascending;
    }

    public Order createOrder(CriteriaBuilder criteriaBuilder, Path<FileObjectEntity> path) {
        if (ascending) return criteriaBuilder.asc(path.get(attributeName));
        else return criteriaBuilder.desc(path.get(attributeName));
    }

    public static FileObjectSort byCreationDateTime(boolean ascending) {
        return new FileObjectSort(EntityBase_.CREATION_DATE_TIME, ascending);
    }

    public static FileObjectSort byModificationDateTime(boolean ascending) {
        return new FileObjectSort(EntityBase_.MODIFICATION_DATE_TIME, ascending);
    }

    public static FileObjectSort byDeletionDateTime(boolean ascending) {
        return new FileObjectSort(FileObjectEntity_.DELETION_DATE_TIME, ascending);
    }

    public static FileObjectSort byName(boolean ascending) {
        return new FileObjectSort(FileObjectEntity_.NAME, ascending);
    }

    public static FileObjectSort byType(boolean ascending) {
        return new FileObjectSort(FileObjectEntity_.TYPE, ascending);
    }
}
