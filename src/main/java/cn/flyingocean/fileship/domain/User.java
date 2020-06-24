package cn.flyingocean.fileship.domain;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.validated.UserSignIn;
import cn.flyingocean.fileship.validated.UserSignUp;
import org.hibernate.annotations.Parent;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户
 */
@Entity

public class User implements Serializable{
    // 自增id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // 昵称
    @Pattern(regexp = "^.{4,14}$",groups = UserSignUp.class)
    private String nickname;
    // 密码
    @Pattern(regexp = "^[0-9a-zA-Z!@#$%^&*.]{6,25}$",groups = UserSignUp.class)
    @Column(length = 32)
    private String password;
    // 邮箱
    @Email
    @Column(unique = true,length = 30)
    private String email;
    // 电话号码
    @Pattern(regexp = "^\\d{11}$",groups = UserSignUp.class)
    @Column(length = 11)
    private String phoneNumber;
    // 所属用户组
    private byte userGroup;
    // 用户状态
    private int status;
    // uuid,仅用于注册激活使用
    @Column(length = 32)
    private String uuid;
    // 创建日期
    @CreatedDate
    private Date createdDate;
    // root 文件仓库id
    private int rootWarehouseId;

    public int getRootWarehouseId() {
        return rootWarehouseId;
    }

    public void setRootWarehouseId(int rootWarehouseId) {
        this.rootWarehouseId = rootWarehouseId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(byte userGroup) {
        this.userGroup = userGroup;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
