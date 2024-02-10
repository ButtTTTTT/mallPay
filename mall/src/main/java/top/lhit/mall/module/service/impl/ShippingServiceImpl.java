package top.lhit.mall.module.service.impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lhit.mall.common.emums.ResponseEnum;
import top.lhit.mall.framework.form.shipping.ShippingForm;
import top.lhit.mall.module.mapper.ShippingMapper;
import top.lhit.mall.module.pojo.Shipping;
import top.lhit.mall.module.service.IShippingService;
import top.lhit.mall.module.vo.ResponseVo;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.lhit.mall.common.emums.ResponseEnum.DELETE_SHIPPING_FAIL;

@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVo<Map<String,Integer>> add(Integer uid, ShippingForm shippingForm) {

        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingForm,shipping);
        shipping.setUserId(uid);
        shipping.setCreateTime(new Date());
        int insert = shippingMapper.insert(shipping);
        if (insert == 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        Map<String,Integer> map = new HashMap<>();
        map.put("shippingId",shipping.getId());

        return ResponseVo.success(map);
    }
    @Override
    public ResponseVo del(Integer uid, Integer shippingId) {
        int i = shippingMapper.deleteByIdAndUid(uid, shippingId);
        if (i==0){
            return ResponseVo.error(DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.successByMsg("成功");
    }
    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm shippingForm) {
        Shipping shipping = new Shipping();
        shipping.setId(shippingId);
        shipping.setUserId(uid);
        BeanUtils.copyProperties(shippingForm,shipping);
        int i = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (i==0){
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }
        return ResponseVo.successByMsg("成功");
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper pageHelper = new PageHelper();
        pageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings = shippingMapper.selectByUid(uid);
        PageInfo pageInfo = new PageInfo(shippings);

        return ResponseVo.success(pageInfo) ;
    }

    @Override
    public Shipping getOrderShipping(Integer uid, Integer shippingId) {
        return shippingMapper.selectByUidAndShippingId(uid,shippingId);
    }
}
