package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.domain.UserRole;
import cn.flyingocean.fileship.repository.UserRoleRepository;
import cn.flyingocean.fileship.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepo;
    @Override
    public List<UserRole> findByUserId(int userId) {
        return userRoleRepo.findByUserId(userId);
    }
}
