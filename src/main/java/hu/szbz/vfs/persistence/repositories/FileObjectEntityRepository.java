package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.errors.ErrorCode;
import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectType;
import hu.szbz.vfs.persistence.model.SpecialDirectoryCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FileObjectEntityRepository extends CrudRepository<FileObjectEntity, Long>, FileObjectEntityLister {

    Optional<FileObjectEntity> findByExternalId(String externalId);

    List<FileObjectEntity> findAllByParentAndTypeIn(FileObjectEntity parent, Set<FileObjectType> types);

    default FileObjectEntity getByExternalId(String fileObjectId) throws VirtualFileSystemException {
        var fileObject = findByExternalId(fileObjectId);
        if (fileObject.isEmpty()) throw new VirtualFileSystemException(String.format("File object is not found with id: %s", fileObjectId), ErrorCode.FILEOBJECT_NOT_EXIST);
        return fileObject.get();
    }

    @Query("SELECT a.fileObject FROM AccessEntity a WHERE a.actor = :actor AND a.role = hu.szbz.vfs.persistence.model.AccessRole.OWNER AND a.fileObject.specialDirectoryCode = :specialDirectoryCode")
    Optional<FileObjectEntity> findBySpecialDirectory(ActorEntity actor, SpecialDirectoryCode specialDirectoryCode);
}
