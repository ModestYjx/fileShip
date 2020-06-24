package cn.flyingocean.fileship.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RolePerm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // 角色ID
    private int roleId;
    // 权限ID
    private int permId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getPermId() {
        return permId;
    }

    public void setPermId(int permId) {
        this.permId = permId;
    }
}
