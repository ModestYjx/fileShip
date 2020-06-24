package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.FileClaim;

public interface FileClaimService {
    /**
     * 持久化FileClaim
     * @param fileClaim
     */
    void save(FileClaim fileClaim);

    /**
     * 凭ID查找FileClaim
     * @param id
     * @return 找不到返回 null
     */
    FileClaim findById(int id);

    /**
     * 凭ID删除FileClaim
     * @param id
     * @return
     */
    void deleteById(int id);

    /**
     * 凭 FileId 查找认领凭证
     * @param fileId
     * @return
     */
    FileClaim findByFileId(int fileId);
}
