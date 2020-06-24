package cn.flyingocean.fileship.controller;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.dto.FOResponse;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class UserExceptionController {

    /**
     * shiro：用户未认证异常
     * @return
     */
    @ExceptionHandler(value =  UnauthenticatedException.class)
    public FOResponse unauthenticatedExceptionHandler(Exception e){
        return new FOResponse(ReturnValue.UNAUTHORIZED,null);
    }
}
