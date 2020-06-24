package cn.flyingocean.fileship.controller;


import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.dto.FOResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ResponseBody
    @ExceptionHandler(value = MultipartException.class)
    public FOResponse multipartException(Exception e){
        e.printStackTrace();
        return new FOResponse(ReturnValue.BAD_REQUEST,null);
    }
}
