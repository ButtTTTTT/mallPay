package top.lhit.mall.module.service;

import com.github.pagehelper.PageInfo;
import top.lhit.mall.framework.form.shipping.ShippingForm;
import top.lhit.mall.module.pojo.Shipping;
import top.lhit.mall.module.vo.ResponseVo;

import java.util.Map;

public interface IShippingService {

    ResponseVo<Map<String,Integer>> add(Integer uid, ShippingForm shippingForm);

    ResponseVo del(Integer uid, Integer shippingId);

    ResponseVo update(Integer uid, Integer shippingId, ShippingForm shippingForm);

    ResponseVo<PageInfo> list(Integer uid, Integer pageNum,Integer pageSize);

    Shipping getOrderShipping(Integer uid,Integer shippingId);
}
