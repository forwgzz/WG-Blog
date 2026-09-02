package vip.wgzz.blog.common.util;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;
import vip.wgzz.blog.model.bo.SelectInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.*;

/**
 * @author wgzz
 * @date 2026/8/8 21:09
 * @description 文件类型工具类
 */
@Slf4j
public class FileTypeUtils {

    /**
     * Tika实例
     */
    private static final Tika TIKA = new Tika();

    /**
     * 图片
     */
    public static final String IMAGE = "image";
    public static final List<String> IMAGE_SUFFIX = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "svg", "webp", "ico");

    /**
     * 视频
     */
    public static final String VIDEO = "video";
    public static final List<String> VIDEO_SUFFIX = Arrays.asList("mp4", "mkv", "avi", "mov", "webm", "flv", "wmv", "3gp", "mpeg", "ts", "m3u8");
    /**
     * 音频
     */
    public static final String AUDIO = "audio";
    public static final List<String> AUDIO_SUFFIX = Arrays.asList("mp3", "wav", "aac", "flac", "ogg", "wma", "aiff", "midi", "m4a", "opus");

    /**
     * 文档
     */
    public static final String DOCUMENT = "document";
    public static final List<String> DOCUMENT_SUFFIX = Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "txt", "rtf", "log");

    /**
     * 代码
     */
    public static final String CODE = "code";
    public static final List<String> CODE_SUFFIX = Arrays.asList("java", "py", "js", "ts", "html", "css", "json", "xml", "sql", "sh", "yml", "yaml", "bat");

    /**
     * 程序
     */
    public static final String EXECUTABLE = "executable";
    public static final List<String> EXECUTABLE_SUFFIX = Arrays.asList("exe", "dll", "apk", "msi", "dmg", "app", "deb", "rpm", "so", "bin");

    /**
     * 压缩包
     */
    public static final String ZIP = "zip";
    public static final List<String> ZIP_SUFFIX = Arrays.asList("zip", "rar", "7z", "tar", "gz", "bz2", "iso");

    /**
     * 其他
     */
    public static final String OTHER = "other";

    /**
     * 后缀与文件类型映射
     */
    private static final Map<String, String> SUFFIX_MAP = new HashMap<>();


    static {
        IMAGE_SUFFIX.forEach(s -> SUFFIX_MAP.put(s, IMAGE));
        VIDEO_SUFFIX.forEach(s -> SUFFIX_MAP.put(s, VIDEO));
        AUDIO_SUFFIX.forEach(s -> SUFFIX_MAP.put(s, AUDIO));
        DOCUMENT_SUFFIX.forEach(s -> SUFFIX_MAP.put(s, DOCUMENT));
        CODE_SUFFIX.forEach(s -> SUFFIX_MAP.put(s, CODE));
        EXECUTABLE_SUFFIX.forEach(s -> SUFFIX_MAP.put(s, EXECUTABLE));
        ZIP_SUFFIX.forEach(s -> SUFFIX_MAP.put(s, ZIP));
    }


    /**
     * @param file 文件
     * @return 获取内容类型
     */
    public static String getContentType(MultipartFile file) {
        try {
            return TIKA.detect(file.getInputStream(), file.getOriginalFilename());
        } catch (Exception e) {
            log.error("获取内容类型失败", e);
            return null;
        }
    }

    /**
     * @param file 文件
     * @return 获取内容类型
     */
    public static String getContentType(File file) {
        try {
            return TIKA.detect(FileUtil.getInputStream(file), file.getName());
        } catch (Exception e) {
            log.error("获取内容类型失败", e);
            return null;
        }
    }

    /**
     * @param fileType 文件类型
     * @return 文件类型Str
     */
    public static String getTypeStr(String fileType) {
        switch (fileType) {
            case DOCUMENT:
                return "文档";
            case IMAGE:
                return "图片";
            case VIDEO:
                return "视频";
            case AUDIO:
                return "音频";
            case CODE:
                return "代码";
            case EXECUTABLE:
                return "程序";
            case ZIP:
                return "压缩包";
            case OTHER:
            default:
                return "其他";
        }
    }

    /**
     * @param suffix 后缀
     * @return 后缀对应文件类型
     */
    public static String getFileType(String suffix) {
        return SUFFIX_MAP.getOrDefault(suffix, OTHER);
    }

    /**
     * @return 文件类型下拉框数据
     */
    public static List<SelectInfo> selectList() {
        ArrayList<SelectInfo> list = new ArrayList<>();
        list.add(new SelectInfo(DOCUMENT, getTypeStr(DOCUMENT)));
        list.add(new SelectInfo(IMAGE, getTypeStr(IMAGE)));
        list.add(new SelectInfo(VIDEO, getTypeStr(VIDEO)));
        list.add(new SelectInfo(AUDIO, getTypeStr(AUDIO)));
        list.add(new SelectInfo(CODE, getTypeStr(CODE)));
        list.add(new SelectInfo(EXECUTABLE, getTypeStr(EXECUTABLE)));
        list.add(new SelectInfo(ZIP, getTypeStr(ZIP)));
        list.add(new SelectInfo(OTHER, getTypeStr(OTHER)));
        return list;
    }

}