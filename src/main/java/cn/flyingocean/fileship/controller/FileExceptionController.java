package cn.flyingocean.fileship.controller;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.dto.FOResponse;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
@ResponseBody
public class FileExceptionController {

    /**
     * 超过最大上传大小异常
     * @param e
     * @return
     */
    @ExceptionHandler(value =  MaxUploadSizeExceededException.class)
    public FOResponse maxUploadSizeExceededExceptionHandler(Exception e){
        return new FOResponse(ReturnValue.ERROR_MAX_UPLOAD_SIZE,null);
    }

    /**
     * 超过最大上传大小异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = FileUploadBase.SizeLimitExceededException.class)
    public FOResponse fileSizeLimitExceededExceptionHandler(Exception e){
        return new FOResponse(ReturnValue.ERROR_MAX_UPLOAD_SIZE,null);
    }
}
