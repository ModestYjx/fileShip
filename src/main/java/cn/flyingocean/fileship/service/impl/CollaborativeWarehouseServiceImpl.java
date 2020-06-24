package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.domain.CollaborativeWarehouse;
import cn.flyingocean.fileship.repository.CollaborativeWarehouseRepository;
import cn.flyingocean.fileship.service.CollaborativeWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollaborativeWarehouseServiceImpl implements CollaborativeWarehouseService {
    @Autowired
    CollaborativeWarehouseRepository associatedWarehouseRepo;


    @Override
    public void save(CollaborativeWarehouse associatedWarehouse) {
        associatedWarehouseRepo.save(associatedWarehouse);
    }

    @Override
    public CollaborativeWarehouse findByWarehouseIdAndUserId(int warehouseId, int userId){
        return associatedWarehouseRepo.findByWarehouseIdAndAndUserId(warehouseId,userId);
    }

    @Override
    public List<CollaborativeWarehouse> findByWarehouseId(int warehouseId){
        return  associatedWarehouseRepo.findByWarehouseId(warehouseId);
    }
}
