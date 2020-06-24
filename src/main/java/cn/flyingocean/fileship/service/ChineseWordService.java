package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.domain.ChineseWord;
import cn.flyingocean.fileship.util.ChineseWordUtil;


public interface ChineseWordService {

    /**
     * 凭 ID 查找
     * @param id
     * @return
     */
    ChineseWord findById(int id);

    /**
     * 凭汉字查找
     * @param text
     * @return
     */
    ChineseWord findByText(String text);

    /**
     * 获取指定长度可用的汉字码
     * @param length
     * @return 出错了才返回null
     */
    ChineseWordUtil.Result getAvailableFileChineseToken(byte length);

    /**
     * 获取指定长度可用的汉字码
     * @param length
     * @return 出错了才返回null
     */
    ChineseWordUtil.Result getAvailableWarehouseChineseToken(byte length);
}
