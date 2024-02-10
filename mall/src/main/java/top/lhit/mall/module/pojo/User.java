package top.lhit.mall.module.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String question;
    private String answer;
    private Integer role;
    private Date createTime;
    private Date updateTime;
    public User(String username, String password, String email,Integer role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role=role;
    }
}
