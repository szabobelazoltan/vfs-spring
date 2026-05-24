package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.ActorEntity;
import hu.szbz.vfs.persistence.parameters.FileObjectFilter;
import hu.szbz.vfs.persistence.parameters.FileObjectSort;
import hu.szbz.vfs.persistence.result.FileObjectResultItem;
import hu.szbz.vfs.persistence.result.ResultPage;

import java.util.Collection;

public interface FileObjectEntityLister {

    ResultPage<FileObjectResultItem> listByFilters(
            ActorEntity actor,
            int pageSize,
            int pageIndex,
            Collection<FileObjectFilter<?>> filters,
            Collection<FileObjectSort> sort);
}
