package hu.szbz.vfs.errors;

public class VirtualFileSystemException extends Exception {
    private static final long  serialVersionUID = -6168202964544416770L;

    private final ErrorCode errorCode;

    public VirtualFileSystemException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public VirtualFileSystemException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
