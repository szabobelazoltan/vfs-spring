package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.ActorEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorEntityRepository extends CrudRepository<ActorEntity, Long> {

    Optional<ActorEntity> findByExternalId(String externalId);
}
