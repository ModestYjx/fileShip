package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<Warehouse,Integer>{

    Warehouse findByUuid(String uuid);

    List<Warehouse> findByHolderIdAndSuperWarehouseId(int holder,int superWarehouseId);

    Warehouse findByChineseToken(String warehouseToken);
}
