package cn.flyingocean.fileship.util;

import cn.flyingocean.fileship.domain.ChineseWord;
import com.hankcs.hanlp.HanLP;

import java.util.List;

public class ChineseWordUtil {
    public static class Result{
        public Result(List<ChineseWord> chineseWordList, String chineseToken) {
            this.chineseWordList = chineseWordList;
            this.chineseToken = chineseToken;
        }

        public List<ChineseWord> chineseWordList;
        public String chineseToken;
    }

    /**
     * 汉字转带声调的拼音
     * @param text
     * @return
     */
    public static String convert2Pinyin(String text){
        return HanLP.convertToPinyinList(text).get(0).getPinyinWithToneMark();
    }


    /**
     * 判断一个字符是否是汉字
     * PS：中文汉字的编码范围：[\u4e00-\u9fa5]
     *
     * @param c 需要判断的字符
     * @return 是汉字(true), 不是汉字(false)
     */
    public static boolean isChineseToken(char c) {
        return String.valueOf(c).matches("[\u4e00-\u9fa5]");
    }

    /**
     * 判断一个字符是否是汉字
     * PS：中文汉字的编码范围：[\u4e00-\u9fa5]
     *
     * @param s 需要判断的字符
     * @return 是汉字(true), 不是汉字(false)
     */
    public static boolean isChineseToken(String s) {
        return s.matches("[\u4e00-\u9fa5]");
    }

}
