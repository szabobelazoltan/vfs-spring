package hu.szbz.vfs.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "FILEOBJECT")
public class FileObjectEntity extends EntityBase {
    @Column(name = "EXT_ID")
    private String externalId;

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private FileObjectType type;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")
    private FileObjectEntity parent;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private FileObjectStatus status;

    @Column(name = "DELETION_DT")
    private LocalDateTime deletionDateTime;

    @Column(name = "CONTENT_REF")
    private String contentReference;

    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID")
    private ApplicationEntity application;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public FileObjectType getType() {
        return type;
    }

    public void setType(FileObjectType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileObjectEntity getParent() {
        return parent;
    }

    public void setParent(FileObjectEntity parent) {
        this.parent = parent;
    }

    public FileObjectStatus getStatus() {
        return status;
    }

    public void setStatus(FileObjectStatus status) {
        this.status = status;
    }

    public LocalDateTime getDeletionDateTime() {
        return deletionDateTime;
    }

    public void setDeletionDateTime(LocalDateTime deletionDateTime) {
        this.deletionDateTime = deletionDateTime;
    }

    public String getContentReference() {
        return contentReference;
    }

    public void setContentReference(String contentReference) {
        this.contentReference = contentReference;
    }

    public ApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileObjectEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(externalId, that.externalId) && type == that.type && Objects.equals(name, that.name) && Objects.equals(contentReference, that.contentReference) && Objects.equals(application, that.application);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), externalId, type, name, contentReference, application);
    }

    public boolean isDirectory() {
        return FileObjectType.DIRECTORY.equals(this.type);
    }
}
