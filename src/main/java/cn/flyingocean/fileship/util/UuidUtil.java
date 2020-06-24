package cn.flyingocean.fileship.util;

import java.util.UUID;

/**
 * @author flyingocean.huwei
 * @version 提供一个获取32位无分割符的uuid大写字符串的公有静态方法
 */
public class UuidUtil {
    /**
     * @return 返回32位无分割符的uuid大写字符串
     * @author flyingocean.huwei
     */
    public static String generate() {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        id = id.toUpperCase().replaceAll("-", "");//转成大写装逼点,去掉分隔的横线
        return id;
    }
}
