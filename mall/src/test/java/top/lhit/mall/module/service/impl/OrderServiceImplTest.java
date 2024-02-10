package top.lhit.mall.module.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.lhit.mall.MallApplicationTests;
import top.lhit.mall.module.service.IOrderService;
import top.lhit.mall.module.vo.OrderVo;
import top.lhit.mall.module.vo.ResponseVo;

@Slf4j
public class OrderServiceImplTest extends MallApplicationTests {
    @Autowired
    private IOrderService orderService;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Integer uId = 1;
    private Integer shippingId = 2;

    @Test
    public void testCancel() {
            orderService.cancel(1,1696252367958l);
    }
    @Test
    public void testCreate() {
        ResponseVo<OrderVo> orderVoResponseVo = orderService.create(uId, shippingId);
        log.info("result={}", orderVoResponseVo);
    }

    @Test
    public void testList() {
        ResponseVo<PageInfo> orderVoResponseVo = orderService.list(uId, 1, 10);
        log.info("result={}", gson.toJson(orderVoResponseVo));
//        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), orderVoResponseVo.getStatus());
    }

}