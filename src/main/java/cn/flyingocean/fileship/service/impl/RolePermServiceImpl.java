package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.domain.Role;
import cn.flyingocean.fileship.domain.RolePerm;
import cn.flyingocean.fileship.repository.RolePermRepository;
import cn.flyingocean.fileship.service.RolePermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermServiceImpl implements RolePermService {
    @Autowired
    RolePermRepository rolePermRepo;

    @Override
    public List<RolePerm> findByRoleId(int roleId) {
        return rolePermRepo.findByRoleId(roleId);
    }
}
