package hu.szbz.vfs.operationhandler;

import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.soap.WsRequestBase;

public class MutableOperationParameter<T> implements OperationParameter<T> {
    private final String applicationId;
    private final String actorId;
    private final String fileObjectId;
    private final T requestBody;

    private ApplicationEntity application;
    private ActorEntity actor;
    private FileObjectEntity fileObject = null;
    private int calculatedPermissions = 0;

    public MutableOperationParameter(String applicationId, String actorId, String fileObjectId, T requestBody) {
        this.applicationId = applicationId;
        this.actorId = actorId;
        this.fileObjectId = fileObjectId;
        this.requestBody = requestBody;
    }

    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }

    public void setActor(ActorEntity actor) {
        this.actor = actor;
    }

    public void setFileObject(FileObjectEntity fileObject) {
        this.fileObject = fileObject;
    }

    public void setCalculatedPermissions(int calculatedPermissions) {
        this.calculatedPermissions = calculatedPermissions;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getActorId() {
        return actorId;
    }

    public String getFileObjectId() {
        return fileObjectId;
    }

    @Override
    public ApplicationEntity getApplication() {
        if (this.application == null) throw new IllegalStateException("Application is not set!");
        return this.application;
    }

    @Override
    public ActorEntity getActor() {
        if (this.actor == null) throw new IllegalStateException("Actor is not set!");
        return this.actor;
    }

    @Override
    public FileObjectEntity getFileObject() {
        if (this.actor == null) throw new IllegalStateException("File object is not set!");
        return this.fileObject;
    }

    @Override
    public T getRequestBody() {
        return this.requestBody;
    }

    @Override
    public int getCalculatedPermissions() {
        return this.calculatedPermissions;
    }

    public static <T> MutableOperationParameter<T> fromRequest(WsRequestBase rq, T body) {
        return new MutableOperationParameter<>(
                rq.getHeader().getApplicationId(),
                rq.getHeader().getActorId(),
                rq.getHeader().getFileObjectId(),
                body
        );
    }
}
