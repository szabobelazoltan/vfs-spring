package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.AccessEntity;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/AccessEntityRepositoryTest.sql")
public class AccessEntityRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private AccessEntityRepository accessEntityRepository;

    @Test
    void findByActorAndFileObjectReturnAccess() {
        ActorEntity actor = em.find(ActorEntity.class, 1);
        FileObjectEntity fo = em.find(FileObjectEntity.class, 1);

        Optional<AccessEntity> result = accessEntityRepository.findByActorAndFileObject(actor, fo);

        assertTrue(result.isPresent());
    }
}
