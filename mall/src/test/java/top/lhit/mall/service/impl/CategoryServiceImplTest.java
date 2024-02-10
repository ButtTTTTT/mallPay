package top.lhit.mall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import top.lhit.mall.MallApplicationTests;
import top.lhit.mall.module.service.ICategoryService;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
@Slf4j
public class CategoryServiceImplTest extends MallApplicationTests {

    @Resource
    private ICategoryService categoryService;
    @Test
    public void testFindSubCategoryId() {
            Set<Integer> set = new HashSet<>();
            categoryService.findSubCategoryId(100001,set);
            log.info("set={}",set);
    }
}