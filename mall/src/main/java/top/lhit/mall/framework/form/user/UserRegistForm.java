package top.lhit.mall.framework.form.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
//用于注册
public class UserRegistForm {
//    @NotBlank 用于String 判断空格 是否有空格
//    @NotEmpty 用于判断集合是否为空
//    @NotNull  用于判断是否为空
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
