package cn.flyingocean.fileship.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class) // 启用JPA @CreatedDate 等注解
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // 文件名
    @NotNull
    @Column(length = 255)
    private String filename;
    // 上传日期
    @CreatedDate
    private Date createdDate;
    // 上传者Id
    @NotNull
    private int uploaderId;
    // 持有者ID
    @NotNull
    private int holderId;
    // 文件状态
    private int status;
    // UUID
    private String uuid;
    // 中文文件码（或称文件令牌）
    private String chineseToken;
    // 所属仓库ID
    @Column(insertable=false,columnDefinition = "int default 0")
    int warehouseId;


    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getChineseToken() {
        return chineseToken;
    }

    public void setChineseToken(String chineseToken) {
        this.chineseToken = chineseToken;
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

    public int getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        createdDate = createdDate;
    }
}
