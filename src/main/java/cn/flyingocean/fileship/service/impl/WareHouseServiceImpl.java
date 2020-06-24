package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.*;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.repository.WareHouseRepository;
import cn.flyingocean.fileship.service.*;
import cn.flyingocean.fileship.util.ChineseWordUtil;
import cn.flyingocean.fileship.util.FileUtil;
import cn.flyingocean.fileship.util.UuidUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WareHouseServiceImpl implements WareHouseService {

    @Autowired
    WareHouseRepository wareHouseRepo;
    @Autowired
    UserService userService;
    @Autowired
    FileService fileService;
    @Autowired
    FileClaimService fileClaimService;
    @Autowired
    CollaborativeWarehouseService cwService;
    @Autowired
    ChineseWordService chineseWordService;

    @Override
    public Warehouse findById(int id) {
        Optional<Warehouse> wareHouseOptional = wareHouseRepo.findById(id);
        if (wareHouseOptional.isPresent()) return wareHouseOptional.get();
        return null;
    }

    @Override
    public Warehouse findByUuid(String uuid) {
        return wareHouseRepo.findByUuid(uuid);
    }

    @Override
    public List<Warehouse> findByHolderIdAndSuperWarehouseId(int holderId,int superWarehouseId) {
        return wareHouseRepo.findByHolderIdAndSuperWarehouseId(holderId,superWarehouseId);
    }

    @Override
    public FOResponse create(String warehouseName, String constraint,int superWarehouseId) {
        // 保证 super warehouse 存在
        Warehouse superWarehouse = findById(superWarehouseId);
        if (superWarehouse==null){
            return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);
        }
        // 创建文件夹
        String uuid = UuidUtil.generate();
        String email = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByEmail(email);
        int userId = user.getId();
        try {
            FileUtil.createWareHouseDirectory(userId,uuid);
        } catch (IOException e) {
            e.printStackTrace();
            return new FOResponse(ReturnValue.ERROR_WAREHOUSE_DIRECTORY_CREATE,null);
        }

        // 持久化数据
        Warehouse warehouse = new Warehouse();
        warehouse.setNameConstraint(constraint);
        warehouse.setHolderId(userId);
        warehouse.setUuid(uuid);
        warehouse.setWarehouseName(warehouseName);
        warehouse.setSuperWarehouseId(superWarehouseId);
        wareHouseRepo.save(warehouse);

        return new FOResponse(ReturnValue.OK,null);
    }


    @Override
    public FOResponse rename(Integer warehouseId, String newName) {
        // 如果该仓库找不到
        Warehouse warehouse = findById(warehouseId);
        if (warehouse == null) return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);

        // 检查该仓库的所有者是否为当前登录用户
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findById(warehouse.getHolderId());
        if (!user.getEmail().equals(email)) return new FOResponse(ReturnValue.FORBIDDEN,null);

        // 由于WareHouse只对应一个物理文件夹，所以不用进行物理的文件重命名
        // 修改数据库中Warehouse
        warehouse.setWarehouseName(newName);
        wareHouseRepo.save(warehouse);

        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public FOResponse deleteById(int warehouseId) {
        // 检查当前操作文件所有者是否为当前登录用户
        Warehouse warehouse = findById(warehouseId);
        if (warehouse==null) return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);
        User user = userService.findById(warehouse.getHolderId());
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        if (email.equals(user.getEmail())==false)   return new FOResponse(ReturnValue.FORBIDDEN,null);
        List<File> fileList = fileService.findByWareHouseId(warehouseId);
        for (File file:fileList){
            try {
                FileUtil.deleteFile(String.valueOf(user.getId()),warehouse.getUuid(),file.getUuid(),file.getFilename());
                FileUtil.deleteFileDir(String.valueOf(user.getId()),warehouse.getUuid(),file.getUuid());

                FileClaim fileClaim = fileClaimService.findByFileId(file.getId());
                if (fileClaim!=null)    fileClaimService.deleteById(fileClaim.getId());

                fileService.deleteById(file.getId());
            } catch (IOException e) {
                e.printStackTrace();
                return new FOResponse(ReturnValue.ERROR_DELETE_FILE,null);
            }
        }
        try {
            FileUtil.deleteWarehouseDir(String.valueOf(user.getId()),warehouse.getUuid());
        } catch (IOException e) {
            e.printStackTrace();
            return new FOResponse(ReturnValue.ERROR_DELETE_WAREHOUSE_DIR,null);
        }
        wareHouseRepo.deleteById(warehouseId);
        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public FOResponse doDownload(HttpServletRequest request, HttpServletResponse response, String warehouseToken) {
        // 校验文件仓库所有权
        Warehouse warehouse = null;
        if (ChineseWordUtil.isChineseToken(warehouseToken)==false){
           warehouse = wareHouseRepo.findByUuid(warehouseToken);
        }else{
            warehouse = wareHouseRepo.findByChineseToken(warehouseToken);
        }

        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findById(warehouse.getHolderId());
        if (user.getEmail().equals(email)==false){
            return new FOResponse(ReturnValue.FORBIDDEN,null);
        }

        // 文件打包
        List<File> fileList = fileService.findByWareHouseId(warehouse.getId());
        List<java.io.File> files = new ArrayList<>();
        for (int i=0;i<fileList.size();i++){
            files.add(FileUtil.getFileInWarehouse(String.valueOf(user.getId()),warehouse.getUuid(),
                    fileList.get(i).getUuid(),fileList.get(i).getFilename()));
        }
        String zipFileName = warehouse.getWarehouseName()+"-"+new Date().getTime()+".zip";
        java.io.File zipFile = FileUtil.packageZip(zipFileName,files);

        // 如果文件存在
        if (zipFile!=null) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + zipFile.getName());// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(zipFile);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                return new FOResponse(ReturnValue.OK,null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            return new FOResponse(ReturnValue.ERROR_WAREHOUSE_PACKAGE,null);
        }
        return new FOResponse(ReturnValue.NOT_ACCEPTABLE,null);
    }

    @Override
    public int createRootWarehouse() {
        // 创建文件夹
        String uuid = UuidUtil.generate();
        String email = (String) SecurityUtils.getSubject().getPrincipal();
        int userId = 0;
        try {
            userId = userService.findByEmail(email).getId();
        }catch (Exception e)
        {
            System.out.println("该邮箱没注册");
        }

        // 持久化数据
        Warehouse warehouse = new Warehouse();
        warehouse.setNameConstraint(null);
        warehouse.setHolderId(userId);
        warehouse.setUuid(uuid);
        warehouse.setWarehouseName(null);
        wareHouseRepo.save(warehouse);
        return warehouse.getId();
    }

    @Override
    public FOResponse moveMerge(Integer srcWarehouseId, String destWarehouseToken) {
        Warehouse srcWarehouse = findById(srcWarehouseId);
        // 如果仓库不存在
        if (srcWarehouse==null)  return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);
        // 如果是关联型仓库不支持本操作
        if (srcWarehouse.isCollaborative())  return new FOResponse(ReturnValue.NOT_SUPPORT_COLLABORATIVE_WAREHOUSE,null);
        // 如果登录用户非该仓库的所有者
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User srcWarehouseHolder = userService.findById(srcWarehouse.getHolderId());
        if (!srcWarehouseHolder.getEmail().equals(email)) return new FOResponse(ReturnValue.FORBIDDEN,null);

        // 将srcWarehouse文件夹中所有文件移动到给destWarehouse文件夹中
        Warehouse destWarehouse = findByToken(destWarehouseToken);
        // 如果dest仓库不存在
        if (destWarehouse==null)    return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);

        srcWarehouse.setSuperWarehouseId(destWarehouse.getId());

        // 树的后序遍历
        ReturnValue rv = moveMerge(srcWarehouse,destWarehouse);
        return new FOResponse(rv,null);
    }

    /**
     *
     * @param srcWarehouse
     * @param destWarehouse
     * @return
     */
    private ReturnValue moveMerge(Warehouse srcWarehouse,Warehouse destWarehouse){
        List<Warehouse> warehouseList = findByHolderIdAndSuperWarehouseId(srcWarehouse.getHolderId(),srcWarehouse.getId());

        for (Warehouse warehouse : warehouseList){
            moveMerge(warehouse,destWarehouse);
        }

        try {
            FileUtil.moveMerge(String.valueOf(srcWarehouse.getHolderId()),srcWarehouse.getUuid(),
                    String.valueOf(destWarehouse.getHolderId()),destWarehouse.getUuid());
            // 将srcWarehouse中所有文件的所有权转交给destWarehouse的拥有者
            srcWarehouse.setHolderId(destWarehouse.getHolderId());
            save(srcWarehouse);
        } catch (IOException e) {
            e.printStackTrace();
            return ReturnValue.ERROR_WAREHOUSE_MERGE;
        }

        return ReturnValue.OK;
    }


    @Override
    public FOResponse copyMerge(Integer srcWarehouseId, String destWarehouseToken) {
        Warehouse srcWarehouse = findById(srcWarehouseId);
        Warehouse destWarehouse = findByToken(destWarehouseToken);
        // 如果仓库不存在
        if (srcWarehouse==null||destWarehouse==null)  return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);
        // 如果是协作型仓库不支持本操作
        if (srcWarehouse.isCollaborative())  return new FOResponse(ReturnValue.NOT_SUPPORT_COLLABORATIVE_WAREHOUSE,null);
        // 如果登录用户非该仓库的所有者
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User srcWarehouseHolder = userService.findById(srcWarehouse.getHolderId());
        if (!srcWarehouseHolder.getEmail().equals(email)) return new FOResponse(ReturnValue.FORBIDDEN,null);

        // 树的前序遍历
        ReturnValue rv = copyMerge(srcWarehouse,destWarehouse,destWarehouse.getId());
        return new FOResponse(rv,null);
    }

    private ReturnValue copyMerge(Warehouse srcWarehouse,Warehouse destWarehouse,int superWarehouseId){

        // 树的前序遍历
        List<File> files = fileService.findByHolderIdAndWarehouseId(srcWarehouse.getHolderId(),
                String.valueOf(srcWarehouse.getId()));
        List<String> fileNames = new ArrayList<>();
        List<String> fileUUIDs = new ArrayList<>();
        for (File file:files){
            fileNames.add(file.getFilename());
            fileUUIDs.add(file.getUuid());
        }
        try {
            FileUtil.copyMerge(String.valueOf(srcWarehouse.getHolderId()),srcWarehouse.getUuid(),fileUUIDs,fileNames,
                    String.valueOf(destWarehouse.getHolderId()),destWarehouse.getUuid());
        } catch (IOException e) {
            e.printStackTrace();
            return ReturnValue.ERROR_WAREHOUSE_MERGE;
        }
        // 将srcWarehouse中所有文件的所有权转交给destWarehouse的拥有者并归属到目标仓库
        Warehouse warehouse = new Warehouse();
        warehouse.setSuperWarehouseId(superWarehouseId);
        warehouse.setHolderId(destWarehouse.getHolderId());
        warehouse.setWarehouseName(srcWarehouse.getWarehouseName());
        warehouse.setNameConstraint(srcWarehouse.getNameConstraint());
        warehouse.setUuid(UuidUtil.generate());
        warehouse.setChineseToken(chineseWordService
                .getAvailableWarehouseChineseToken((byte) srcWarehouse.getChineseToken().length()).chineseToken);

        save(warehouse);

        for (int i=0;i<files.size();i++){
            File file = new File();
            file.setFilename(files.get(i).getFilename());
            file.setWarehouseId(warehouse.getId());
            file.setChineseToken(chineseWordService
                    .getAvailableFileChineseToken((byte) file.getChineseToken().length()).chineseToken);
            file.setUuid(UuidUtil.generate());
            file.setStatus(files.get(i).getStatus());
            file.setUploaderId(file.getUploaderId());
            fileService.save(file);
        }

        List<Warehouse> children = findByHolderIdAndSuperWarehouseId(srcWarehouse.getHolderId(),srcWarehouse.getId());

        ReturnValue rv = null;
        for (Warehouse child : children){
             rv = copyMerge(warehouse,destWarehouse,warehouse.getId());
            if (rv!=ReturnValue.OK){
                return rv;
            }
        }
        return ReturnValue.OK;
    }


    @Override
    public FOResponse collaborativeMerge(Integer srcWarehouseId, String destWarehouseToken) {
        Warehouse srcWarehouse = findById(srcWarehouseId);
        Warehouse destWarehouse = findByToken(destWarehouseToken);
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User currentUser = userService.findByEmail(email);
        // 如果仓库不存在
        if (srcWarehouse==null||destWarehouse==null)  return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);
        // 判断当前登录用户是否有权限操作当前仓库
        // 先判断srcWarehouse是否已经是协作型仓库
        if (srcWarehouse.isCollaborative()){
            int wId = srcWarehouse.getId();
            CollaborativeWarehouse cw = null;
            Warehouse w = srcWarehouse;
            boolean havePerm = false;
            // 向上查询，是否当前用户是srcWarehouse某个super warehouse 的拥有者
            while (true){
                cw = cwService.findByWarehouseIdAndUserId(wId,currentUser.getId());
                if (cw==null){
                    if (w.isRootWarehouse()) break;
                    wId = w.getSuperWarehouseId();
                    w = findById(wId);
                    // 如果super warehouse 已经是私密型仓库
                    if (!w.isCollaborative()&&w.getHolderId()==currentUser.getId()){
                        havePerm = true;
                    }
                }else {
                    havePerm = true;
                    break;
                }
            }
            // 没有权限禁止操作
            if (!havePerm) return new FOResponse(ReturnValue.FORBIDDEN,null);
            // 参与到新的协作中，将失去原来的协作
            List<CollaborativeWarehouse> cws = cwService.findByWarehouseId(srcWarehouseId);
            for (CollaborativeWarehouse c:cws){
                c.setSuperWarehouseId(destWarehouse.getId());
                cwService.save(c);
            }

        }else {
            // 到这里，说明srcWarehouse是私密型仓库
            if (currentUser.getId() != srcWarehouse.getId()) return new FOResponse(ReturnValue.FORBIDDEN, null);
            // 转成协作型仓库
            srcWarehouse.transfer2Collaborative();
            save(srcWarehouse);

            int holderId = srcWarehouse.getHolderId();
            CollaborativeWarehouse cw = new CollaborativeWarehouse();
            cw.setUserId(holderId);
            cw.setWarehouseId(srcWarehouseId);
            cw.setSuperWarehouseId(srcWarehouse.getSuperWarehouseId());
            cwService.save(cw);
        }

        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public Warehouse findByToken(String token) {
        boolean r= ChineseWordUtil.isChineseToken(token);
        if (r){
            return findByChineseToken(token);
        }else {
            return findByUuid(token);
        }
    }

    @Override
    public Warehouse findByChineseToken(String token) {
        return wareHouseRepo.findByChineseToken(token);
    }

    public void save(Warehouse warehouse){
        wareHouseRepo.save(warehouse);
    }
}
