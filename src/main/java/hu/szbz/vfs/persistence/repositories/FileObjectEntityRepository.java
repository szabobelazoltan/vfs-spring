package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.model.FileObjectType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FileObjectEntityRepository extends CrudRepository<FileObjectEntity, Long>, FileObjectEntityLister {

    Optional<FileObjectEntity> findByExternalId(String externalId);

    List<FileObjectEntity> findAllByParentAndTypeIn(FileObjectEntity parent, Set<FileObjectType> types);
}
