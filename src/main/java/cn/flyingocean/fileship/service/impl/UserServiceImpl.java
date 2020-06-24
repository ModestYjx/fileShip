package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.User;
import cn.flyingocean.fileship.dto.FOResponse;
import cn.flyingocean.fileship.repository.UserRepository;
import cn.flyingocean.fileship.service.UserService;
import cn.flyingocean.fileship.service.WareHouseService;
import cn.flyingocean.fileship.util.EmailUtil;
import cn.flyingocean.fileship.util.FileUtil;
import cn.flyingocean.fileship.util.UuidUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private WareHouseService wareHouseService;
    @Override
    public FOResponse doSignUp(User user,HttpServletRequest request) {
        // 用户状态判断
        // 如果用户已注册则返回
        if (userRepo.findByEmail(user.getEmail()) != null) {
            return new FOResponse(ReturnValue.ACCOUNT_EMAIL_REGSITED, null);
        }
        // MD5加密 迭代一次
        user.setPassword(  new Md5Hash(user.getPassword()).toString()  );
        // 生产注册激活使用的UUID，等待用户激活
        String uuid = UuidUtil.generate();
        user.setUuid(uuid);
        // fixme 暂时不需要激活操作
//        user.setStatus(ReturnValue.ACCOUNT_NOT_ACTIVATED.getCode());
        user.setStatus(ReturnValue.OK.getCode());
        // 创建root仓库
        int rootWarehouseId = wareHouseService.createRootWarehouse();
        user.setRootWarehouseId(rootWarehouseId);
        // 持久化用户注册信息
        userRepo.save(user);
        // 创建用户文件夹 fixme 这一步应该在激活操作里，测试阶段就先放这里
        FileUtil.createNewUserDirectory(user.getId());
        // 发送注册邮件
        StringBuilder basePath = new StringBuilder("请点击链接以激活您的FileShip账号，如果不是本人操作，请忽略本邮件。 <br/>");
        basePath.append(request.getScheme()).append("://").append(request.getServerName())
                .append(":").append(request.getServerPort()).append("/user/activate/")
                .append(user.getUuid()).append("/").append(user.getId());
        emailUtil.setHtmlText(basePath.toString());
        emailUtil.setSubject("Fileship 激活邮件");
        emailUtil.setReceiver(user.getEmail());
        // 异步发送邮件
        Thread thread = new Thread(emailUtil);
        thread.start();

        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public FOResponse doActivate(String uuid, int id) {

        Optional<User> userOptional  = userRepo.findById(id);
        // 如果id不存在，则返回
        if (!userOptional.isPresent()) {
            return new FOResponse(ReturnValue.ACTIVATE_FAILED,null);
        }

        User user = userOptional.get();
        // 如果uuid不符合，则返回
        if (!user.getUuid().equals(uuid)) {
            return new FOResponse(ReturnValue.ACTIVATE_FAILED,null);
        }
        // 将uuid字段置空节约空间
        user.setUuid("");
        user.setStatus(ReturnValue.OK.getCode());
        userRepo.save(user);

        // 创建用户文件
      //  FileUtil.createNewUserDirectory(user.getId());

        return new FOResponse(ReturnValue.ACTIVATE_SUCCESS,null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void save(User user) {
        userRepo.save(user);
    }


    @Override
    public FOResponse doSignIn(User user,boolean isRememberMe) {
        //test
        System.err.println("isRememberMe"+isRememberMe);
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getEmail(), user.getPassword());
        if (isRememberMe){
            token.setRememberMe(true);
        }else {
            token.setRememberMe(false);
        }
        try {
            currentUser.login(token);
        } catch (UnknownAccountException e) {
            return new FOResponse(ReturnValue.ACCOUNT_NOT_FOUND,null);
        } catch (DisabledAccountException e){
            return new FOResponse(ReturnValue.ACCOUNT_NOT_ACTIVATED,null);
        } catch (IncorrectCredentialsException e){
            return new FOResponse(ReturnValue.ERROR_ACC_OR_PAS,null);
        }
        return new FOResponse(ReturnValue.OK,null);
    }

    @Override
    public User findById(int id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()){
            return userOptional.get();
        }
        return null;
    }
}
