package hu.szbz.vfs.components;

import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.persistence.model.*;
import hu.szbz.vfs.persistence.repositories.AccessEntityRepository;
import hu.szbz.vfs.persistence.repositories.FileObjectEntityRepository;
import hu.szbz.vfs.testing.ActorEntityBuilder;
import hu.szbz.vfs.testing.FileObjectEntityBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        TreeTraversal.class,
        FileObjectProtection.class
})
public class FileObjectProtectionTest {
    @MockitoBean
    private FileObjectEntityRepository fileObjectEntityRepository;

    @MockitoBean
    private AccessEntityRepository accessEntityRepository;

    @Autowired
    private TreeTraversal treeTraversal;

    @Autowired
    private FileObjectProtection testSubject;

    @Test
    void calculateAndCheckPermission_returnsValue_ofAggregation() throws VirtualFileSystemException {
        var actor = new ActorEntityBuilder()
                .build();

        var root = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .build();
        var sub = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .withParent(root)
                .build();
        when(fileObjectEntityRepository.findAllByParentAndTypeIn(eq(root), anySet())).thenReturn(List.of(sub));
        var leaf = new FileObjectEntityBuilder(FileObjectType.FILE)
                .withParent(sub)
                .build();
        when(fileObjectEntityRepository.findAllByParentAndTypeIn(eq(sub), anySet())).thenReturn(List.of(leaf));

        var rootAccess = createAccess(1L, actor, root, Byte.MAX_VALUE);
        when(accessEntityRepository.findByActorAndFileObject(actor, root)).thenReturn(Optional.of(rootAccess));

        var subAccess = createAccess(2L, actor, root, Byte.MAX_VALUE);
        when(accessEntityRepository.findByActorAndFileObject(actor, sub)).thenReturn(Optional.of(subAccess));

        var leafAccess = createAccess(3L, actor, root, 27);
        when(accessEntityRepository.findByActorAndFileObject(actor, leaf)).thenReturn(Optional.of(leafAccess));

        int result = testSubject.calculateAndCheckPermission(actor, root, Permission.DELETE);

        assertEquals(leafAccess.getPermissions(), result);
    }

    @Test
    void calculateAndCheckPermission_throwsException_afterMissingAccess() throws VirtualFileSystemException {
        var actor = new ActorEntityBuilder()
                .build();

        var root = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .build();
        var sub = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .withParent(root)
                .build();
        when(fileObjectEntityRepository.findAllByParentAndTypeIn(eq(root), anySet())).thenReturn(List.of(sub));

        var rootAccess = createAccess(1L, actor, root, Byte.MAX_VALUE);
        when(accessEntityRepository.findByActorAndFileObject(actor, root)).thenReturn(Optional.of(rootAccess));

        when(accessEntityRepository.findByActorAndFileObject(actor, sub)).thenReturn(Optional.empty());

        var ex = assertThrows(VirtualFileSystemException.class, () -> testSubject.calculateAndCheckPermission(actor, root, Permission.DELETE));
        assertEquals(ErrorCode.PERMISSION_DENIED, ex.getErrorCode());
        assertEquals("Actor does not have permission to perform the requested action on the file object!", ex.getMessage());
    }

    @Test
    void calculateAndCheckPermission_returnsValue_ofFileObjectAccess() throws VirtualFileSystemException {
        var actor = new ActorEntityBuilder()
                .build();

        var fileObject = new FileObjectEntityBuilder(FileObjectType.DIRECTORY)
                .build();

        var access = createAccess(1L, actor, fileObject, Byte.MAX_VALUE);
        when(accessEntityRepository.findByActorAndFileObject(actor, fileObject)).thenReturn(Optional.of(access));

        int result = testSubject.calculateAndCheckPermission(actor, fileObject, Permission.READ);

        assertEquals(access.getPermissions(), result);
    }

    private AccessEntity createAccess(long id, ActorEntity actor, FileObjectEntity fileObject, int permissions) {
        var entity = new AccessEntity();
        entity.setId(id);
        entity.setActor(actor);
        entity.setFileObject(fileObject);
        entity.setRole(AccessRole.OWNER);
        entity.setPermissions(permissions);
        return entity;
    }
}
