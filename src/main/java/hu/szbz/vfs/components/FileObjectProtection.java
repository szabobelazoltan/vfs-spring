package hu.szbz.vfs.components;

import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.persistence.model.AccessEntity;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.repositories.AccessEntityRepository;
import org.springframework.stereotype.Component;

@Component
public class FileObjectProtection {
    private final AccessEntityRepository accessEntityRepository;
    private final TreeTraversal treeTraversal;

    public FileObjectProtection(AccessEntityRepository accessEntityRepository, TreeTraversal treeTraversal) {
        this.accessEntityRepository = accessEntityRepository;
        this.treeTraversal = treeTraversal;
    }

    public int calculateAndCheckPermission(ActorEntity actor, FileObjectEntity fileObject, Permission permission) throws VirtualFileSystemException {
        int calculatedPermission = 0;
        if (permission.isAggregationNeeded()) {
            var processor = new PermissionAggregator(actor);
            calculatedPermission = treeTraversal.traverseDown(fileObject, processor);
        } else {
            calculatedPermission = accessEntityRepository.findByActorAndFileObject(actor, fileObject)
                    .map(AccessEntity::getPermissions)
                    .orElse(Permission.NONE.getCode());
        }
        if (!permission.isPresent(calculatedPermission)) {
            throw new VirtualFileSystemException("Actor does not have permission to perform the requested action on the file object!", ErrorCode.PERMISSION_DENIED);
        }
        return calculatedPermission;
    }

    private class PermissionAggregator implements TreeTraversal.TreeTraversalProcessor<Integer> {
        private final ActorEntity actor;

        public PermissionAggregator(ActorEntity actor) {
            this.actor = actor;
        }

        @Override
        public Integer prepareResult() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Integer processNode(FileObjectEntity node, Integer oldResultValue) {
            return accessEntityRepository.findByActorAndFileObject(actor, node)
                    .map(AccessEntity::getPermissions)
                    .orElse(Permission.NONE.getCode());
        }
    }
}
