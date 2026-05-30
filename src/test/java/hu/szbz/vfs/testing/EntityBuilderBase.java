package hu.szbz.vfs.testing;

import hu.szbz.vfs.persistence.model.EntityBase;

import java.time.LocalDate;
import java.util.Random;

abstract class EntityBuilderBase<T extends EntityBase> {
    static final Random ID_RANDOMIZER = new Random();

    protected abstract T create();

    public final T build() {
        T entity = create();
        entity.setId(ID_RANDOMIZER.nextInt());
        entity.setCreationDateTime(LocalDate.now().minusDays(4).atTime(9, 0, 0));
        entity.setModificationDateTime(LocalDate.now().minusDays(1).atTime(12, 44, 21));
        return entity;
    }
}
