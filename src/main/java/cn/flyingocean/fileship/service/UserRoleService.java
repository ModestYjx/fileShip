package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.UserRole;

import java.util.List;

public interface UserRoleService{

    /**
     * 凭userId查询UserRole
     * @param userId
     * @return
     */
    List<UserRole> findByUserId(int userId);
}
