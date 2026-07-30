package hu.szbz.vfs.services;

import hu.szbz.vfs.components.FileObjectMapper;
import hu.szbz.vfs.components.FileObjectProtection;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.operationhandler.OperationParameter;
import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.soap.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManipulationService {
    private final FileObjectEntityRepository fileObjectEntityRepository;
    private final FileObjectMapper mapper;
    private final FileObjectProtection protection;

    @Autowired
    public ManipulationService(FileObjectEntityRepository fileObjectEntityRepository, FileObjectMapper mapper, FileObjectProtection protection) {
        this.fileObjectEntityRepository = fileObjectEntityRepository;
        this.mapper = mapper;
        this.protection = protection;
    }

    @Transactional
    public RenameFileObjectResponseType rename(OperationParameter<RenameFileObjectRequestBody> parameter) {
        var fileObject = parameter.getFileObject();
        fileObject.setName(parameter.getRequestBody().getName());
        var basicInfo = mapper.mapToBasicInfo(fileObject, parameter.getCalculatedPermissions());
        var rp = new RenameFileObjectResponseType();
        rp.setBody(new RenameFileObjectResponseBody());
        rp.getBody().setFileObjectBasicInfo(basicInfo);
        return rp;
    }

    @Transactional
    public MoveFileObjectResponseType move(OperationParameter<MoveFileObjectRequestBody> parameter) throws VirtualFileSystemException {
        var parent = fileObjectEntityRepository.getByExternalId(parameter.getRequestBody().getTargetId());
        protection.calculateAndCheckPermission(parameter.getActor(), parent, Permission.WRITE);
        var fileObject = parameter.getFileObject();
        fileObject.setParent(parent);
        var basicInfo = mapper.mapToBasicInfo(fileObject, parameter.getCalculatedPermissions());
        var rp = new MoveFileObjectResponseType();
        rp.setBody(new MoveFileObjectResponseBody());
        rp.getBody().setFileObjectBasicInfo(basicInfo);
        return rp;
    }
}
