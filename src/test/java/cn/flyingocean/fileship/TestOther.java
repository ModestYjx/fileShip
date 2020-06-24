package cn.flyingocean.fileship;

import cn.flyingocean.fileship.domain.User;
import cn.flyingocean.fileship.util.ChineseWordUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestOther {
    // 测试正则表达式
    @Test
    public void testRegExp() {
        // 按指定模式在字符串查找
        String line = "1234";
        String pattern = ".{1}";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        while (true){
            System.out.println();

        }
    }

    // 测试反射, 测试结果如下
    // class org.springframework.mail.javamail.JavaMailSenderImpl
    // 1:interface org.springframework.mail.javamail.JavaMailSender
    @Test
    public void testReflect() {
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        System.out.println(javaMailSender.getClass());
        System.out.println(javaMailSender.getClass().getInterfaces().length + ":" + javaMailSender.getClass().getInterfaces()[0]);
    }

    @Test
    public void testSecureRandom() {
        SecureRandom secureRandom = new SecureRandom();
        for(int i=0;i<20;i++){
            System.out.println(secureRandom.nextInt(3496));
        }
    }

    @Test
    public void testStringBuilder(){
        StringBuilder sb = new StringBuilder("123456");
        System.out.println(sb);
        sb.delete(0,sb.length());
        System.out.println(sb);
    }


    @Test
    public void testEncryption() {
        System.out.println(new Md5Hash("asd123"));
    }

    @Test
    public void getOS(){
        System.out.println("-"+System.getProperty("os.name")+"-");
    }
}
