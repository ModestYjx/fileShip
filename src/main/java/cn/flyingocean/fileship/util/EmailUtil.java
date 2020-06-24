package cn.flyingocean.fileship.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Junit Test中必须由Spring容器注入, 因为Spring 容器会读取yml中的配置信息，
 * 然后去配置JavaMailSender。亲测，不用Spring注入，简单使用会报错连接不上
 */
@Component
@Scope("prototype")
//TODO: 改成@Scope("session")时会报错
public class EmailUtil implements Runnable{
    @Value("${spring.mail.from}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    private String receiver;

    private String htmlText;

    private String subject;

    public static String claimEmailTemplate = "<!DOCTYPE html>\n" +
            "<html lang=\"zh-CN\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>通知用户认领文件模板</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,minimum-scale=1\">\n" +
            "    <link rel=\"stylesheet\" href=\"common-css/bootstrap3.css\">\n" +
            "</head>\n" +
            "<body>\n" +
            "    <table class=\"table table-bordered table-striped\">\n" +
            "        <tr>\n" +
            "            <th>文件名</th>\n" +
            "            <th>上传日期</th>\n" +
            "            <th>32位文件码</th>\n" +
            "            <th>中文文件码</th>\n" +
            "            <th>认领地址</th>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td>{filename}</td>\n" +
            "            <td>{createDate}</td>\n" +
            "            <td>{uuid}</td>\n" +
            "            <td>{chineseToken}</td>\n" +
            "            <td>{claimUrl}</td>\n" +
            "        </tr>\n" +
            "    </table>\n" +
            "</body>\n" +
            "</html>";


    public String initClaimEmailFromTemplate(String filename,String createDate,String uuid,
                                             String chineseToken,String claimUrl){

        String r = claimEmailTemplate.replace("{filename}",filename);
        r = r.replace("{createDate}",createDate);
        r = r.replace("{uuid}",uuid);
        r = r.replace("{chineseToken}",chineseToken);
        r = r.replace("{claimUrl}",claimUrl);
        return r;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // 仅开发测试时使用
    public JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }

    // 仅开发测试时使用
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public static final int MAILSTATUS_OK = 0;
    public static final int MAILSTATUS_FAILED = 1;

    /**
     * 发送简单文本邮件
     *
     * @param receiver
     * @param subject
     * @param text
     * @return
     */
    public byte sendSimpleMail(String receiver, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(text);
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return MAILSTATUS_FAILED;
        }
        return MAILSTATUS_OK;
    }

    /**
     * 发送HTML文本
     *
     */
    public byte sendHtmlMail() {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return MAILSTATUS_FAILED;
        }
        return MAILSTATUS_OK;
    }

    /**
     * 发送带附件的HTML邮件
     *
     * @param receiver
     * @param subject
     * @param htmlText
     * @param filePath
     * @return
     */
    public byte sendFilesMail(String receiver, String subject, String htmlText, String filePath) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(htmlText, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return MAILSTATUS_FAILED;
        }
        System.out.println("邮件发送成功");
        return MAILSTATUS_OK;
    }

    @Override
    public void run() {
        sendHtmlMail();
    }
}
