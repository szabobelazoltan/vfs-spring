package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.ActorEntity;
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
@Sql("/sql/ActorEntityRepositoryTest.sql")
public class ActorEntityRepositoryTest {
    @Autowired
    private ActorEntityRepository repository;

    @Test
    void findByExternalIdReturnsEntity() {
        String extId = "actor2";
        Optional<ActorEntity> result = repository.findByExternalId(extId);

        assertTrue(result.isPresent());
    }
}
