package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {
    List<UserRole> findByUserId(int userId);
}
