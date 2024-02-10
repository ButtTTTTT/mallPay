package top.lhit.mall.module.mapper;
import org.apache.ibatis.annotations.Mapper;
import top.lhit.mall.module.pojo.User;

@Mapper
public interface UserMapper {
    int countByEmail(String email);
    Integer countByUsername(String username);

    Integer insertSelective(User user);
    User selectByUsername(String username);
}
