package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.*;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.repository.FileRepository;
import cn.flyingocean.fileship.service.*;
import cn.flyingocean.fileship.util.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    UserService userService;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    FileRepository fileRepo;
    @Autowired
    FileClaimService fileClaimService;
    @Autowired
    WareHouseService wareHouseService;
    @Autowired
    ChineseWordService chineseWordService;
    @Autowired
    NameListService nameListService;

    @Override
    public FOResponse doUploadSinglePersonFile(MultipartFile file, String email) {

        // 凭邮箱查询用户ID
        // todo 这里应该改成查询用户ID字段以提升性能
        User user = userService.findByEmail(email);
        if (user==null){
            return new FOResponse(ReturnValue.ACCOUNT_NOT_FOUND,null);
        }
        if (file.isEmpty()) {
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }
        // 存入本地文件
        String uuid = FileUtil.storeFile(user.getId(),file);
        // 持久化 File
        
        File newFile = new File();
        newFile.setFilename(file.getOriginalFilename()); // 设置文件名
        newFile.setStatus(ReturnValue.FILE_UNCLAIMED.getCode()); // 设置·文件状态·为无人认领
        newFile.setUploaderId(user.getId()); // 设置持有者
        newFile.setHolderId(user.getId());
        // 存在root仓库下
        newFile.setWarehouseId(user.getRootWarehouseId());
        // todo 为了偷懒省去验证文件码的可用性（不可重复），这里直接将32位uuid作为文件是不可取的
        newFile.setUuid(uuid);
        // 生成中文文件码
        ChineseWordUtil.Result result = chineseWordService.getAvailableFileChineseToken((byte) 3);
        newFile.setChineseToken(result.chineseToken);
        save(newFile);
        // 生成认领单
        String claimCode = UuidUtil.generate();
        FileClaim fileClaim = new FileClaim(claimCode,newFile.getId());
        fileClaimService.save(fileClaim);
        // 发送认领提醒邮件
        StringBuilder claimUrl = new StringBuilder();
        claimUrl.append("http://localhost/files/claim/").append(fileClaim.getId()).append("-").append(claimCode);

        String htmlText = emailUtil.initClaimEmailFromTemplate(newFile.getFilename(),newFile.getCreatedDate().toString(),
                newFile.getUuid(),newFile.getChineseToken(),claimUrl.toString());
        emailUtil.setHtmlText(htmlText);
        emailUtil.setReceiver(email);
        emailUtil.setSubject("FileShip 邮件");
        // 另起线程发送邮件
        Thread thread = new Thread(emailUtil);
        thread.start();
        return new FOResponse(ReturnValue.OK,newFile);
    }

    @Override
    public void save(File file) {
        fileRepo.save(file);
    }

    @Override
    public FOResponse doClaim(int fileClaimId,String fileClaimCode) {
        FileClaim fileClaim = fileClaimService.findById(fileClaimId);
        // 如果认领凭证找不到
        if (fileClaim==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE_CLAIM,null);
        // 认领凭证匹配
        if (fileClaim.getClaimCode().equals(fileClaimCode)){
            File file = findById(fileClaim.getFileId());
            // 如果文件找不到
            if (file==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
            // 修改文件状态为OK
            file.setStatus(ReturnValue.OK.getCode());
            save(file);
            // 移除该FileClaim
            fileClaimService.deleteById(fileClaimId);
            return new FOResponse(ReturnValue.OK,null);
        }
        return new FOResponse(ReturnValue.ERROR_FILE_CLAIM,null);
    }

    @Override
    public File findById(int id) {
        System.out.println("id: "+id);
        return fileRepo.findById2(id);
        // todo 这里不知道出了什么问题，表里明明有就是找不到
//        Optional<File> fileOptional =  fileRepo.findById(id);
//        // test
//        System.out.println("id: "+id);
//        System.out.println("fileOptional.isPresent(): "+fileOptional.isPresent());
//        // test
//        if (fileOptional.isPresent()) {
//            return fileOptional.get();
//        }
//        return null;
    }

    @Override
    public File findByUuid(String uuid) {
        return fileRepo.findByUuid(uuid);
    }

    @Override
    public FOResponse doDownload(HttpServletRequest request, HttpServletResponse response, String token) {
        File file = null;
        if (ChineseWordUtil.isChineseToken(token.charAt(0))){
            file = findByChineseToken(token);
        }else {
            file = findByUuid(token);
        }

        // 如果文件找不到
        if (file==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
        // 判断文件是否被认领
        if (file.getStatus()!=ReturnValue.OK.getCode()){
            return new FOResponse(ReturnValue.FILE_UNCLAIMED,null);
        }

        String fileName = file.getFilename();// 文件名
        // 设置文件路径
        java.io.File retFile = FileUtil.getFile(String.valueOf(file.getHolderId()),file.getUuid(),fileName);

        // 如果文件存在
        if (retFile.exists()) {
            // 取消这个配置，否则会报一堆错误
//           response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(retFile);
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
            return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
        }
        return new FOResponse(ReturnValue.NOT_ACCEPTABLE,null);
    }

    @Override
    public List<File> listByUserId(int userId) {
        return fileRepo.findFilesByHolderId(userId);
    }

    @Override
    public FOResponse doMerge() {
        return null;
    }

    @Override
    public FOResponse doJoin(int fileId, String wareHouseToken,String newFileName){
       // 检查当前用户是否持有该fileId指定的文件
        org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
        String email = subject.getPrincipal().toString();
        File srcFile = findById(fileId);
        // 如果文件不存在
        if (srcFile==null)  return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
        // 如果文件未被认领
        if (srcFile.getStatus()!= ReturnValue.OK.getCode()){
            return new FOResponse(ReturnValue.FILE_UNCLAIMED,null);
        }
        User user = userService.findById(srcFile.getHolderId());
        // 如果当前用户不是持有者
        if (!user.getEmail().equals(email)) return new FOResponse(ReturnValue.FORBIDDEN,null);
        // 文件操作
        // 如果需要重命名
        System.out.println("newFileName: "+newFileName);
        if (newFileName!=null&&!newFileName.isEmpty()){
            try {
                //test
                System.out.println(user.getId()+"-"+srcFile.getUuid()+"-"+srcFile.getFilename()+"-"+newFileName);
                FileUtil.rename(String.valueOf(user.getId()),srcFile.getUuid(),srcFile.getFilename(),newFileName);
            } catch (IOException e) {
                e.printStackTrace();
                return new FOResponse(ReturnValue.ERROR_FILE_RENAME,null);
            }

            srcFile.setFilename(newFileName);
        }
        // todo 增加中文文件码时此处需修改
        Warehouse warehouse = wareHouseService.findByUuid(wareHouseToken);

        try {
            FileUtil.join(String.valueOf(user.getId()),srcFile.getUuid(),
                    String.valueOf(warehouse.getHolderId()), warehouse.getUuid());
        } catch (IOException e) {
            e.printStackTrace();
            return new FOResponse(ReturnValue.ERROR_FILE_JOIN,null);
        }

        // 将被操作的File的所有权转给对方
        srcFile.setHolderId(warehouse.getHolderId());
        // 设置仓库ID
        srcFile.setWarehouseId(warehouse.getId());
        save(srcFile);

        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public List<File> findByWareHouseId(int wareHouseId) {
        return fileRepo.findByWarehouseId(wareHouseId);
    }

    @Override
    public List<File> findByHolderIdAndWarehouseId(int id, String warehouseId) {
        return fileRepo.findByHolderIdAndWarehouseId(id,Integer.valueOf(warehouseId));
    }

    @Override
    public List<File> findByWarehouseId(int warehouseId) {
        return fileRepo.findByWarehouseId(warehouseId);
    }

    @Override
    public FOResponse rename(int fileId, String newName) {
        File file = findById(fileId);
        // 如果文件不存在
        if (file == null)   return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
        // 如果文件未认领
        if (file.getStatus()!= ReturnValue.OK.getCode()){
            return new FOResponse(ReturnValue.FILE_UNCLAIMED,null);
        }
        // 检查该文件所属者是否为当前登录用户
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        int holderId = file.getHolderId();
        User user = userService.findById(holderId);
        if (!user.getEmail().equals(email)) return new FOResponse(ReturnValue.FORBIDDEN,null);

        // 物理文件重名
        try {
            if (file.getWarehouseId()==0){
                FileUtil.rename(String.valueOf(holderId),file.getUuid(),file.getFilename(),newName);
            }else{
                Warehouse warehouse = wareHouseService.findById(file.getWarehouseId());
                FileUtil.rename(String.valueOf(holderId),warehouse.getUuid(),file.getUuid(),file.getFilename(),newName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new FOResponse(ReturnValue.ERROR_FILE_RENAME,null);
        }

        // 数据库中File重命名
        file.setFilename(newName);
        save(file);

        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public File findByChineseToken(String token) {
        return fileRepo.findByChineseToken(token);
    }

    @Override
    public FOResponse doClaimOnline(String fileId) {
        int fi;
        try{
            fi = Integer.parseInt(fileId);
            File file = findById(fi);
            // 如果文件不存在
            if (file==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
            User user = userService.findById(file.getHolderId());
            String email = SecurityUtils.getSubject().getPrincipal().toString();
            // 检查文件所有权是否属于当前登录用户
            if (user.getEmail().equals(email)==false)   return new FOResponse(ReturnValue.FORBIDDEN,null);

            FileClaim fileClaim = fileClaimService.findByFileId(fi);
            // 如果认领凭证找不到
            if (fileClaim==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE_CLAIM,null);
            // 修改文件状态为OK
            file.setStatus(ReturnValue.OK.getCode());
            save(file);
            // 移除该FileClaim
            fileClaimService.deleteById(fileClaim.getId());
            return new FOResponse(ReturnValue.OK,null);
        }catch (NumberFormatException e){
            return new FOResponse(ReturnValue.BAD_REQUEST,null);
        }
    }

    @Override
    public FOResponse collationAndAnalysis1(int warehouseId, int nameListId, String keyColumn, String namePattern) {
        Warehouse warehouse = wareHouseService.findById(warehouseId);
        NameList nameList = nameListService.findById(nameListId);
        if (warehouse==null)  return new FOResponse(ReturnValue.NOT_FOUND_WAREHOUSE,null);
        if (nameList==null) return new FOResponse(ReturnValue.NOT_FOUND_NAMELIST,null);
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByEmail(email);
        ExcelUtil.ParseResult parseResult;
        int keyColumnIndex = -1;
        List<File> filesInWarehouse = findByWareHouseId(warehouseId);
        // 没有交的名单
        List<String[]> notFoundList = new ArrayList<>();
        try {
            parseResult = ExcelUtil.parse(FileUtil.getFile(String.valueOf(user.getId()),nameList.getUuid(),nameList.getName()).getAbsolutePath());

            // 计算匹配列的下标
            for (int i=0;i<parseResult.colNum;i++){
                if (parseResult.rMatrix[0][i].equals(keyColumn)){
                    keyColumnIndex = i;
                    break;
                }
            }
            // 解析namePattern
            String[] namePatternItems = namePattern.split("-");
            int[] namePatternIndex = new int[namePatternItems.length];
            for (int i=0;i<namePatternItems.length;i++){
                for (int j=0;j<parseResult.colNum;j++){
                    if (parseResult.rMatrix[0][j].equals(namePatternItems[i])==true){
                        namePatternIndex[i]=j;
                        break;
                    }
                }
            }
            // 如果没有找到匹配列
            if (keyColumnIndex==-1) return new FOResponse(ReturnValue.BAD_REQUEST,null);

            // 开始整理并分析
            // todo 这里的分析目前就是看看哪个弟弟没有交作业
            // 把表头加进去
            notFoundList.add(parseResult.rMatrix[0]);
            for (int i = 1;i<parseResult.rowNum;i++){
                boolean find = false;
                for(int j=0;j<filesInWarehouse.size();j++){
                    File file = filesInWarehouse.get(j);
                    if (file.getFilename().indexOf(parseResult.rMatrix[i][keyColumnIndex])!=-1){
                        find = true;
                        // 文件后缀名
                        String[] fNameParts = file.getFilename().split("\\.");
                        String fileSuffix = "." + fNameParts[fNameParts.length-1];
                        // fixme 暂时不考虑没有后缀名
                        StringBuilder newName = new StringBuilder();
                        for (int k=0;k<namePatternIndex.length;k++){
                            newName.append(parseResult.rMatrix[i][namePatternIndex[k]]);
                            if (k!=namePatternIndex.length-1) newName.append("-");
                        }
                        newName.append(fileSuffix);
                        FileUtil.rename(String.valueOf(user.getId()),warehouse.getUuid(),file.getUuid(),file.getFilename(),newName.toString());
                        file.setFilename(newName.toString());
                        fileRepo.save(file);
                        break;
                    }
                }
                if (find==false){
                    notFoundList.add(parseResult.rMatrix[i]);
                }
            }
            return new FOResponse(ReturnValue.OK,notFoundList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FOResponse(ReturnValue.ERROR_COLLATIONANDANALYSIS1,null);
    }

    @Override
    public FOResponse deleteById(Integer fileId) {
        // 检查当前操作文件所有者是否为当前登录用户
        File file = findById(fileId);
        if (file==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);
        User user = userService.findById(file.getHolderId());
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        if (email.equals(user.getEmail())==false)   return new FOResponse(ReturnValue.FORBIDDEN,null);
        // 如果该文件不在文件仓库中
        if (file.getWarehouseId()==0){
            try {
                FileUtil.deleteFile(String.valueOf(user.getId()),file.getUuid(),file.getFilename());
                FileUtil.deleteFileDir(String.valueOf(user.getId()),file.getUuid());
            } catch (IOException e) {
                e.printStackTrace();
                return new FOResponse(ReturnValue.ERROR_DELETE_FILE,null);
            }
        }else{
            Warehouse warehouse = wareHouseService.findById(file.getWarehouseId());
            try {
                FileUtil.deleteFile(String.valueOf(user.getId()),warehouse.getUuid(),file.getUuid(),file.getFilename());
                FileUtil.deleteFileDir(String.valueOf(user.getId()),file.getUuid());
            } catch (IOException e) {
                e.printStackTrace();
                return new FOResponse(ReturnValue.ERROR_DELETE_FILE,null);
            }
        }

        FileClaim fileClaim = fileClaimService.findByFileId(fileId);
        if (fileClaim!=null)    fileClaimService.deleteById(fileClaim.getId());

        fileRepo.deleteById(fileId);
        return new FOResponse(ReturnValue.OK,null);
    }


}
