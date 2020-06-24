package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.Perm;

public interface PermService {
    /**
     * 凭id查询 Perm
     * @param id
     * @return
     */
    Perm findById(int id);
}
