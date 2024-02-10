package top.lhit.mall.module.service;

import com.github.pagehelper.PageInfo;
import top.lhit.mall.module.vo.OrderVo;
import top.lhit.mall.module.vo.ResponseVo;

public interface IOrderService {
    ResponseVo<OrderVo> create(Integer uId,Integer shippingId);
    ResponseVo<OrderVo> detail(Integer uId,Long orderNo);
    ResponseVo<PageInfo>  list(Integer uId, Integer pageNum, Integer pageSize);
    ResponseVo cancel(Integer uId, Long orderNo);

}
