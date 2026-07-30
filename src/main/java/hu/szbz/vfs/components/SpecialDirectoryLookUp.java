package hu.szbz.vfs.components;

import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.operationhandler.MutableOperationParameter;
import hu.szbz.vfs.operationhandler.OperationParameter;
import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.persistence.model.SpecialDirectoryCode;
import hu.szbz.vfs.persistence.repositories.ActorEntityRepository;
import hu.szbz.vfs.persistence.repositories.ApplicationEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.soap.SpecialDirectoryKeyEnumType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialDirectoryLookUp extends MandatoryDataProvider {
    @Autowired
    public SpecialDirectoryLookUp(ApplicationEntityRepository applicationRepository, ActorEntityRepository actorEntityRepository, FileObjectEntityRepository fileObjectEntityRepository, FileObjectProtection protection) {
        super(applicationRepository, actorEntityRepository, fileObjectEntityRepository, protection);
    }

    public OperationParameter<Void> lookUp(String applicationId, String actorId, SpecialDirectoryKeyEnumType rqKey) throws VirtualFileSystemException {
        var key = convertSpecialDirectoryKey(rqKey);
        var application = findApplication(applicationId);
        var actor = findActor(actorId);
        var optFileObject = fileObjectEntityRepository.findBySpecialDirectory(actor, key);
        if (optFileObject.isEmpty()) throw new VirtualFileSystemException(String.format("Special directory for user was not found. Key: %s", rqKey), ErrorCode.CORRUPTED);
        var fileObject = optFileObject.get();
        var calculatedPermissions = protection.calculateAndCheckPermission(actor, fileObject, Permission.READ);
        MutableOperationParameter<Void> operationParameter = new MutableOperationParameter<>(applicationId, actorId, fileObject.getExternalId(), null);
        operationParameter.setApplication(application);
        operationParameter.setActor(actor);
        operationParameter.setFileObject(fileObject);
        operationParameter.setCalculatedPermissions(calculatedPermissions);
        return operationParameter;
    }

    private SpecialDirectoryCode convertSpecialDirectoryKey(SpecialDirectoryKeyEnumType rq) {
        return switch (rq) {
            case HOME -> SpecialDirectoryCode.HOME;
        };
    }
}
