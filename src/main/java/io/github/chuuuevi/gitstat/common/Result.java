package io.github.chuuuevi.gitstat.common;

/**
 *
 * @param code      错误码
 * @param message   错误消息
 * @param data      响应数据
 * @param <T>       响应类型
 */
public record Result<T>(long code, String message, T data) {

    public boolean isSuccess() {
        return this.code == Code.SUCCESS;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Code.SUCCESS, null, data);
    }

    public static <T> Result<T> failure(long code) {
        return new Result<>(code, null, null);
    }

    public static <T> Result<T> failure(long code, String message) {
        return new Result<>(code, message, null);
    }

}
