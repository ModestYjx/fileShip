package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.NameList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NameListRepository extends JpaRepository<NameList,Integer>{

    List<NameList> findByHolderId(int holderId);
}
