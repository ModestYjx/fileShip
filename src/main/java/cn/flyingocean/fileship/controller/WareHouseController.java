package cn.flyingocean.fileship.controller;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.User;
import cn.flyingocean.fileship.domain.Warehouse;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.dto.FileWarehouseDTO;
import cn.flyingocean.fileship.service.UserService;
import cn.flyingocean.fileship.service.WareHouseService;
import cn.flyingocean.fileship.util.FileUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/warehouses")
public class WareHouseController {
    @Autowired
    WareHouseService wareHouseService;
    @Autowired
    UserService userService;

    /**
     * 创建仓库
     * @param data
     * @return
     */
    @RequiresUser
    @PostMapping("/create/{superWarehouseId}")
    public FOResponse create(@RequestBody Map<String,String> data,@PathVariable("superWarehouseId")int superWarehouseId){
        // 仓库名
        String warehouseName = data.get("warehouseName");
        // 命名要求
        String constraint = data.get("constraint");
        // todo 这里暂时没有做对文件有效期的功能
        // 参数检验
        if (warehouseName==null||warehouseName.isEmpty()||
                constraint==null||constraint.isEmpty()){
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }
        return wareHouseService.create(warehouseName,constraint,superWarehouseId);
    }

    /**
     * 列出 superWarehouseId 指定的父仓库下所有的文件仓库
     * @return
     */
    @RequiresUser
    @GetMapping("/list/{superWarehouseId}")
    public FOResponse listWarehouse(@PathVariable("superWarehouseId") int superWarehouseId){
        Subject subject = SecurityUtils.getSubject();
        String email = subject.getPrincipal().toString();
        User user = userService.findByEmail(email);
        List<Warehouse> warehouses = wareHouseService.findByHolderIdAndSuperWarehouseId(user.getId(),superWarehouseId);
        List<FileWarehouseDTO> retList = new LinkedList<>();
        for (int i = 0; i< warehouses.size(); i++){
            retList.add(FileWarehouseDTO.buildFromWarehouse(warehouses.get(i)));
        }
        return new FOResponse(ReturnValue.OK, retList);
    }

    /**
     * 合并仓库 /merge?type=
     * @param data
     * @return
     */
    @RequiresAuthentication
    @PatchMapping("/merge")
    public FOResponse merge(@RequestBody Map<String,String> data,@RequestParam String type){
        // 参数检查
        String srcWarehouseId = data.get("srcWarehouseId");
        String destWarehouseToken = data.get("destWarehouseToken");

        if (srcWarehouseId==null||srcWarehouseId.isEmpty() ||
                destWarehouseToken==null|| destWarehouseToken.isEmpty()||
                type==null||type.isEmpty())
            return new FOResponse(ReturnValue.BAD_REQUEST,null);

        if (type==FileUtil.MOVE_MERGE){
            return wareHouseService.moveMerge(Integer.valueOf(srcWarehouseId),destWarehouseToken);
        }else if (type==FileUtil.COPY_MERGE){
            return wareHouseService.copyMerge(Integer.valueOf(srcWarehouseId),destWarehouseToken);
        }else if (type==FileUtil.ASSOCIATE_MERGE){
            return wareHouseService.collaborativeMerge(Integer.valueOf(srcWarehouseId),destWarehouseToken);
        }else {
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }

    }

    @RequiresUser
    @ResponseBody
    @PatchMapping("/rename")
    public FOResponse rename(@RequestBody Map<String,String> data){
        // 参数检查
        String warehouseId = data.get("warehouseId");
        String newName = data.get("newName");

        if (warehouseId==null||warehouseId.isEmpty()||
                newName==null||newName.isEmpty()){
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }

        return wareHouseService.rename(Integer.valueOf(warehouseId),newName);
    }

    @RequiresAuthentication
    @ResponseBody
    @DeleteMapping("/delete/{warehouseId}")
    public FOResponse delete(@PathVariable("warehouseId") String warehouseId){
        // 参数检查
        if (warehouseId==null||warehouseId.isEmpty()){
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }

        return wareHouseService.deleteById(Integer.valueOf(warehouseId));
    }

    @RequiresAuthentication
    @ResponseBody
    @GetMapping("/download/{warehouseToken}")
    public FOResponse download(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable("warehouseToken") String warehouseToken) {
        // 参数检查
        if (warehouseToken==null||warehouseToken.length()==0)   return new FOResponse(ReturnValue.BAD_REQUEST,null);

        return wareHouseService.doDownload(request,response,warehouseToken);
    }
}
