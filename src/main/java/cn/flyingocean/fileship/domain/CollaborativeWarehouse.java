package cn.flyingocean.fileship.domain;


import javax.persistence.*;

@Entity
public class CollaborativeWarehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 仓库ID
    private int warehouseId;
    // 用户ID
    private int userId;
    // 父仓库ID
    private int superWarehouseId;
    // 权限类型，保留字段
    private byte perm;

    /**
     * 判断当前仓库是否为 root warehouse
     * @return
     */
    public boolean isRootWarehouse(){
        return superWarehouseId==0?true:false;
    }
    public int getSuperWarehouseId() {
        return superWarehouseId;
    }
    public void setSuperWarehouseId(int superWarehouseId) {
        this.superWarehouseId = superWarehouseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte getPerm() {
        return perm;
    }

    public void setPerm(byte perm) {
        this.perm = perm;
    }
}
