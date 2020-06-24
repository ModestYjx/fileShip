package cn.flyingocean.fileship.domain;

import javax.persistence.*;

@Entity
public class Perm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 权限名
    @Column(length = 30)
    private String permName;

    // 权限描述
    @Column(length = 100)
    private String permDesc;

    // 权限值（用来判断的表达式）
    @Column(length = 20)
    private String permValue;

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }

    public String getPermDesc() {
        return permDesc;
    }

    public void setPermDesc(String permDesc) {
        this.permDesc = permDesc;
    }

    public String getPermValue() {
        return permValue;
    }

    public void setPermValue(String permValue) {
        this.permValue = permValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
