package hu.szbz.vfs.operationhandler;

import hu.szbz.vfs.components.FileObjectProtection;
import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.repositories.ActorEntityRepository;
import hu.szbz.vfs.persistence.repositories.ApplicationEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.soap.ResponseHeader;
import hu.szbz.vfs.soap.WsResponseBase;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class OperationInvoker {
    private static final String FILEOBJECT_ID_HOME = "HOME";

    private final ApplicationEntityRepository applicationRepository;
    private final ActorEntityRepository actorEntityRepository;
    private final FileObjectEntityRepository fileObjectEntityRepository;
    private final FileObjectProtection protection;

    public OperationInvoker(ApplicationEntityRepository applicationRepository, ActorEntityRepository actorEntityRepository, FileObjectEntityRepository fileObjectEntityRepository, FileObjectProtection protection) {
        this.applicationRepository = applicationRepository;
        this.actorEntityRepository = actorEntityRepository;
        this.fileObjectEntityRepository = fileObjectEntityRepository;
        this.protection = protection;
    }

    public <T, R extends WsResponseBase> R invoke(
            FileObjectOperation<T, R> operation,
            Permission requiredPermission,
            MutableOperationParameter<T> operationParameter,
            Supplier<R> responseFactory) {
        try {
            var application = findApplication(operationParameter.getApplicationId());
            var actor = findActor(operationParameter.getActorId());
            var fileObject = findFileObject(operationParameter.getFileObjectId(), actor);
            var calculatedPermissions = protection.calculateAndCheckPermission(actor, fileObject, requiredPermission);
            operationParameter.setApplication(application);
            operationParameter.setActor(actor);
            operationParameter.setFileObject(fileObject);
            operationParameter.setCalculatedPermissions(calculatedPermissions);
            return setResponseHeader(operation.perform(operationParameter), null);
        } catch (VirtualFileSystemException ex) {
            return setResponseHeader(responseFactory.get(), ex);
        }
    }

    private ActorEntity findActor(String actorId) throws VirtualFileSystemException {
        var actor = actorEntityRepository.findByExternalId(actorId);
        if (actor.isEmpty()) throw new VirtualFileSystemException(String.format("Actor is not found with id: %s", actorId), ErrorCode.UNKNOWN_ACTOR);
        return actor.get();
    }

    private ApplicationEntity findApplication(String applicationId) throws VirtualFileSystemException {
        var application = applicationRepository.findByExternalId(applicationId);
        if (application.isEmpty()) throw new VirtualFileSystemException(String.format("Application is not found with id: %s", applicationId), ErrorCode.UNKNOWN_APPLICATION);
        return application.get();
    }

    private FileObjectEntity findFileObject(String id, ActorEntity actor) throws VirtualFileSystemException {
        if (FILEOBJECT_ID_HOME.equals(id)) {
            var home = fileObjectEntityRepository.findHome(actor);
            if (home.isEmpty()) throw new VirtualFileSystemException(String.format("Home directory is not found for user: %s!", actor.getExternalId()), ErrorCode.FILEOBJECT_NOT_EXIST);
            return home.get();
        } else {
            return fileObjectEntityRepository.getByExternalId(id);
        }
    }

    private <R extends WsResponseBase> R setResponseHeader(R response, VirtualFileSystemException ex) {
        response.setHeader(new ResponseHeader());
        response.getHeader().setSuccess(ex == null);
        if (!response.getHeader().isSuccess()) {
            response.getHeader().setErrorCode(ex.getErrorCode().name());
            response.getHeader().setErrorMessage(ex.getMessage());
        }
        return response;
    }
}
