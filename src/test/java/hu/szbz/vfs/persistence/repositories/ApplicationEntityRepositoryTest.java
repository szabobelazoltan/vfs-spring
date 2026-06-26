package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.ApplicationEntity;
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
@Sql("/sql/ApplicationEntityRepositoryTest.sql")
public class ApplicationEntityRepositoryTest {
    @Autowired
    private ApplicationEntityRepository repository;

    @Test
    void findByExternalIdReturnsEntity() {
        String extId = "MYAPP";
        Optional<ApplicationEntity> result = repository.findByExternalId(extId);

        assertTrue(result.isPresent());
    }
}
