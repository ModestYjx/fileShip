package cn.flyingocean.fileship.controller;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.User;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.dto.UserDTO;
import cn.flyingocean.fileship.repository.UserRepository;
import cn.flyingocean.fileship.service.UserService;
import cn.flyingocean.fileship.util.VerificationCodeUtil;
import cn.flyingocean.fileship.validated.UserSignUp;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    UserService userService;

    /**
     * 处理登录
     * @param user
     * @return
     */
    @PostMapping("/doSignIn")
    @ResponseBody
    public FOResponse doSignIn(@Validated @RequestBody User user, BindingResult result, HttpSession session,
                               @RequestParam("verificationCode") String verificationCode,@RequestParam("isRememberMe") boolean isRememberMe) {
        // 验证码校验
        boolean r = VerificationCodeUtil.check(session,verificationCode);
        if (!r) return new FOResponse(ReturnValue.ERROR_VERIFICATION_CODE,null);
        // 参数校验
        if (result.hasErrors()) {
            return new FOResponse(ReturnValue.BAD_REQUEST, null);
        }

       return userService.doSignIn(user,isRememberMe);
    }

    /**
     * 处理注册
     *
     * @param user
     * @param result
     * @param verificationCode
     * @param session
     * @return
     */
    @PostMapping("/doSignUp")
    @ResponseBody
    public FOResponse doSignUp(@Validated(UserSignUp.class)  @RequestBody User user,BindingResult result,
                               HttpSession session, HttpServletRequest request,
                               @RequestParam("verificationCode") String verificationCode) {
        // 验证码校验
        boolean r = VerificationCodeUtil.check(session,verificationCode);
        if (!r) return new FOResponse(ReturnValue.ERROR_VERIFICATION_CODE,null);

        // 参数校验
        if (result.hasErrors()) {
            return new FOResponse(ReturnValue.BAD_REQUEST, null);
        }

        return userService.doSignUp(user,request);
    }

    /**
     * 激活账号
     *
     * @param uuid
     * @param id
     * @return
     */
    @GetMapping("/activate/{uuid}/{id}")
    @ResponseBody
    FOResponse doActivate(@PathVariable("uuid") String uuid, @PathVariable("id") int id) {
        if(uuid==null||uuid.isEmpty())  return new FOResponse(ReturnValue.BAD_REQUEST,null);
        return userService.doActivate(uuid,id);
    }

    /**
     * 返回当前认证用户的信息
     * @return
     */
    @ResponseBody
    @GetMapping("/")
    @RequiresUser
    public FOResponse getUser(){
        Subject subject = SecurityUtils.getSubject();
        String email = subject.getPrincipal().toString();
        User user = userService.findByEmail(email);
        UserDTO userDTO = UserDTO.buildFromUser(user);
        return new FOResponse(ReturnValue.OK,userDTO);
    }


}
