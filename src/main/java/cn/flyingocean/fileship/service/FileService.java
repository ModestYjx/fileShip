package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.File;
import cn.flyingocean.fileship.domain.Warehouse;
import cn.flyingocean.fileship.dto.FOResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface FileService {
    /**
     * 处理上传文件请求
     *
     * @param file
     * @param email
     * @return
     */
    FOResponse doUploadSinglePersonFile(MultipartFile file, String email);

    /**
     * 持久化文件
     *
     * @param file
     */
    void save(File file);

    /**
     * 处理认领文件请求
     *
     * @param fileClaimId
     * @param fileClaimCode
     * @return
     */
    FOResponse doClaim(int fileClaimId, String fileClaimCode);


    /**
     * 凭文件ID查找
     *
     * @param id
     * @return 找不到返回null
     */
    File findById(int id);

    /**
     * 凭文件 uuid 查找
     * @param uuid
     * @return
     */
    File findByUuid(String uuid);
    /**
     * 凭文件码 fileCode下载文件
     *
     * @param request
     * @param response
     * @param token
     * @return
     */
    FOResponse doDownload(HttpServletRequest request, HttpServletResponse response, String token);


    /**
     * 找出 userId 指定的 User 所持有的所有的 File
     *
     * @param userId
     * @return
     */
    List<File> listByUserId(int userId);

    FOResponse doMerge();

    /**
     * 将 fileId 指定的file移动到 destFileCode 指定的文件仓库
     * @param fileId
     * @param destFileCode
     * @return
     */
    FOResponse doJoin(int fileId,String destFileCode,String newFileName);


    /**
     * 凭仓库 ID 查找
     * @param wareHouseId
     * @return
     */
    List<File> findByWareHouseId(int wareHouseId);

    /**
     * 凭所有者ID和所属仓库ID查找
     * @param id
     * @param warehouseId
     * @return
     */
    List<File> findByHolderIdAndWarehouseId(int id, String warehouseId);


    /**
     * 凭所属仓库ID查找
     * @param warehouseId
     * @param warehouseId
     * @return
     */
    List<File> findByWarehouseId(int warehouseId);

    /**
     * 文件重命名
     * @param fileId
     * @param newName
     * @return
     */
    FOResponse rename(int fileId, String newName);


    /**
     * 凭 token 查找 File
     * @param token
     * @return
     */
    File findByChineseToken(String token);

    /**
     * 在线认领，
     * @param fileId
     * @return
     */
    FOResponse doClaimOnline(String fileId);

    /**
     * 按方式一整理
     * @param warehouseId
     * @param keyColumn
     * @param namePattern
     * @return
     */
    FOResponse collationAndAnalysis1(int warehouseId,int nameListId, String keyColumn, String namePattern);

    /**
     * 凭 FileId 删除指定 File
     * @param integer
     */
    FOResponse deleteById(Integer integer);
}


