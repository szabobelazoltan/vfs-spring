package hu.szbz.vfs.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
@DiscriminatorValue("FILE")
public class FileEntity extends FileObjectEntity {
    @Column(name = "CONTENT_REF")
    private String contentReference;

    public String getContentReference() {
        return contentReference;
    }

    public void setContentReference(String contentReference) {
        this.contentReference = contentReference;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(contentReference, that.contentReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contentReference);
    }

    @Override
    public boolean isFile() {
        return true;
    }
}
