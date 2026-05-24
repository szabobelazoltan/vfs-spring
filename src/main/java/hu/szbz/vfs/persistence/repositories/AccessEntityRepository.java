package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.AccessEntity;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessEntityRepository extends CrudRepository<AccessEntity, Long> {

    Optional<AccessEntity> findByActorAndFileObject(ActorEntity actor, FileObjectEntity fileObject);
}
