package cn.flyingocean.fileship.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class) // 启用JPA @CreatedDate 等注解
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 仓库名
    private String warehouseName;
    // 仓库中的文件的命名约束
    // 这里字段不能使constraint，它是mysql关键字，导致建表失败
    private String nameConstraint;
    // uuid
    @Column(length = 32,unique = true)
    private String uuid;
    // 所属者id
    private int holderId;
    // 中文文件码
    @Column(length = 25)
    private String chineseToken;
    // 上传日期
    @CreatedDate
    private Date createdDate;
    // 父仓库的ID
    private int superWarehouseId;

    /**
     * 判断当前仓库是否为 root warehouse
     * @return
     */
    public boolean isRootWarehouse(){
        return superWarehouseId==0?true:false;
    }

    /**
     * 0判断当前仓库是否为 collaborative warehouse
     * @return
     */
    public boolean isCollaborative(){
        return this.holderId==0?true:false;
    }

    /**
     * 转成协作型仓库
     */
    public void transfer2Collaborative(){
        this.holderId = 0;
    }
    public int getSuperWarehouseId() {
        return superWarehouseId;
    }

    public void setSuperWarehouseId(int superWarehouseId) {
        this.superWarehouseId = superWarehouseId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getNameConstraint() {
        return nameConstraint;
    }

    public void setNameConstraint(String nameConstraint) {
        this.nameConstraint = nameConstraint;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getHolderId() {
        return holderId;
    }

    public void setHolderId(int holderId) {
        this.holderId = holderId;
    }

    public String getChineseToken() {
        return chineseToken;
    }

    public void setChineseToken(String chineseToken) {
        this.chineseToken = chineseToken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
