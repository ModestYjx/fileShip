package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.Warehouse;
import cn.flyingocean.fileship.dto.FOResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface WareHouseService {
    Warehouse findById(int id);
    Warehouse findByUuid(String uuid);
    List<Warehouse> findByHolderIdAndSuperWarehouseId(int holderId,int superWarehouseId);

    /**
     * 为当前认证用户创建一个文件仓库
     * @param warehouseName
     * @param constraint 文件仓库中的文件的命名约束
     * @param superWarehouseId 父仓库ID
     * @return
     */
    FOResponse create(String warehouseName, String constraint,int superWarehouseId);


    /**
     * 仓库重命名
     * @param warehouseId
     * @param newName
     * @return
     */
    FOResponse rename(Integer warehouseId, String newName);

    /**
     * 凭 warehouseId 删除指定仓库
     * @param warehouseId
     * @return
     */
    FOResponse deleteById(int warehouseId);

    /**
     * 处理下载
     * @param request
     * @param response
     * @param warehouseToken
     * @return
     */
    FOResponse doDownload(HttpServletRequest request, HttpServletResponse response, String warehouseToken);

    /**
     * 创建root文件仓库
     * @return
     */
    int createRootWarehouse();

    /**
     * 移动地合并，不支持关联型的仓库
     * @param srcWarehouseId
     * @param destWarehouseToken
     * @return
     */
    FOResponse moveMerge(Integer srcWarehouseId, String destWarehouseToken);

    /**
     * 复制地合并
     * @param srcWarehouseId
     * @param destWarehouseToken
     * @return
     */
    FOResponse copyMerge(Integer srcWarehouseId, String destWarehouseToken);

    /**
     * 关联地合并
     * @param srcWarehouseId
     * @param destWarehouseToken
     * @return
     */
    FOResponse collaborativeMerge(Integer srcWarehouseId, String destWarehouseToken);

    /**
     * token可能是中文也可能是32位UUID
     * @param token
     * @return
     */
    Warehouse findByToken(String token);


    Warehouse findByChineseToken(String token);

    void save(Warehouse warehouse);
}
