package hu.szbz.vfs.testing;

import hu.szbz.vfs.persistence.model.ApplicationEntity;

public class ApplicationEntityBuilder extends EntityBuilderBase<ApplicationEntity> {
    private String id = "MAPID";
    private String name = "My App";

    public ApplicationEntityBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ApplicationEntityBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    protected ApplicationEntity create() {
        var entity = new ApplicationEntity();
        entity.setExternalId(this.id);
        entity.setName(this.name);
        return entity;
    }
}
