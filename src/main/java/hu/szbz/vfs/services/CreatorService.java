package hu.szbz.vfs.services;

import hu.szbz.vfs.components.FileObjectMapper;
import hu.szbz.vfs.operationhandler.OperationParameter;
import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.persistence.model.AccessEntity;
import hu.szbz.vfs.persistence.model.AccessRole;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.repositories.AccessEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.soap.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreatorService {
    private static final Permission[] OWNER_PERMISSIONS = {
            Permission.READ,
            Permission.EXECUTE,
            Permission.WRITE,
            Permission.RENAME,
            Permission.MOVE,
            Permission.DELETE,
            Permission.SHARE
    };

    private final FileObjectMapper mapper;
    private final FileObjectEntityRepository fileObjectEntityRepository;
    private final AccessEntityRepository accessEntityRepository;

    @Autowired
    public CreatorService(FileObjectMapper mapper, FileObjectEntityRepository fileObjectEntityRepository, AccessEntityRepository accessEntityRepository) {
        this.mapper = mapper;
        this.fileObjectEntityRepository = fileObjectEntityRepository;
        this.accessEntityRepository = accessEntityRepository;
    }

    @Transactional
    public CreateDirResponseType createDir(OperationParameter<CreateDirRequestBody> parameter) {
        var basicInfo = createAndStoreFileObject(parameter.getApplication(), parameter.getActor(), parameter.getFileObject(), parameter.getRequestBody().getName());
        var rp = new CreateDirResponseType();
        rp.setBody(new CreateDirResponseBody());
        rp.getBody().setFileObjectBasicInfo(basicInfo);
        return rp;
    }

    @Transactional
    public CreateFileResponseType createFile(OperationParameter<CreateFileRequestBody> parameter) {
        var basicInfo = createAndStoreFileObject(parameter.getApplication(), parameter.getActor(), parameter.getFileObject(), parameter.getRequestBody().getName());
        var rp = new CreateFileResponseType();
        rp.setBody(new CreateFileResponseBody());
        rp.getBody().setFileObjectBasicInfo(basicInfo);
        return rp;
    }

    private FileObjectBasicInfo createAndStoreFileObject(
            ApplicationEntity application,
            ActorEntity actor,
            FileObjectEntity parent,
            String name) {
        var fileObject = fileObjectEntityRepository.save(FileObjectEntity.createNewDirectory(name, application, parent));
        var access = accessEntityRepository.save(AccessEntity.create(actor, fileObject, AccessRole.OWNER, Permission.vectorToCode(OWNER_PERMISSIONS)));
        return mapper.mapToBasicInfo(fileObject, access.getPermissions());
    }
}
