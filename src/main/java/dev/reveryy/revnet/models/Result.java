package dev.reveryy.revnet.models;

import dev.reveryy.revnet.enums.NetError;

public record Result<T>(
        NetError error,
        T value
) {
    public static <T> Result<T> ok(T value) {
        return new Result<>(NetError.OK, value);
    }

    public static <T> Result<T> error(NetError error) {
        return new Result<>(error, null);
    }

    public boolean isOk() {
        return error == NetError.OK;
    }
}
