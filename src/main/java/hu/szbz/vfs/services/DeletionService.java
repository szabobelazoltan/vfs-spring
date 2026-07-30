package hu.szbz.vfs.services;

import hu.szbz.vfs.components.TreeTraversal;
import hu.szbz.vfs.messages.DeletedFileObjectDTO;
import hu.szbz.vfs.messages.MessageHeader;
import hu.szbz.vfs.messages.PublishFileObjectDeletionDg;
import hu.szbz.vfs.messages.PublishFileObjectDeletionDgBody;
import hu.szbz.vfs.operationhandler.OperationParameter;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectStatus;
import hu.szbz.vfs.persistence.repositories.AccessEntityRepository;
import hu.szbz.vfs.persistence.repositories.ApplicationEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.soap.DeleteFileObjectRequestBody;
import hu.szbz.vfs.soap.DeleteFileObjectResponseBody;
import hu.szbz.vfs.soap.DeleteFileObjectResponseType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DeletionService {
    private final TreeTraversal treeTraversal;
    private final FileObjectEntityRepository fileObjectEntityRepository;
    private final AccessEntityRepository accessEntityRepository;

    @Autowired
    public DeletionService(TreeTraversal treeTraversal, FileObjectEntityRepository fileObjectEntityRepository, AccessEntityRepository accessEntityRepository) {
        this.treeTraversal = treeTraversal;
        this.fileObjectEntityRepository = fileObjectEntityRepository;
        this.accessEntityRepository = accessEntityRepository;
    }

    @Transactional
    public DeleteFileObjectResponseType delete(OperationParameter<DeleteFileObjectRequestBody> parameter) {
        List<FileObjectEntity> relatedFileObjects = collectAllRelatedFileObjects(parameter.getFileObject());
        notifyClients(relatedFileObjects);
        relatedFileObjects.forEach(fo -> fo.setStatus(FileObjectStatus.REMOVED));

        var rp = new DeleteFileObjectResponseType();
        rp.setBody(new DeleteFileObjectResponseBody());
        return rp;
    }

    private List<FileObjectEntity> collectAllRelatedFileObjects(FileObjectEntity requestedFileObject) {
        List<FileObjectEntity> relatedFileObjects = new LinkedList<>();
        if (requestedFileObject.isDirectory()) {
            relatedFileObjects.addAll(treeTraversal.traverseDown(requestedFileObject, new TreeTraversal.SubTreeCollector()));
        }
        return relatedFileObjects;
    }

    private void notifyClients(List<FileObjectEntity> relatedFileObjects) {
        var dg = new PublishFileObjectDeletionDg();
        dg.setHeader(new MessageHeader());
        dg.getHeader().setApplicationId(ApplicationEntityRepository.APPID_VFS);
        dg.setBody(new PublishFileObjectDeletionDgBody());
        dg.getBody().setFileObjects(new PublishFileObjectDeletionDgBody.FileObjects());
        dg.getBody().getFileObjects().getDeletedFileObject().addAll(relatedFileObjects.stream()
                .filter(fo -> fo.isFile() && !ApplicationEntityRepository.APPID_VFS.equals(fo.getApplication().getExternalId()))
                .map(this::mapFileObject)
                .toList());
        // TODO: send DG through MQ
    }

    private DeletedFileObjectDTO mapFileObject(FileObjectEntity entity) {
        var dto = new DeletedFileObjectDTO();
        dto.setId(entity.getExternalId());
        dto.setCreatorApplicationId(entity.getApplication().getExternalId());
        return dto;
    }
}
