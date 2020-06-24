package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.Perm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermRepository extends JpaRepository<Perm,Integer>{

}
