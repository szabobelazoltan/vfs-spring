package hu.szbz.vfs.persistence.result;

import hu.szbz.vfs.persistence.model.FileObjectStatus;
import hu.szbz.vfs.persistence.model.FileObjectType;

import java.time.LocalDateTime;

public record FileObjectResultItem(
        long id,
        String externalId,
        FileObjectType type,
        LocalDateTime creationDateTime,
        LocalDateTime modificationDateTime,
        FileObjectStatus status,
        String name,
        LocalDateTime deletionDateTime,
        String contentReference,
        int permissions
) {
}
