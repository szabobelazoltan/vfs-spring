package hu.szbz.vfs.operationhandler;

import hu.szbz.vfs.components.FileObjectProtection;
import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.persistence.model.FileObjectType;
import hu.szbz.vfs.persistence.repositories.ActorEntityRepository;
import hu.szbz.vfs.persistence.repositories.ApplicationEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.soap.WsResponseBase;
import hu.szbz.vfs.testing.ActorEntityBuilder;
import hu.szbz.vfs.testing.ApplicationEntityBuilder;
import hu.szbz.vfs.testing.FileObjectEntityBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OperationInvokerTest {
    @Mock
    private ApplicationEntityRepository applicationRepository;

    @Mock
    private ActorEntityRepository actorEntityRepository;

    @Mock
    private FileObjectEntityRepository fileObjectEntityRepository;

    @Mock
    private FileObjectProtection protection;

    @Spy
    private FileObjectOperation<Void, WsResponseBase> operation;

    private OperationInvoker testSubject;

    @BeforeEach
    void setUp() {
        this.testSubject = new OperationInvoker(applicationRepository, actorEntityRepository, fileObjectEntityRepository, protection);
    }

    @Test
    void invoke_callOperation_withoutErrors() throws VirtualFileSystemException {
        var app = new ApplicationEntityBuilder()
                .build();
        when(applicationRepository.findByExternalId(app.getExternalId())).thenReturn(Optional.of(app));

        var actor = new ActorEntityBuilder()
                .build();
        when(actorEntityRepository.findByExternalId(actor.getExternalId())).thenReturn(Optional.of(actor));

        var fileObject = new FileObjectEntityBuilder(FileObjectType.FILE)
                .build();
        when(fileObjectEntityRepository.getByExternalId(fileObject.getExternalId())).thenReturn(fileObject);

        int permissions = 2;
        when(protection.calculateAndCheckPermission(actor, fileObject, Permission.NONE)).thenReturn(permissions);

        var param = new MutableOperationParameter<Void>(app.getExternalId(), actor.getExternalId(), fileObject.getExternalId(), null);
        var rp = new WsResponseBase();
        when(operation.perform(param)).thenReturn(rp);

        WsResponseBase result = testSubject.invoke(operation, Permission.NONE, param, WsResponseBase::new);

        assertEquals(rp, result);
        assertNotNull(result.getHeader());
        assertTrue(result.getHeader().isSuccess());
        assertEquals(app, param.getApplication());
        assertEquals(actor, param.getActor());
        assertEquals(fileObject, param.getFileObject());
    }

    @Test
    void invoke_handlesOperationException() throws VirtualFileSystemException {
        var app = new ApplicationEntityBuilder()
                .build();
        when(applicationRepository.findByExternalId(app.getExternalId())).thenReturn(Optional.of(app));

        var actor = new ActorEntityBuilder()
                .build();
        when(actorEntityRepository.findByExternalId(actor.getExternalId())).thenReturn(Optional.of(actor));

        var fileObject = new FileObjectEntityBuilder(FileObjectType.FILE)
                .build();
        when(fileObjectEntityRepository.getByExternalId(fileObject.getExternalId())).thenReturn(fileObject);

        int permissions = 2;
        when(protection.calculateAndCheckPermission(actor, fileObject, Permission.NONE)).thenReturn(permissions);

        var param = new MutableOperationParameter<Void>(app.getExternalId(), actor.getExternalId(), fileObject.getExternalId(), null);
        var ex = new VirtualFileSystemException("Dummy error", ErrorCode.PERMISSION_DENIED);
        when(operation.perform(param)).thenThrow(ex);

        WsResponseBase result = testSubject.invoke(operation, Permission.NONE, param, WsResponseBase::new);

        assertNotNull(result);
        assertNotNull(result.getHeader());
        assertFalse(result.getHeader().isSuccess());
        assertEquals(ex.getErrorCode().name(), result.getHeader().getErrorCode());
        assertEquals(ex.getMessage(), result.getHeader().getErrorMessage());
    }

    @Test
    void invoke_handlesMissingApplication() throws VirtualFileSystemException {
        String appId = "NotApp";
        when(applicationRepository.findByExternalId(appId)).thenReturn(Optional.empty());

        String actorId = "someActor";
        String fileObjectId = "someFileObject";

        var param = new MutableOperationParameter<Void>(appId, actorId, fileObjectId, null);
        WsResponseBase result = testSubject.invoke(operation, Permission.NONE, param, WsResponseBase::new);

        assertNotNull(result);
        assertNotNull(result.getHeader());
        assertFalse(result.getHeader().isSuccess());
        assertEquals(ErrorCode.UNKNOWN_APPLICATION.name(), result.getHeader().getErrorCode());
        assertEquals("Application is not found with id: NotApp", result.getHeader().getErrorMessage());
    }

    @Test
    void invoke_handlesMissingActor() throws VirtualFileSystemException {
        var app = new ApplicationEntityBuilder()
                .build();
        when(applicationRepository.findByExternalId(app.getExternalId())).thenReturn(Optional.of(app));

        String actorId = "someActor";
        when(actorEntityRepository.findByExternalId(actorId)).thenReturn(Optional.empty());

        String fileObjectId = "someFileObject";

        var param = new MutableOperationParameter<Void>(app.getExternalId(), actorId, fileObjectId, null);
        WsResponseBase result = testSubject.invoke(operation, Permission.NONE, param, WsResponseBase::new);

        assertNotNull(result);
        assertNotNull(result.getHeader());
        assertFalse(result.getHeader().isSuccess());
        assertEquals(ErrorCode.UNKNOWN_ACTOR.name(), result.getHeader().getErrorCode());
        assertEquals("Actor is not found with id: someActor", result.getHeader().getErrorMessage());
    }

    @Test
    void invoke_handlesMissingHomeDirectory() throws VirtualFileSystemException {
        var app = new ApplicationEntityBuilder()
                .build();
        when(applicationRepository.findByExternalId(app.getExternalId())).thenReturn(Optional.of(app));

        var actor = new ActorEntityBuilder()
                .build();
        when(actorEntityRepository.findByExternalId(actor.getExternalId())).thenReturn(Optional.of(actor));

        String fileObjectId = "HOME";
        when(fileObjectEntityRepository.findHome(actor)).thenReturn(Optional.empty());

        var param = new MutableOperationParameter<Void>(app.getExternalId(), actor.getExternalId(), fileObjectId, null);
        WsResponseBase result = testSubject.invoke(operation, Permission.NONE, param, WsResponseBase::new);

        assertNotNull(result);
        assertNotNull(result.getHeader());
        assertFalse(result.getHeader().isSuccess());
        assertEquals(ErrorCode.FILEOBJECT_NOT_EXIST.name(), result.getHeader().getErrorCode());
        assertEquals("Home directory is not found for user: " + actor.getExternalId()  + "!", result.getHeader().getErrorMessage());
    }
}
