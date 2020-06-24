package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.File;
import cn.flyingocean.fileship.domain.FileClaim;
import cn.flyingocean.fileship.domain.NameList;
import cn.flyingocean.fileship.domain.User;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.repository.NameListRepository;
import cn.flyingocean.fileship.service.NameListService;
import cn.flyingocean.fileship.service.UserService;
import cn.flyingocean.fileship.util.ExcelUtil;
import cn.flyingocean.fileship.util.FileUtil;
import cn.flyingocean.fileship.util.UuidUtil;
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
import java.util.Optional;

@Service
public class NameListServiceImpl implements NameListService{
    @Autowired
    private NameListRepository nameListRepo;
    @Autowired
    private UserService userService;

    @Override
    public FOResponse upload(MultipartFile file) {
        String email = SecurityUtils.getSubject().getPrincipal().toString();
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
        // 持久化 NameList
        NameList nameList = new NameList();
        nameList.setHolderId(user.getId());
        nameList.setName(file.getOriginalFilename());
        nameList.setUuid(uuid);

        nameListRepo.save(nameList);

        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public FOResponse doDownload(HttpServletRequest request, HttpServletResponse response, Integer nameListId) {
        NameList nameList = findById(nameListId);
        // 如果文件找不到
        if (nameList==null) return new FOResponse(ReturnValue.NOT_FOUND_FILE,null);

        String nameListName = nameList.getName();// 名单名
        // 设置文件路径
        java.io.File retFile = FileUtil.getFile(String.valueOf(nameList.getHolderId()),nameList.getUuid(),nameListName);

        // 如果文件存在
        if (retFile.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + nameListName);// 设置文件名
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
    public NameList findById(int id) {
        Optional<NameList> nameListOptional = nameListRepo.findById(id);
        if (nameListOptional.isPresent()){
            return nameListOptional.get();
        }
        return null;
    }

    @Override
    public FOResponse list() {
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByEmail(email);

        return new FOResponse(ReturnValue.OK,nameListRepo.findByHolderId(user.getId()));
    }

    @Override
    public FOResponse fetchHeader(Integer nameListId) {
        // 检查该名单是否存在
        NameList nameList = findById(nameListId);
        // 检查当前登录用户是否为该名单的所有者
        String email = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findById(nameList.getHolderId());
        if (user.getEmail().equals(email)==false)   return new FOResponse(ReturnValue.FORBIDDEN,null);
        java.io.File file = FileUtil.getFile(String.valueOf(user.getId()),nameList.getUuid(),nameList.getName());

        ExcelUtil.ParseResult parseResult = null;
        try {
            parseResult = ExcelUtil.fetchHeader(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return new FOResponse(ReturnValue.ERROR_EXCEL_FETCH_HEADER,null);
        }
        return new FOResponse(ReturnValue.OK,parseResult.header);

    }


}
