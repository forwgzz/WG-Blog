package vip.wgzz.blog.common.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vip.wgzz.blog.common.BaseConstants;
import vip.wgzz.blog.common.RespResult;
import vip.wgzz.blog.model.bo.ErrorTemp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * @author wgzz
 * @date 2026/8/2 17:11
 * @description 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 资源不存在
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Object noReExceptionHandler(HttpServletRequest request, HttpServletResponse response, NoResourceFoundException e) {
        log.error("[{}]未找到该资源：{}", request.getRequestURI(), e.getMessage(), e);
        return buildModelAndView(ExceptionEnum.NOT_FOUND, request, response);
    }


    /**
     * 拦截路径参数 字符串转数字 异常
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     * @return String
     */
    @ExceptionHandler(TypeMismatchException.class)
    public Object typeMismatchException(HttpServletRequest request, HttpServletResponse response, TypeMismatchException e) {
        log.error("类型转换异常" + e.getMessage());
        // 直接404
        return buildModelAndView(ExceptionEnum.NOT_FOUND, request, response);
    }


    /**
     * 处理参数校验异常
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     * @return RespResult
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Object validExceptionHandler(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e) {
        // 默认信息
        String errorMsg = ExceptionEnum.REQUEST_ERROR.getMsg();
        // @Valid注解校验抛异常
        if (e.getMessage().contains("Validation")) {
            // 拼接错误信息
            errorMsg = e.getBindingResult().getFieldErrors().stream()
                    .map(f -> f.getField() + ":" + f.getDefaultMessage())
                    .collect(Collectors.joining("| "));
        }
        log.error("[{}]校验异常：{}", request.getRequestURI(), errorMsg);
        return buildModelAndView(ExceptionEnum.REQUEST_ERROR.getCode(), errorMsg, request, response);
    }


    /**
     * 处理自定义的业务异常
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     */
    @ExceptionHandler(BaseException.class)
    public Object baseExceptionHandler(HttpServletRequest request, HttpServletResponse response, BaseException e) {
        log.error("[{}]自定义异常：{}", request.getRequestURI(), e.getErrorMsg());
        if (e.getErrorEnum() != null) {
            return buildModelAndView(e.getErrorEnum(), request, response);
        } else {
            return buildModelAndView(e.getErrorCode(), e.getMessage(), request, response);
        }
    }

    /**
     * 处理其他异常
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     */
    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException {
        log.error("[{}]通用异常异常：{}", request.getRequestURI(), e.getMessage(), e);
        return buildModelAndView(ExceptionEnum.INNER_ERROR.getCode(), e.getMessage(), request, response);
    }


    /**
     * 判断是否有ResponseBody注解（是否接口请求）
     *
     * @param request 请求
     * @return Boolean
     */
    public Boolean hasResponseBody(HttpServletRequest request) {
        // 获取HandlerMethod
        Object attribute = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (attribute instanceof HandlerMethod handlerMethod) {
            // 判断是否是ResponseBody
            return handlerMethod.getMethod().isAnnotationPresent(ResponseBody.class) ||
                    handlerMethod.getBeanType().isAnnotationPresent(RestController.class);
        }
        // 非Controller请求默认false
        return false;
    }


    /**
     * 构建 返回视图
     *
     * @param exceptionEnum 异常枚举
     * @param request       请求
     */
    private Object buildModelAndView(ExceptionEnum exceptionEnum, HttpServletRequest request, HttpServletResponse response) {
        return buildModelAndView(exceptionEnum.getCode(), exceptionEnum.getMsg(), request, response);
    }

    /**
     * 构建 返回视图
     *
     * @param code    错误码
     * @param msg     消息
     * @param request 请求
     */
    private Object buildModelAndView(Integer code, String msg, HttpServletRequest request, HttpServletResponse response) {
        // api接口返回
        if (hasResponseBody(request)) {
            return ResponseEntity.status(HttpStatus.OK).body(RespResult.error(code, msg));
        }
        // 状态码
        response.setStatus(code);
        // 正常视图返回
        ModelAndView model = new ModelAndView("error/temp");
        ErrorTemp errorTemp = new ErrorTemp()
                .setUrl(request.getRequestURL().toString())
                .setCode(code)
                .setMsg(msg)
                .setTime(LocalDateTime.now());
        model.addObject(BaseConstants.AttributeName.ERROR_TEMP, errorTemp);
        return model;
    }
}
