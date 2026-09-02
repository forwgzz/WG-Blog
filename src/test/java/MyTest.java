import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.aspectj.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import vip.wgzz.blog.BlogApplication;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wgzz
 * @date 2026/8/2 15:39
 * @description test
 */
@SpringBootTest(classes = BlogApplication.class)
public class MyTest {


    @Test
    public void test() {
        long start = System.currentTimeMillis();

        long t1 = System.currentTimeMillis() - start;
        System.out.println(t1);
    }


    @Test
    public void test2() {

    }
}
