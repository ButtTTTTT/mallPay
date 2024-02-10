package top.lhit.mall;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import top.lhit.mall.common.emums.RoleEnum;
import top.lhit.mall.module.pojo.User;
import top.lhit.mall.module.service.ICategoryService;
import top.lhit.mall.module.service.IUserService;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class MallApplicationTests {

    @Autowired
    private IUserService userService;

    @Test
    public void register() {
        User user = new User("jack", "123456", "123@qq.com", RoleEnum.ADMIN.getCode());
        userService.register(user);
    }
    @Resource
    private ICategoryService categoryService;
    @Test
    public void testFindSubCategoryId() {
        Set<Integer> set = new HashSet<>();
        categoryService.findSubCategoryId(100001,set);
        log.info("set={}",set);
    }
}
