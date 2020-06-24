package cn.flyingocean.fileship.controller;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.File;
import cn.flyingocean.fileship.domain.User;
import cn.flyingocean.fileship.domain.Warehouse;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.dto.FileWarehouseDTO;
import cn.flyingocean.fileship.dto.UserDTO;
import cn.flyingocean.fileship.service.FileService;
import cn.flyingocean.fileship.service.UserService;
import cn.flyingocean.fileship.service.WareHouseService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    FileService fileService;
    @Autowired
    UserService userService;
    @Autowired
    WareHouseService wareHouseService;
    /**
     * 文件上传
     * @param file 用户上传的单个文件
     * @param email 邮箱
     * @param ttl time to live
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public FOResponse upload(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "email",required = false) String email
                            ,@RequestParam(value = "ttl",required = false) String ttl){
        //参数检查
        if (file==null) return new FOResponse(ReturnValue.BAD_REQUEST,null);
        if (email==null||email.isEmpty())   return new FOResponse(ReturnValue.BAD_REQUEST,null);

        return fileService.doUploadSinglePersonFile(file,email);
        // todo 没有对有效期进行处理
    }

    /**
     * 获得fileId 指定的文件信息
     * @param fileId
     * @return
     */

    @ResponseBody
    @GetMapping("/{fileId}")
    @RequiresUser
    public FOResponse getFileById(@PathVariable("fileId") String fileId){
        // 参数校验
        if (fileId.isEmpty()||fileId==null) return new FOResponse(ReturnValue.BAD_REQUEST,null);

        Subject subject = SecurityUtils.getSubject();
        String email = subject.getPrincipal().toString();
        File file = fileService.findById(Integer.valueOf(fileId));
        // 如果文件找不到
        if (file==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
        // 所有权认证
        User user = userService.findById(file.getHolderId());
        if (user.getEmail().equals(email)){
            return new FOResponse(ReturnValue.OK,file);
        }
        return new FOResponse(ReturnValue.FORBIDDEN,null);
    }


    /**
     * 列出当前认证用户指定仓库中所有文件（包含认领和未认领）
     * 根仓库 ID 为0
     * @return
     */
    @ResponseBody
    @GetMapping("/list/{warehouseId}")
    @RequiresUser
    public FOResponse list(@PathVariable("warehouseId") String warehouseId){
        Subject subject = SecurityUtils.getSubject();
        String email = subject.getPrincipal().toString();
        User user = userService.findByEmail(email);
        // 如果指定所属文件仓库ID
        if (warehouseId!=null&&!warehouseId.isEmpty()){
            List<File> files = fileService.findByHolderIdAndWarehouseId(user.getId(),warehouseId);
            List<FileWarehouseDTO> retList = new LinkedList<>();
            for (int i=0;i<files.size();i++){
                retList.add(FileWarehouseDTO.buildFromFile(files.get(i)));
            }
            return new FOResponse(ReturnValue.OK,retList);
        }
        // 如果没有指定所属文件仓库ID，列出当前用户所有拥有的所有File
        List<File> files = fileService.listByUserId(user.getId());
        List<FileWarehouseDTO> retList = new LinkedList<>();
        for (int i=0;i<files.size();i++){
            //todo 偷懒做法，这里应该dao中过滤
            if (files.get(i).getWarehouseId()!=0) continue;
            retList.add(FileWarehouseDTO.buildFromFile(files.get(i)));
        }
        return new FOResponse(ReturnValue.OK,retList);
    }



    /**
     * 认领文件
     * fileClaimId-fileClaimCode 为了方便，也可是仅仅是fileId
     *
     * @return
     */
    @GetMapping("/claim/{fileClaimId-fileClaimCode}")
    @ResponseBody
    public FOResponse claim(@PathVariable("fileClaimId-fileClaimCode") String fileClaimId_fileClaimCode){
        // 参数校验
        if (fileClaimId_fileClaimCode==null) return  new FOResponse(ReturnValue.BAD_REQUEST,null);

        String[] splitResult = fileClaimId_fileClaimCode.split("-");

        // 参数检查
        if (splitResult.length!=2){
            return fileService.doClaimOnline(fileClaimId_fileClaimCode);
        }

        String fileClaimId = splitResult[0];
        String fileCliamCode = splitResult[1];
        return fileService.doClaim(Integer.valueOf(fileClaimId),fileCliamCode);
    }


    /**
     * 下载 token 指定的文件
     * copy来的代码 https://www.jianshu.com/p/85017f5ecba1
     * 这里如果 不加@ResponseBody，就不能有返回值，否则会因启用默认的ViewResovler报错
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/download/{token}")
    @ResponseBody
    public FOResponse download(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable("token") String token) {
        // 参数检查
        if (token==null||token.length()==0)   return new FOResponse(ReturnValue.BAD_REQUEST,null);

        return fileService.doDownload(request,response,token);
    }

    /**
     * 提交文件到仓库
     * @param data
     * @return
     */
    @RequiresAuthentication
    @PatchMapping("/join")
    @ResponseBody
    public FOResponse join(@RequestBody Map<String,String> data){
        // 参数检查
        String fileId = data.get("fileId");
        String warehouseToken = data.get("warehouseToken");
        String newFileName = data.get("newFileName");

        System.out.println(fileId+"-"+warehouseToken+"-"+newFileName);
        if (fileId==null||fileId.isEmpty() ||
                warehouseToken==null|| warehouseToken.isEmpty()||
                (newFileName==null) ) return new FOResponse(ReturnValue.BAD_REQUEST,null);

        return fileService.doJoin(Integer.valueOf(fileId),warehouseToken,newFileName);
}


    /**
     * 文件重命名
     * @param data
     * @return
     */
    @PatchMapping("/rename")
    @ResponseBody
    @RequiresUser
    public FOResponse rename(@RequestBody Map<String,String> data){
        // 参数检查
        String fileId = data.get("fileId");
        String newName = data.get("newName");

        if (fileId==null||fileId.isEmpty()||
                newName==null||newName.isEmpty()){
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }

        return  fileService.rename(Integer.parseInt(fileId),newName);
    }


    @PostMapping("/collationAndAnalysis")
    @ResponseBody
    @RequiresUser
    public FOResponse collationAndAnalysis(@RequestBody Map<String,String> data){

        int methodKind;
        try{
            methodKind = Integer.valueOf(data.get("methodKind"));
        }catch (Exception e){
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }
        String keyColumn = data.get("keyColumn");
        String namePattern = data.get("namePattern");
        String warehouseId = data.get("warehouseId");
        String nameListId = data.get("nameListId");
        // 参数检查
        if (keyColumn==null||keyColumn.isEmpty()||
                namePattern==null||namePattern.isEmpty()||
                nameListId==null||nameListId.isEmpty()){
           return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }

        // 通过名单和文件名对比
        if (methodKind==1){
            return fileService.collationAndAnalysis1(Integer.valueOf(warehouseId),Integer.valueOf(nameListId),keyColumn,namePattern);
        }else if (methodKind==2){ // 通过名单和上传者的邮箱对比

        }else{
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }

        //fixme
        return null;
    }

    @ResponseBody
    @RequiresAuthentication
    @DeleteMapping("/delete/{fileId}")
    public FOResponse delete(@PathVariable("fileId") String fileId){
        // 参数检查
        if(fileId==null||fileId.isEmpty()){
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }

        return fileService.deleteById(Integer.valueOf(fileId));
    }
}
