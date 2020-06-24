package cn.flyingocean.fileship.util;

import javax.servlet.http.HttpSession;

public class VerificationCodeUtil {
    /**
     * 校验验证码
     * @param session
     * @param verificationCode
     * @return
     */
    public static boolean check(HttpSession session,String verificationCode){
        String vcInSession = (String)session.getAttribute("verificationCode");
        if(vcInSession==null){
            return false;
        }
        if (vcInSession.equals(verificationCode.toUpperCase())){
           return true;
        }
        return false;
    }
}
