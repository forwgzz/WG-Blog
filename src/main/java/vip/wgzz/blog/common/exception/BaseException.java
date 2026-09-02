package vip.wgzz.blog.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wgzz
 * @date 2026/8/2 17:09
 * @description 自定义异常类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    /**
     * 错误码
     */
    protected Integer errorCode = ExceptionEnum.REQUEST_ERROR.getCode();

    /**
     * 错误信息
     */
    protected String errorMsg;

    /**
     * 异常枚举
     */
    protected ExceptionEnum errorEnum;


    public BaseException() {
        super();
    }

    /**
     * 异常枚举构造
     *
     * @param exceptionEnum 错误枚举
     */
    public BaseException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getCode() + ":" + exceptionEnum.getMsg());
        this.errorEnum = exceptionEnum;
        this.errorCode = exceptionEnum.getCode();
        this.errorMsg = exceptionEnum.getMsg();
    }

    /**
     * 异常枚举构造+实际异常
     *
     * @param exceptionEnum 错误枚举
     * @param cause         异常 cause
     */
    public BaseException(ExceptionEnum exceptionEnum, Throwable cause) {
        super(exceptionEnum.getCode() + ":" + exceptionEnum.getMsg(), cause);
        this.errorCode = exceptionEnum.getCode();
        this.errorMsg = exceptionEnum.getMsg() + ":" + cause.getMessage();
    }

    /**
     * 异常描述+实际异常
     *
     * @param errorMsg 错误信息
     * @param cause    异常 cause
     */
    public BaseException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorMsg = errorMsg + ":" + cause.getMessage();
    }

    /**
     * 异常描述
     *
     * @param errorMsg 错误信息
     */
    public BaseException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    /**
     * 错误码+错误描述
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     */
    public BaseException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 支持格式化异常描述
     *
     * @param format 错误信息
     * @param key    错误信息参数
     * @return BaseException
     */
    public static BaseException format(String format, String... key) {
        return new BaseException(StrUtil.format(format, key));
    }

    /**
     * @return 404异常
     */
    public static BaseException notFind() {
        return new BaseException(ExceptionEnum.NOT_FOUND);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}