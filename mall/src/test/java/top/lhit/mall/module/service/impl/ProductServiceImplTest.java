package top.lhit.mall.module.service.impl;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.lhit.mall.MallApplicationTests;
import top.lhit.mall.common.emums.ResponseEnum;
import top.lhit.mall.module.service.IProductService;
import top.lhit.mall.module.vo.ProductDetailVo;
import top.lhit.mall.module.vo.ResponseVo;

public class ProductServiceImplTest extends MallApplicationTests {
        @Autowired
        private IProductService productService;

        @Test
    public void testDetail() {
            ResponseVo<ProductDetailVo> detail = productService.detail(26);
            Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),detail.getStatus());

        }
}