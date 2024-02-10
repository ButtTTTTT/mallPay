package top.lhit.mall.module.service;

import top.lhit.mall.module.pojo.User;
import top.lhit.mall.module.vo.ResponseVo;

public interface IUserService {
    /**
     * 注册
     * @param user
     */
    ResponseVo<User> register(User user);
    ResponseVo<User> login(String username,String password);

    /**
     * 登录
     */

}
