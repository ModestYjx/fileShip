package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.RolePerm;

import java.util.List;

public interface RolePermService {
    /**
     * 凭roleId查询RolePerm
     * @param roleId
     * @return
     */
    List<RolePerm> findByRoleId(int roleId);
}
