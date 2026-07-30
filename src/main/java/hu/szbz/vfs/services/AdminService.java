package hu.szbz.vfs.services;

import hu.szbz.vfs.components.AdministrativeDataMapper;
import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.messages.PublishUserRegistrationDg;
import hu.szbz.vfs.messages.RegisterVfsClientDg;
import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.persistence.model.AccessEntity;
import hu.szbz.vfs.persistence.model.AccessRole;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.repositories.AccessEntityRepository;
import hu.szbz.vfs.persistence.repositories.ActorEntityRepository;
import hu.szbz.vfs.persistence.repositories.ApplicationEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private static final Permission[] HOMEDIR_PERMISSIONS = {
            Permission.READ,
            Permission.EXECUTE,
            Permission.WRITE
    };

    private final ApplicationEntityRepository applicationRepository;
    private final ActorEntityRepository actorRepository;
    private final FileObjectEntityRepository fileObjectRepository;
    private final AccessEntityRepository accessRepository;
    private final AdministrativeDataMapper mapper;

    @Autowired
    public AdminService(ApplicationEntityRepository applicationRepository, ActorEntityRepository actorRepository, FileObjectEntityRepository fileObjectRepository, AccessEntityRepository accessRepository, AdministrativeDataMapper mapper) {
        this.applicationRepository = applicationRepository;
        this.actorRepository = actorRepository;
        this.fileObjectRepository = fileObjectRepository;
        this.accessRepository = accessRepository;
        this.mapper = mapper;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void registerClient(RegisterVfsClientDg dg) {
        ApplicationEntity entity = mapper.mapApplicationDataToEntity(dg.getBody().getApplicationData());
        applicationRepository.save(entity);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void registerUser(PublishUserRegistrationDg dg) throws VirtualFileSystemException {
        Optional<ApplicationEntity> vfsApp = applicationRepository.findByExternalId(ApplicationEntityRepository.APPID_VFS);
        if (vfsApp.isEmpty()) throw new VirtualFileSystemException("Application entry is not found for VFS service", ErrorCode.CORRUPTED);

        ActorEntity actorEntity = mapper.mapActorToEntity(dg.getBody().getUserData());
        actorEntity = actorRepository.save(actorEntity);

        FileObjectEntity homeDir = FileObjectEntity.createNewDirectory(dg.getBody().getUserData().getName(), vfsApp.get(), null);
        homeDir = fileObjectRepository.save(homeDir);

        accessRepository.save(AccessEntity.create(actorEntity, homeDir, AccessRole.OWNER, Permission.vectorToCode(HOMEDIR_PERMISSIONS)));
    }
}
