package top.lhit.mall.module.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.lhit.mall.common.emums.RoleEnum;
import top.lhit.mall.module.mapper.UserMapper;
import top.lhit.mall.module.pojo.User;
import top.lhit.mall.module.service.IUserService;
import top.lhit.mall.module.vo.ResponseVo;
import java.nio.charset.StandardCharsets;
import static top.lhit.mall.common.emums.ResponseEnum.*;
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ResponseVo<User> register(User user) {
        //username 不能重复
           int countByUsername = userMapper.countByUsername(user.getUsername());
           if (countByUsername > 0){
              return ResponseVo.error(USERNAME_EXIST);
           }
           //email不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
           if (countByEmail>0){
               return ResponseVo.error(EMAIL_EXIST);
           }
           user.setRole(RoleEnum.CUSTOMER.getCode());
           //MD5加密 摘要算法(Spring自带了)
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
           //写入数据库
        int res = userMapper.insertSelective(user);
        if (res==0){
            return ResponseVo.error(ERROR);
        }
        return  ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user==null){
            //用户不存在(返回： 用户名/密码错误)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        if (!user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            //密码不正确(返回： 用户名/密码错误)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
            //错误信息是 用户名/密码错误
        user.setPassword("");
        return ResponseVo.success(user);
    }
}
