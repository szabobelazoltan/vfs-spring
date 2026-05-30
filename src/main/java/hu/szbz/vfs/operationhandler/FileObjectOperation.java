package hu.szbz.vfs.operationhandler;

import hu.szbz.vfs.errors.VirtualFileSystemException;
import hu.szbz.vfs.soap.WsResponseBase;

public interface FileObjectOperation<T, R extends WsResponseBase> {

    R perform(OperationParameter<T> parameter) throws VirtualFileSystemException;
}
