package cn.flyingocean.fileship.controller;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.NameList;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.service.NameListService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/namelists")
public class NameListController {
    @Autowired
    private NameListService nameListService;

    /**
     * 名单上传
     * @param file 用户上传的单个文件
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    @RequiresUser
    public FOResponse upload(@RequestParam("file") MultipartFile file){
        //参数检查
        if (file==null) return new FOResponse(ReturnValue.BAD_REQUEST,null);

        return nameListService.upload(file);
    }


    /**
     * 下载 NameList-ID 指定的文件
     * copy来的代码 https://www.jianshu.com/p/85017f5ecba1
     * 这里如果 不加@ResponseBody，就不能有返回值，否则会因启用默认的ViewResovler报错
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/download/{nameListId}")
    @ResponseBody
    @RequiresAuthentication
    public FOResponse download(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable("nameListId") String nameListId) {
        // 参数检查
        if (nameListId==null||nameListId.length()==0)   return new FOResponse(ReturnValue.BAD_REQUEST,null);

        return nameListService.doDownload(request,response,Integer.valueOf(nameListId));
    }


    @GetMapping("/list")
    @ResponseBody
    @RequiresUser
    public FOResponse list(){
        return nameListService.list();
    }

    @GetMapping("/fetch-header/{nameListId}")
    @ResponseBody
    @RequiresUser
    public FOResponse fetchHeader(@PathVariable("nameListId") String nameListId){
        // 参数校验
        if (nameListId==null||nameListId.isEmpty()) return new FOResponse(ReturnValue.BAD_REQUEST,null);

        return nameListService.fetchHeader(Integer.valueOf(nameListId));
    }
}
