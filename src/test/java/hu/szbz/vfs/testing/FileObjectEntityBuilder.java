package hu.szbz.vfs.testing;

import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectStatus;
import hu.szbz.vfs.persistence.model.FileObjectType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class FileObjectEntityBuilder extends EntityBuilderBase<FileObjectEntity> {
    private final FileObjectType type;
    private String externalId;
    private String name;
    private FileObjectEntity parent;
    private LocalDateTime deletionDateTime;
    private String contentReference;
    private ApplicationEntity application;
    private FileObjectStatus status = FileObjectStatus.ACTIVE;

    public FileObjectEntityBuilder(FileObjectType type) {
        this.type = type;
        this.externalId = String.format("%s_%d", this.type, ID_RANDOMIZER.nextInt());
    }

    public FileObjectEntityBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FileObjectEntityBuilder withParent(FileObjectEntity parent) {
        this.parent = parent;
        return this;
    }

    public FileObjectEntityBuilder withDeletionDateTime(LocalDateTime deletionDateTime) {
        this.deletionDateTime = deletionDateTime;
        return this;
    }

    public FileObjectEntityBuilder withContentReference(String contentReference) {
        this.contentReference = contentReference;
        return this;
    }

    public FileObjectEntityBuilder withApplication(ApplicationEntity application) {
        this.application = application;
        return this;
    }

    public FileObjectEntityBuilder withStatus(FileObjectStatus status) {
        this.status = status;
        return this;
    }

    @Override
    protected FileObjectEntity create() {
        var entity = new FileObjectEntity();
        entity.setExternalId(this.externalId);
        entity.setType(this.type);
        entity.setStatus(status);
        entity.setName(this.name);
        entity.setParent(this.parent);
        entity.setApplication(this.application);
        entity.setDeletionDateTime(this.deletionDateTime);
        entity.setContentReference(this.contentReference);
        return entity;
    }
}
