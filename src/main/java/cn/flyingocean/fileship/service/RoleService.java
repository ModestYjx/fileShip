package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.Role;

public interface RoleService {
    /**
     * 凭ID查找Role
     * @param roleId
     * @return 没有找到返回null
     */
    Role findById(int roleId);
}
