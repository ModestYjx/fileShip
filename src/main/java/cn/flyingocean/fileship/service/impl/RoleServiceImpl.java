package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.domain.Role;
import cn.flyingocean.fileship.repository.RoleRepository;
import cn.flyingocean.fileship.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepo;
    @Override
    public Role findById(int roleId) {
        Optional<Role>  roleOptional = roleRepo.findById(roleId);
        if (!roleOptional.isPresent()){
            return null;
        }
        return roleOptional.get();
    }
}
