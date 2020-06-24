package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.NameList;
import cn.flyingocean.fileship.dto.FOResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface NameListService {
    /**
     * 上传名单
     * @param file
     * @return
     */
    FOResponse upload(MultipartFile file);

    FOResponse doDownload(HttpServletRequest request, HttpServletResponse response, Integer nameListId);

    /**
     * 凭 ID 查找 NameList
     * @param id
     * @return
     */
    NameList findById(int id);

    /**
     * 列出当前用户所有的名单
     * @return
     */
    FOResponse list();

    /**
     * 获取表头(默认sheet=0,row=0)
     * @param integer
     * @return
     */
    FOResponse fetchHeader(Integer integer);
}
