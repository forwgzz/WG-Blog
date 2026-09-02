package vip.wgzz.blog.common.util;

import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.IdUtil;
import vip.wgzz.blog.common.exception.BaseException;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author wgzz
 * @date 2026/8/12 21:53
 * @description ID生成工具
 */
public class IdUtils {

    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Random RANDOM = new SecureRandom();

    /**
     * @param length id长度
     * @return NanoId（指定长度）
     */
    public static String nanoId(int length) {
        if (length < 8) {
            throw new BaseException("唯一ID长度不能小于8");
        }
        return NanoId.randomNanoId(RANDOM, ALPHABET, length);
    }

    /**
     * @return 12位 NanoId
     */
    public static String shortNanoId() {
        return nanoId(12);
    }

    /**
     * @return 32位UUID
     */
    public static String uuid() {
        return IdUtil.simpleUUID();
    }

    /**
     * 当前毫秒 序列
     */
    private static int index;

    /**
     * 上一毫秒时间
     */
    private static long lastTime;

    /**
     * @return 20位 时间 yyyyMMddHHmmssSSS+000-999
     */
    public static synchronized String timeId() {
        long currentTimestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = sdf.format(new Date(currentTimestamp));
        if (currentTimestamp == lastTime) {
            if (++index > 999) {
                //等待下一毫秒
                while (currentTimestamp <= lastTime) {
                    currentTimestamp = System.currentTimeMillis();
                }
                timestamp = sdf.format(new Date(currentTimestamp));
                index = 0;
            }
        } else {
            index = 0;
        }
        lastTime = currentTimestamp;
        return timestamp + String.format("%03d", index);
    }


    /**
     * 上一次生成 ID 的时间戳
     */
    private volatile static long lastTimestamp = -1L;

    /**
     * @return 毫秒时间戳id
     */
    public static synchronized String getTimestampId() {
        // 当前时间戳
        long currentTimestamp = System.currentTimeMillis();

        // 如果当前时间戳与上一次相同，则等待到下一毫秒
        while (currentTimestamp <= lastTimestamp) {
            try {
                Thread.sleep(1); // 等待 1 毫秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            currentTimestamp = System.currentTimeMillis();
        }
        // 更新最后的时间戳
        lastTimestamp = currentTimestamp;
        // 返回
        return String.valueOf(currentTimestamp);
    }
}
