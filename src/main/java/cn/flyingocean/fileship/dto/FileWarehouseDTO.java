package cn.flyingocean.fileship.dto;

import cn.flyingocean.fileship.domain.File;
import cn.flyingocean.fileship.domain.Warehouse;

import java.util.Date;

public class FileWarehouseDTO {
    // 文件ID 当type=TYPE_WAREHOUSE时，该字段不设置
    private int fileId;
    // 文件仓库ID 当type=TYPE_FILE时，该字段指示该File所属的文件仓库
    // 当type=TYPE_WAREHOUSE时，该字段是该文件仓库的ID
    private int warehouseId;
    // 文件名
    private String name;
    // 上传日期
    private Date createdDate;
    // 上传者Id
    private int uploaderId;
    // 持有者ID
    private int holderId;
    // 文件状态
    private int status;
    // UUID
    private String uuid;
    // 中文文件码（或称文件令牌）
    private String chineseToken;
    // 文件仓库ID
    // type
    private int type;

    // 类型指示常量
    public static final int TYPE_FILE = 0;
    public static final int TYPE_WAREHOUSE = 1;

    public FileWarehouseDTO() {
    }

    /**
     * 根据Warehouse填充自己
     * @param wareHouse
     * @return
     */
    public static FileWarehouseDTO buildFromWarehouse(Warehouse wareHouse){
        FileWarehouseDTO ret = new FileWarehouseDTO();
        ret.setCreatedDate(wareHouse.getCreatedDate());
        ret.setHolderId(wareHouse.getHolderId());
        ret.setUuid(wareHouse.getUuid());
        ret.setWarehouseId(wareHouse.getId());
        ret.setType(TYPE_WAREHOUSE);
        ret.setChineseToken(wareHouse.getChineseToken());
        ret.setName(wareHouse.getWarehouseName());
        return ret;
    }

    /**
     * 根据File填充自己
     * @param file
     * @return
     */
    public static FileWarehouseDTO buildFromFile(File file){
        FileWarehouseDTO ret = new FileWarehouseDTO();
        ret.setName(file.getFilename());
        ret.setChineseToken(file.getChineseToken());
        ret.setType(TYPE_FILE);
        ret.setWarehouseId(file.getWarehouseId());
        ret.setUuid(file.getUuid());
        ret.setHolderId(file.getHolderId());
        ret.setCreatedDate(file.getCreatedDate());
        ret.setFileId(file.getId());
        ret.setStatus(file.getStatus());
        return ret;
    }

    public int getHolderId() {
        return holderId;
    }

    public void setHolderId(int holderId) {
        this.holderId = holderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChineseToken() {
        return chineseToken;
    }

    public void setChineseToken(String chineseToken) {
        this.chineseToken = chineseToken;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
