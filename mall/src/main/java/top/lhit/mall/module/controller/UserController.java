package top.lhit.mall.module.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import top.lhit.mall.common.consts.MallConsts;
import top.lhit.mall.common.emums.ResponseEnum;
import top.lhit.mall.framework.form.user.UserLoginForm;
import top.lhit.mall.framework.form.user.UserRegistForm;
import top.lhit.mall.module.pojo.User;
import top.lhit.mall.module.service.IUserService;
import top.lhit.mall.module.vo.ResponseVo;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import static top.lhit.mall.common.emums.ResponseEnum.PARAM_ERROR;
@Slf4j
@RestController
public class UserController {
    @Autowired
    private IUserService userService;
    @PostMapping("/user/register")
    public ResponseVo<User> regist(@Valid @RequestBody UserRegistForm userRegistForm,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("提交的参数有误,{},{}",
                    bindingResult.getFieldError().getField(),
                    bindingResult.getFieldError().getDefaultMessage());
            return ResponseVo.error(PARAM_ERROR,
                    bindingResult);
        }
        User user = new User();
        BeanUtils.copyProperties(userRegistForm, user);
        //如果项目大的话需要解耦就做一层dto用来解耦
        return userService.register(user);
    }
    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  BindingResult bindingResult,
                                  HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseVo.error(PARAM_ERROR);
        }

        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        if (userResponseVo != null) {
            //设置session
            session.setAttribute(MallConsts.CURRENT_USER, userResponseVo.getData());
        }
        return userResponseVo;
    }

    @GetMapping("/user")
    public ResponseVo<User> getUser(HttpSession session){
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
        if (null==user){
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }
        return  ResponseVo.success(user);



    }
}
