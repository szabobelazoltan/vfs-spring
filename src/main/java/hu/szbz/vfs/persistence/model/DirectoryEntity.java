package hu.szbz.vfs.persistence.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DIRECTORY")
public class DirectoryEntity extends FileObjectEntity {
    @Override
    public boolean isDirectory() {
        return true;
    }
}
