package hu.szbz.vfs.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "ACCESS")
public class AccessEntity {
    @Id
    private long id;

    @Column(name = "PERMISSIONS")
    private int permissions;

    @Column(name = "ROLE")
    @Enumerated(EnumType.ORDINAL)
    private AccessRole role;

    @ManyToOne
    @JoinColumn(name = "FILEOBJECT_ID", referencedColumnName = "ID")
    private FileObjectEntity fileObject;

    @ManyToOne
    @JoinColumn(name = "ACTOR_ID", referencedColumnName = "ID")
    private ActorEntity actor;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public AccessRole getRole() {
        return role;
    }

    public void setRole(AccessRole role) {
        this.role = role;
    }

    public FileObjectEntity getFileObject() {
        return fileObject;
    }

    public void setFileObject(FileObjectEntity fileObject) {
        this.fileObject = fileObject;
    }

    public ActorEntity getActor() {
        return actor;
    }

    public void setActor(ActorEntity actor) {
        this.actor = actor;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccessEntity that)) return false;
        return id == that.id && permissions == that.permissions && role == that.role && Objects.equals(fileObject, that.fileObject) && Objects.equals(actor, that.actor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permissions, role, fileObject, actor);
    }

    @Override
    public String toString() {
        return "AccessEntity{" +
                "id=" + id +
                ", permissions=" + permissions +
                ", role=" + role +
                ", fileObject=" + fileObject +
                ", actor=" + actor +
                '}';
    }
}
