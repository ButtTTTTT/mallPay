package top.lhit.mall.service.impl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.lhit.mall.MallApplicationTests;
import top.lhit.mall.module.service.IProductService;
public class ProductServiceImplTest extends MallApplicationTests {
    @Autowired
    private IProductService service;
    @Test
    public void testList() {
        service.list(100002,1,2);
    }
}