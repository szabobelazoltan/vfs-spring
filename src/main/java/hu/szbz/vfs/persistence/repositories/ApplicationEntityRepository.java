package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.ApplicationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationEntityRepository extends CrudRepository<ApplicationEntity, Long> {

    Optional<ApplicationEntity> findByExternalId(String externalId);
}
