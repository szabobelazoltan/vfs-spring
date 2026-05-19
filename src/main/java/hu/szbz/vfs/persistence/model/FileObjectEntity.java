package hu.szbz.vfs.persistence.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "FILEOBJECT")
@DiscriminatorColumn(name = "TYPE")
public class FileObjectEntity extends EntityBase {
    @Column(name = "EXT_ID")
    private String externalId;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")
    private DirectoryEntity parent;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private FileObjectStatus status;

    @Column(name = "DELETION_DT")
    private LocalDateTime deletionDateTime;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DirectoryEntity getParent() {
        return parent;
    }

    public void setParent(DirectoryEntity parent) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileObjectEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(externalId, that.externalId) && Objects.equals(name, that.name) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), externalId, name, status);
    }

    public boolean isDirectory() {
        return false;
    }

    public boolean isFile() {
        return false;
    }

    public boolean isLink() {
        return false;
    }
}
