package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.CollaborativeWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborativeWarehouseRepository extends JpaRepository<CollaborativeWarehouse,Integer>{
    CollaborativeWarehouse findByWarehouseIdAndAndUserId(int warehouseId, int userId);
    List<CollaborativeWarehouse> findByWarehouseId(int warehouseId);
}
