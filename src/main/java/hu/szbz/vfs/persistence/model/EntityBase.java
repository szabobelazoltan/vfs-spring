package hu.szbz.vfs.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public class EntityBase {
    @Id
    private int id;

    @Column(name = "CREATION_DT")
    private LocalDateTime creationDateTime;

    @Column(name = "MODIFICATION_DT")
    private LocalDateTime modificationDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getModificationDateTime() {
        return modificationDateTime;
    }

    public void setModificationDateTime(LocalDateTime modificationDateTime) {
        this.modificationDateTime = modificationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EntityBase that = (EntityBase) o;
        return id == that.id && Objects.equals(creationDateTime, that.creationDateTime) && Objects.equals(modificationDateTime, that.modificationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDateTime, modificationDateTime);
    }
}
