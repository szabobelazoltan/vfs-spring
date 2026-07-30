package hu.szbz.vfs.operationhandler;

import hu.szbz.vfs.components.FileObjectProtection;
import hu.szbz.vfs.components.MandatoryDataProvider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class OperationInvoker extends MandatoryDataProvider {
    @Autowired
    public OperationInvoker(ApplicationEntityRepository applicationRepository, ActorEntityRepository actorEntityRepository, FileObjectEntityRepository fileObjectEntityRepository, FileObjectProtection protection) {
        super(applicationRepository, actorEntityRepository, fileObjectEntityRepository, protection);
    }

    public <T, R extends WsResponseBase> R invoke(
            FileObjectOperation<T, R> operation,
            Permission requiredPermission,
            MutableOperationParameter<T> operationParameter,
            Supplier<R> responseFactory) {
        try {
            var application = findApplication(operationParameter.getApplicationId());
            var actor = findActor(operationParameter.getActorId());
            var fileObject = fileObjectEntityRepository.getByExternalId(operationParameter.getFileObjectId());
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
