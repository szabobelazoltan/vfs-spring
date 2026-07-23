package hu.szbz.vfs.services;

import hu.szbz.vfs.components.AdministrativeDataMapperImpl;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.messages.PublishUserRegistrationDg;
import hu.szbz.vfs.messages.PublishUserRegistrationDgBody;
import hu.szbz.vfs.messages.RegisterVfsClientDg;
import hu.szbz.vfs.messages.RegisterVfsClientDgBody;
import hu.szbz.vfs.persistence.model.*;
import hu.szbz.vfs.persistence.repositories.AccessEntityRepository;
import hu.szbz.vfs.persistence.repositories.ActorEntityRepository;
import hu.szbz.vfs.persistence.repositories.ApplicationEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AdministrativeDataMapperImpl.class, AdminService.class })
public class AdminServiceTest {
    @MockitoBean
    private ApplicationEntityRepository applicationRepository;

    @MockitoBean
    private ActorEntityRepository actorRepository;

    @MockitoBean
    private FileObjectEntityRepository fileObjectRepository;

    @MockitoBean
    private AccessEntityRepository accessRepository;

    @Autowired
    private AdminService adminService;

    @Test
    void registerClient() {
        RegisterVfsClientDg dg = new RegisterVfsClientDg();
        dg.setBody(new RegisterVfsClientDgBody());
        dg.getBody().setApplicationData(new RegisterVfsClientDgBody.ApplicationData());
        dg.getBody().getApplicationData().setId("MYAPP");
        dg.getBody().getApplicationData().setName("My first application");

        ArgumentCaptor<ApplicationEntity> applicationEntityCaptor = ArgumentCaptor.forClass(ApplicationEntity.class);
        when(applicationRepository.save(applicationEntityCaptor.capture())).thenReturn(null);

        adminService.registerClient(dg);

        ApplicationEntity entity = applicationEntityCaptor.getValue();
        assertEquals(dg.getBody().getApplicationData().getId(), entity.getExternalId());
        assertEquals(dg.getBody().getApplicationData().getName(), entity.getName());
    }

    @Test
    void registerUserCreatesEntities() throws VirtualFileSystemException {
        PublishUserRegistrationDg dg = new PublishUserRegistrationDg();
        dg.setBody(new PublishUserRegistrationDgBody());
        dg.getBody().setUserData(new PublishUserRegistrationDgBody.UserData());
        dg.getBody().getUserData().setId("john.doe");
        dg.getBody().getUserData().setName("John Doe");

        ArgumentCaptor<ActorEntity> actorCaptor = ArgumentCaptor.forClass(ActorEntity.class);
        when(actorRepository.save(actorCaptor.capture())).thenAnswer(i -> i.getArguments()[0]);

        ArgumentCaptor<FileObjectEntity> fileCaptor = ArgumentCaptor.forClass(FileObjectEntity.class);
        when(fileObjectRepository.save(fileCaptor.capture())).thenAnswer(i -> i.getArguments()[0]);

        ArgumentCaptor<AccessEntity> accessCaptor = ArgumentCaptor.forClass(AccessEntity.class);
        when(accessRepository.save(accessCaptor.capture())).thenReturn(null);

        ApplicationEntity vfsApp = new ApplicationEntity();
        vfsApp.setId(1);
        vfsApp.setExternalId(ApplicationEntityRepository.APPID_VFS);
        when(applicationRepository.findByExternalId(ApplicationEntityRepository.APPID_VFS)).thenReturn(Optional.of(vfsApp));

        adminService.registerUser(dg);

        ActorEntity actor = actorCaptor.getValue();
        assertEquals(dg.getBody().getUserData().getId(), actor.getExternalId());

        FileObjectEntity fileObject = fileCaptor.getValue();
        assertEquals(dg.getBody().getUserData().getName(), fileObject.getName());
        assertEquals(vfsApp, fileObject.getApplication());
        assertNull(fileObject.getParent());

        AccessEntity access = accessCaptor.getValue();
        assertEquals(actor, access.getActor());
        assertEquals(fileObject, access.getFileObject());
        assertEquals(AccessRole.OWNER, access.getRole());
        assertEquals(7, access.getPermissions());
    }
}
