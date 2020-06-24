package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Integer>{
    @Query("select o from File o where o.id=?1")
    File findById2(int id);

    List<File> findFilesByHolderId(int holderId);

    File findByUuid(String uuid);

    List<File> findByWarehouseId(int warehouseId);

    List<File> findByHolderIdAndWarehouseId(int holderId,int warehouseId);

    File findByChineseToken(String token);

}
