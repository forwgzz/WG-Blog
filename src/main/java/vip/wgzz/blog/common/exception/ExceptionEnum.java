package vip.wgzz.blog.common.exception;

import lombok.Getter;

/**
 * 异常枚举
 * @author wgzz
 * @date 2026/8/2 17:10
 * @description
 */
@Getter
public enum ExceptionEnum {

    SUCCESS(200, "成功!"),
    REQUEST_ERROR(400,"请求错误"),
    NOT_FOUND(404, "未找到该资源!"),
    INNER_ERROR(500, "服务器内部错误!"),

    ;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误描述
     */
    private final String msg;

    ExceptionEnum(Integer code, String resultMsg) {
        this.code = code;
        this.msg = resultMsg;
    }
}