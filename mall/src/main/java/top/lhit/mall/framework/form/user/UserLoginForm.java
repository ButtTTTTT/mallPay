package top.lhit.mall.framework.form.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
//用于登录
public class UserLoginForm {
    //    @NotBlank 用于String 判断空格 是否有空格
//    @NotEmpty 用于判断集合是否为空
//    @NotNull  用于判断是否为空
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
