package top.lhit.mall.module.service.impl;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.lhit.mall.MallApplicationTests;
import top.lhit.mall.common.emums.ResponseEnum;
import top.lhit.mall.framework.form.shipping.ShippingForm;
import top.lhit.mall.module.service.IShippingService;
import top.lhit.mall.module.vo.ResponseVo;
import java.util.Map;
@Slf4j
public class ShippingServiceImplTest extends MallApplicationTests {
    private Gson gson = new GsonBuilder().create();
    @Autowired
    private IShippingService service;
    @Test
    public void testAdd() {
        ShippingForm shippingForm = new ShippingForm();
        shippingForm.setReceiverName("林海");
        shippingForm.setReceiverAddress("福州闽侯");
        shippingForm.setReceiverCity("福州");
        shippingForm.setReceiverMobile("12312312311");
        shippingForm.setReceiverPhone("13599966202");
        shippingForm.setReceiverProvince("福州");
        shippingForm.setReceiverDistrict("闽侯");
        shippingForm.setReceiverZip("350100");
        ResponseVo<Map<String, Integer>> add = service.add(1, shippingForm);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),add.getStatus());
        log.info("add={}",gson.toJson(add));
    }
    @Test
    public void testDel() {
        ResponseVo del = service.del(1, 1);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),del.getStatus());
        log.info("del={}",gson.toJson(del));
    }
    @Test
    public void testUpdate() {
                ShippingForm shippingForm = new ShippingForm();
        shippingForm.setReceiverName("1213123");
        shippingForm.setReceiverAddress("福州闽侯");
        shippingForm.setReceiverCity("福州");
        shippingForm.setReceiverMobile("12312312311");
        shippingForm.setReceiverPhone("13599966202");
        shippingForm.setReceiverProvince("福州");
        shippingForm.setReceiverDistrict("闽侯");
        shippingForm.setReceiverZip("350100");
        ResponseVo del = service.update(1, 2,shippingForm);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),del.getStatus());
        log.info("del={}",gson.toJson(del));
    }
    @Test
    public void testList(){
        ResponseVo<PageInfo> list = service.list(1, 1, 10);
        log.info("list{}=",list);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),list.getStatus());
    }
}