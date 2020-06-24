package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.FileClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileClaimRepository extends JpaRepository<FileClaim,Integer>{
    FileClaim findByFileId(int fileId);
}
