package top.lhit.mall.module.service;
import top.lhit.mall.framework.form.cart.CartAddForm;
import top.lhit.mall.framework.form.cart.CartDeleteForm;
import top.lhit.mall.framework.form.cart.CartUpdateForm;
import top.lhit.mall.module.pojo.Cart;
import top.lhit.mall.module.vo.CartVo;
import top.lhit.mall.module.vo.ResponseVo;

import java.util.List;

public interface ICartService {
    List<Cart> listForUserCart(Integer uId);
    //每次加1的购物车
    ResponseVo<CartVo> add(Integer uId,CartAddForm cartAddForm);
    //获取购物车列表
    ResponseVo<CartVo> list(Integer uId);
    //更新购物车
    ResponseVo<CartVo> update(Integer uId, Integer productId, CartUpdateForm cartUpdateForm);

    //删除购物车商品
    ResponseVo<CartVo> del(Integer uId, CartDeleteForm cartDeleteForm);
    ResponseVo<CartVo> selectAll(Integer uId);
    ResponseVo<CartVo> unSelectAll(Integer uId);
    ResponseVo<Integer> sum(Integer uId);


}
