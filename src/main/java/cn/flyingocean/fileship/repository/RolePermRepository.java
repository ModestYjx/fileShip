package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.Role;
import cn.flyingocean.fileship.domain.RolePerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RolePermRepository extends JpaRepository<RolePerm,Integer> {
    List<RolePerm> findByRoleId(int roleId);
}
