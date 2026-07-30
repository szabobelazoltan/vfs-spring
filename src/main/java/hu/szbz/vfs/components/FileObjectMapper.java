package hu.szbz.vfs.components;

import hu.szbz.vfs.operationhandler.Permission;
import hu.szbz.vfs.persistence.model.FileObjectEntity;
import hu.szbz.vfs.persistence.result.FileObjectResultItem;
import hu.szbz.vfs.soap.FileObjectBasicInfo;
import hu.szbz.vfs.soap.FileObjectDetailsInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = { Permission.class }
)
public interface FileObjectMapper {
    @Mapping(target = "id", source = "entity.externalId")
    @Mapping(target = "permissions", expression = "java( Permission.codeToStringList(calculatedPermission) )")
    FileObjectBasicInfo mapToBasicInfo(FileObjectEntity entity, int calculatedPermission);

    @Mapping(target = "id", source = "entity.externalId")
    @Mapping(target = "permissions", expression = "java( Permission.codeToStringList(calculatedPermission) )")
    @Mapping(target = "path.node", source = "path")
    FileObjectDetailsInfo mapToDetailsInfo(FileObjectEntity entity, int calculatedPermission, List<FileObjectEntity> path);

    List<FileObjectBasicInfo> mapList(List<FileObjectEntity> entities);

    @Mapping(target = "id", source = "item.externalId")
    @Mapping(target = "permissions", expression = "java( Permission.codeToStringList(item.permissions()) )")
    FileObjectBasicInfo mapResultItem(FileObjectResultItem item);

    List<FileObjectBasicInfo> mapResultItemList(List<FileObjectResultItem> items);
}
