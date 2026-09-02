package vip.wgzz.blog.common.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import vip.wgzz.blog.common.exception.BaseException;
import vip.wgzz.blog.common.exception.ExceptionEnum;

import java.util.Set;

/**
 * @author wgzz
 * @date 2026/8/9 13:01
 * @description 字段检验工具类
 */
public class ValidatorUtils {

    private static final Validator VALIDATOR = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    /**
     * 验证单个对象，并支持分组
     */
    public static <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new BaseException(ExceptionEnum.REQUEST_ERROR.getCode(), violations.iterator().next().getMessage());
        }
    }

    /**
     * 验证集合中的每个元素，并支持分组
     */
    public static <T> void validateAll(Iterable<T> iterable, Class<?>... groups) {
        for (T obj : iterable) {
            validate(obj, groups);
        }
    }
}
