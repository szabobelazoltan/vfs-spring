package hu.szbz.vfs.testing;

import hu.szbz.vfs.persistence.model.ActorEntity;

public class ActorEntityBuilder extends EntityBuilderBase<ActorEntity> {
    private String id = "ACT_01";

    public ActorEntityBuilder withId(String id) {
        this.id = id;
        return this;
    }

    @Override
    protected ActorEntity create() {
        var entity = new ActorEntity();
        entity.setExternalId(this.id);
        return entity;
    }
}
