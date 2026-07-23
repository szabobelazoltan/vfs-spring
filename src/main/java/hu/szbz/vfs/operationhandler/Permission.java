package hu.szbz.vfs.operationhandler;

public enum Permission {
    NONE(0, false),
    READ(1, false),
    EXECUTE(2, false),
    WRITE(4, false),
    DELETE(8, true),
    RENAME(16, false),
    MOVE(32, false),
    SHARE(64, false)
    ;

    private final int code;
    private final boolean aggregationNeeded;

    Permission(int code, boolean aggregationNeeded) {
        this.code = code;
        this.aggregationNeeded = aggregationNeeded;
    }

    public int getCode() {
        return code;
    }

    public boolean isAggregationNeeded() {
        return aggregationNeeded;
    }

    public boolean isPresent(int input) {
        return (input & this.code) == this.code;
    }

    public static int vectorToCode(Permission... permissions) {
        int result = 0;
        for (Permission permission : permissions) result |= permission.code;
        return result;
    }
}
