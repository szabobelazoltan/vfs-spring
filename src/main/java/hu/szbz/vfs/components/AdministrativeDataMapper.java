package hu.szbz.vfs.components;

import hu.szbz.vfs.messages.PublishUserRegistrationDgBody;
import hu.szbz.vfs.messages.RegisterVfsClientDgBody;
import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.model.ApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdministrativeDataMapper {
    @Mapping(target = "externalId", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "id", ignore = true)
    ApplicationEntity mapApplicationDataToEntity(RegisterVfsClientDgBody.ApplicationData data);

    @Mapping(target = "externalId", source = "id")
    @Mapping(target = "id", ignore = true)
    ActorEntity mapActorToEntity(PublishUserRegistrationDgBody.UserData userData);
}
