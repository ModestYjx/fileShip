package cn.flyingocean.fileship.config.shiro;

import cn.flyingocean.fileship.constance.ReturnValue;
import cn.flyingocean.fileship.domain.*;
import cn.flyingocean.fileship.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

/**
 * 这个类是参照JDBCRealm写的，主要是自定义了如何查询用户信息，如何查询用户的角色和权限，如何校验密码等逻辑
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RolePermService rolePermService;
    @Autowired
    private PermService permService;
    @Autowired
    private UserRoleService userRoleService;

    //定义如何获取用户的角色和权限的逻辑，给shiro做权限判断
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 因为非正常退出，即没有显式调用 SecurityUtils.getSubject().logout()
        // (可能是关闭浏览器，或超时)，但此时缓存依旧存在(principals)，所以会自己跑到授权方法里。
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            doClearCache(principals);
            SecurityUtils.getSubject().logout();
            return null;
        }

        //获取登录用户名
        String principal = (String) principals.getPrimaryPrincipal();
        //查询用户名称
        User user = userService.findByEmail(principal);
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        List<UserRole> userRoleList = userRoleService.findByUserId(user.getId());

        for (UserRole userRole:userRoleList) {
            Role role = roleService.findById(userRole.getRoleId());
            //添加角色
            simpleAuthorizationInfo.addRole(role.getRoleValue());
            List<RolePerm> rolePermList = rolePermService.findByRoleId(role.getId());

            for (RolePerm rolePerm:rolePermList) {
                // 查询权限信息
                Perm perm = permService.findById(rolePerm.getPermId());
                //添加权限
                simpleAuthorizationInfo.addStringPermission(perm.getPermValue());
            }
        }
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String email = token.getPrincipal().toString();

        User userDB = userService.findByEmail(email);

        if (userDB == null) {
            throw new UnknownAccountException("邮箱找不到");
        }

        if (userDB.getStatus() == ReturnValue.ACCOUNT_NOT_ACTIVATED.getCode()){
            throw new DisabledAccountException("用户未激活");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDB.getEmail(),userDB.getPassword(), getName());
//        if (userDB.getSalt() != null) {
//            info.setCredentialsSalt(ByteSource.Util.bytes(userDB.getSalt()));
//        }

        return info;

    }



}
