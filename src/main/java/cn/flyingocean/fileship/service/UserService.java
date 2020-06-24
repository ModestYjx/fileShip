package cn.flyingocean.fileship.service;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.User;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    /**
     * 处理注册
     * @param user
     * @param request
     */
    FOResponse doSignUp(User user, HttpServletRequest request);

    /**
     * 处理激活账户
     * @param uuid
     * @param id
     * @return
     */
    FOResponse doActivate(String uuid,int id);


    /**
     * 凭email查找用户
     * @param email
     * @return
     */
    User findByEmail(String email);

    /**
     * 保存或更新 User
     * @param user
     */
    void save(User user);

    /**
     * 处理登录请求
     * @param user
     * @return
     */
    FOResponse doSignIn(User user,boolean isRememberMe);

    /**
     * 凭ID查找User
     * @param id
     * @return 找不到返回null
     */
    User findById(int id);
}
