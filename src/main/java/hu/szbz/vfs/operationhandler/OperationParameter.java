package hu.szbz.vfs.operationhandler;

import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;

public interface OperationParameter<T> {

    ApplicationEntity getApplication();

    ActorEntity getActor();

    FileObjectEntity getFileObject();

    T getRequestBody();

    int getCalculatedPermissions();
}
