package vip.wgzz.blog.common.util;

import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

/**
 * @author wgzz
 * @date 2026/8/21 12:37
 * @description 邮箱工具类
 */
@Slf4j
@Component
public class MailUtils  {

    private static String from;

    private static String fromName;

    private static JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    public void setFrom(String from) {
        MailUtils.from = from;
    }

    @Value("${spring.mail.from-name}")
    public void setFromName(String fromName) {
        MailUtils.fromName = fromName;
    }

    @Resource
    public void setMailSender(JavaMailSender mailSender) {
        MailUtils.mailSender = mailSender;
    }

    /**
     * 发送纯文本邮件
     */
    public static void send(String to, String subject, String content) {
        send(to, subject, content, false);
    }

    /**
     * 发送文本/HTML邮件
     */
    public static void send(String to, String subject, String content, boolean isHtml) {
        send(new String[]{to}, null, null, subject, content, isHtml, null, null);
    }

    /**
     * 发送带附件的邮件
     */
    public static void send(String to, String subject, String content, boolean isHtml, Map<String, File> attachments) {
        send(new String[]{to}, null, null, subject, content, isHtml, attachments, null);
    }

    /**
     * 全功能发送
     */
    public static void send(String[] to, String[] cc, String[] bcc,
                            String subject, String content, boolean isHtml,
                            Map<String, File> attachments, Map<String, File> inlineResources) {
        if (mailSender == null || from == null || fromName == null) {
            log.error("mailSender 尚未初始化，请确保 mail 配置正确");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(from, fromName, "UTF-8"));
            helper.setTo(to);
            if (cc != null) helper.setCc(cc);
            if (bcc != null) helper.setBcc(bcc);
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            // 添加附件
            if (attachments != null) {
                for (var entry : attachments.entrySet()) {
                    helper.addAttachment(entry.getKey(), entry.getValue());
                }
            }
            // 添加内嵌资源
            if (inlineResources != null) {
                for (var entry : inlineResources.entrySet()) {
                    helper.addInline(entry.getKey(), entry.getValue());
                }
            }

            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", Arrays.toString(to), subject);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, subject={}", Arrays.toString(to), subject, e);
        }
    }

}
