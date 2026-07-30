package hu.szbz.vfs.components;

import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.repositories.ActorEntityRepository;
import hu.szbz.vfs.persistence.repositories.ApplicationEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;

public class MandatoryDataProvider {
    private final ApplicationEntityRepository applicationRepository;
    private final ActorEntityRepository actorEntityRepository;
    protected final FileObjectEntityRepository fileObjectEntityRepository;
    protected final FileObjectProtection protection;

    public MandatoryDataProvider(ApplicationEntityRepository applicationRepository, ActorEntityRepository actorEntityRepository, FileObjectEntityRepository fileObjectEntityRepository, FileObjectProtection protection) {
        this.applicationRepository = applicationRepository;
        this.actorEntityRepository = actorEntityRepository;
        this.fileObjectEntityRepository = fileObjectEntityRepository;
        this.protection = protection;
    }

    protected final ActorEntity findActor(String actorId) throws VirtualFileSystemException {
        var actor = actorEntityRepository.findByExternalId(actorId);
        if (actor.isEmpty()) throw new VirtualFileSystemException(String.format("Actor is not found with id: %s", actorId), ErrorCode.UNKNOWN_ACTOR);
        return actor.get();
    }

    protected final ApplicationEntity findApplication(String applicationId) throws VirtualFileSystemException {
        var application = applicationRepository.findByExternalId(applicationId);
        if (application.isEmpty()) throw new VirtualFileSystemException(String.format("Application is not found with id: %s", applicationId), ErrorCode.UNKNOWN_APPLICATION);
        return application.get();
    }
}
