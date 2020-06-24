package cn.flyingocean.fileship.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.Email;

import static org.junit.Assert.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class EmailUtilTest {

    private EmailUtil emailUtil = new EmailUtil();

    @Test
    public void sendSimpleMail() throws Exception {
        System.out.println(emailUtil.sendSimpleMail("1195657409@qq.com", "测试邮件发送", "成功了！"));
    }

    @Test
    public void testHtmlMail() throws Exception {
    }

    @Test
    public void sendFilesMail() throws Exception {
    }

    @Test
    public void initClaimEmailFromTemplate() throws Exception {
        String s = emailUtil.initClaimEmailFromTemplate("1.txt","2018-01-12","sdfasfasfd",
                "中文文件码","localhost://asdf");
        System.out.println(s);
    }
}