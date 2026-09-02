package vip.wgzz.blog.common;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.exception.ExceptionEnum;

/**
 * @author wgzz
 * @date 2026/8/2 17:13
 * @description 统一返回对象
 */
@Data
public class RespResult {
    /**
     * 响应代码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private Object data;

    public RespResult() {
    }

    /**
     * 指定异常
     *
     * @param exceptionEnum 异常枚举
     */
    public RespResult(ExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
    }


    /**
     * 成功空数据
     */
    public static RespResult success() {
        return success(null);
    }

    /**
     * 成功有数据
     */
    public static RespResult success(Object data) {
        RespResult result = new RespResult(ExceptionEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 失败
     */
    public static RespResult error(ExceptionEnum exceptionEnum) {
        RespResult result = new RespResult(exceptionEnum);
        result.setData(null);
        return result;
    }

    /**
     * 失败
     */
    public static RespResult error(Integer code, String msg) {
        RespResult result = new RespResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /**
     * 失败
     */
    public static RespResult error(BaseException e) {
        RespResult result = new RespResult();
        result.setCode(e.getErrorCode());
        result.setMsg(e.getErrorMsg());
        return result;
    }

    /**
     * 失败
     */
    public static RespResult error(String message) {
        RespResult result = new RespResult();
        result.setCode(ExceptionEnum.REQUEST_ERROR.getCode());
        result.setMsg(message);
        result.setData(null);
        return result;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

}
