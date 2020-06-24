package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.CollaborativeWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaborativeWarehouseService{
    void save(CollaborativeWarehouse associatedWarehouse);
    CollaborativeWarehouse findByWarehouseIdAndUserId(int warehouseId, int userId);
    List<CollaborativeWarehouse> findByWarehouseId(int warehouseId);
}
