package cn.flyingocean.fileship.controller;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.util.VerificationCodeUtil;
import org.apache.shiro.web.session.HttpServletSession;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Controller
@RequestMapping("/common")
public class CommonController {
    /**
     * 验证码生成器
     * @param request
     * @param response
     */
    @RequestMapping("/VerificationCodeGenerator")
    public void YZMGenerator(HttpServletRequest request, HttpServletResponse response) {
        BufferedImage bi = new BufferedImage(68,22,BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        Color c = new Color(255,255,255);
        g.setColor(c);
        g.fillRect(0, 0, 68, 22);

        char[] ch = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();//去掉0,1,O,I
        Random r = new Random();
        int len=ch.length,index;
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<4; i++){
            index = r.nextInt(len);
            g.setColor(new Color(r.nextInt(88),r.nextInt(188),r.nextInt(255)));
            g.drawString(ch[index]+"", (i*15)+3, 18);
            sb.append(ch[index]);
        }
        request.getSession().setAttribute("verificationCode", sb.toString());
        try {
            ImageIO.write(bi, "JPG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证码校验
     * @param verificationCode
     * @param session
     * @return
     */
    @RequestMapping("/VerificationCodeChecker")
    @ResponseBody
    public FOResponse YZMChecker(String verificationCode, HttpSession session){
        boolean r = VerificationCodeUtil.check(session,verificationCode);
        if (r) return new FOResponse(ReturnValue.OK,null);
        return new FOResponse(ReturnValue.ERROR_VERIFICATION_CODE,null);
    }


}
