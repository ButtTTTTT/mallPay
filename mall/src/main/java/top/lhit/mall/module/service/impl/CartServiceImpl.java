package top.lhit.mall.module.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lhit.mall.common.constant.CacheConstants;
import top.lhit.mall.common.utils.RedisCache;
import top.lhit.mall.common.utils.StringUtils;
import top.lhit.mall.framework.form.cart.CartAddForm;
import top.lhit.mall.framework.form.cart.CartDeleteForm;
import top.lhit.mall.framework.form.cart.CartUpdateForm;
import top.lhit.mall.module.mapper.ProductMapper;
import top.lhit.mall.module.pojo.Cart;
import top.lhit.mall.module.pojo.Product;
import top.lhit.mall.module.service.ICartService;
import top.lhit.mall.module.vo.CartProductVo;
import top.lhit.mall.module.vo.CartVo;
import top.lhit.mall.module.vo.ResponseVo;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static top.lhit.mall.common.emums.ProductStatusEnum.ON_SALE;
import static top.lhit.mall.common.emums.ResponseEnum.*;

@Service
@Slf4j
public class CartServiceImpl implements ICartService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisCache redisCache;
    @Override
    public List<Cart> listForUserCart(Integer uId) {
        List<Cart> carts = new ArrayList<>();
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, uId);//获取用户redis存储的key
        //查所有的购物车内的商品
        Map<String, Object> entries = redisCache.getCacheMap(redisKey);//拿到购物车所有商品map

        if (entries.size() <= 0) {//判断是否未添加商品
            return carts;
        }

        for (Map.Entry<String, Object> entry : entries.entrySet()) {//遍历所有购物车的商品
            //找到所有在购物车里面的所有商品id
            Integer productId = Integer.valueOf(entry.getKey());
            //根据商品id 反序列化生成Cart对象
            Cart cart = JSON.parseObject(JSON.toJSONString(entry.getValue()), Cart.class);
            if (cart.getProductSelected()) {
                carts.add(cart);
            }
        }
        return carts;
    }


    @Override
    public ResponseVo<CartVo> add(Integer userId, CartAddForm cartAddForm) {
        Integer quantity = 1;
        Product product = productMapper.selectByPrimaryKey(cartAddForm.getProductId());

        //判断商品是否存在
        if (product == null) {
            return ResponseVo.error(PRODUCT_NOT_EXIST);
        }
        //商品状态判断
        if (!(product.getStatus().equals(ON_SALE.getCode()))) {
            return ResponseVo.error(PRODUCT_OFF_SALE_OR_DELETE);
        }
        //商品库存是否充足
        if (product.getStock() <= 0) {
            return ResponseVo.error(PROODUCT_STOCK_ERROR);
        }
        //正常添加购物车 写入redis
        //这里是核心业务
        //key: cart_:userId
        //这个是该用户redisKey
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, userId);
        Cart cart;
        //这里是找到是否存在过了这个商品id;
        JSONObject value = (JSONObject) redisCache.getHashCache(redisKey, String.valueOf(product.getId()));

        if (null == value) {
            //如果没有商品存在
            cart = new Cart(product.getId(), quantity, cartAddForm.getSelected());
        } else {
            //如果已经存在了这个商品 old quantity + new quantity;
            //把这个对象拿出来再加上新增的数量 并放到redis中
            cart = JSON.parseObject(JSON.toJSONString(value), Cart.class);
            cart.setQuantity(cart.getQuantity() + quantity);
            redisCache.putHashCache(redisKey, String.valueOf(cart.getProductId()),
                    JSON.toJSON(cart));
            return list(userId);
        }
        /**
         * key: "cart"+userId
         * value: cart对象
         */
        //这里是商品不存购物车当中 去redis中添加新的购物车
        redisCache.setHashCacheExpire(redisKey, String.valueOf(product.getId()), JSON.toJSON(cart), 30, TimeUnit.DAYS);
        return list(userId);
    }

    @Override
    public ResponseVo<CartVo> list(Integer userId) {
        CartVo cartVo = new CartVo();//购物车vo对象
        List<Cart> carts = new ArrayList<>();
        Boolean selectAll = true;//购物车商品标识标记
        Integer cartTotalQuantity = 0;//购物车商品总数变量
        BigDecimal cartTotalPrice = BigDecimal.ZERO;//购物车总价变量
        Set<Integer> productIdSet = new HashSet<>();//创建商品idSet集合
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, userId);//获取用户redis存储的key

        Map<String, Object> entries = redisCache.getCacheMap(redisKey);//拿到购物车所有商品map
        if (entries.size() == 0) {//判断是否未添加商品
            return ResponseVo.successByMsg("未添加任何商品");
        }
        for (Map.Entry<String, Object> entry : entries.entrySet()) {//遍历所有购物车的商品
            //找到所有在购物车里面的所有商品id
            Integer productId = Integer.valueOf(entry.getKey());
            //根据商品id 反序列化生成Cart对象
            Cart cart = JSON.parseObject(JSON.toJSONString(entry.getValue()), Cart.class);
            carts.add(cart);
            //TODO 需要优化使用mysql里面的in
            //添加id到set集合当中
            productIdSet.add(productId);
        }
        //更具productIdSet来获取 购物车中的商品集合
        List<Product> products = productMapper.selectByProductIdSet(productIdSet);
        //如果商品集合中没有元素的话 直接返回未添加任何商品
        if (0 == products.size() && products == null) {
            return ResponseVo.successByMsg("未添加任何商品");
        }
        //如果商品不为空的话 继续生成一个购物车商品VO对象的集合
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        //将购物车中的商品集合添加到购物车商品VO对象集合中
        for (Product product : products) {
            for (Cart cart : carts) {
                if (cart.getProductId().equals(product.getId())) {
                    //创建一个新的购物车商品VO对象
                    CartProductVo cartProductVo = new CartProductVo(
                            product.getId(),//商品id
                            cart.getQuantity(),//商品数量
                            product.getName(),//商品名称
                            product.getSubtitle(),//商品子标题
                            product.getMainImage(),//购物车商品图片
                            product.getPrice(),//购物车单价
                            product.getStatus(),//购物车商品状态
                            //总价 单价乘以数量
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                            product.getStock(),//商品库存
                            cart.getProductSelected()//购物车商品是否被选中
                    );
                    cartTotalQuantity += cart.getQuantity();//计算总数
                    cartProductVoList.add(cartProductVo);//添加到购物车商品VO对象集合中
                    if (!cartProductVo.getProductSelected()) {//如果this商品没有被选中 则将selected = false
                        selectAll = false;
                    } else {
                        //只计算选中的
                        cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());//进行累加商品
                    }
                }
            }
        }
        //只计算选中的商品
        cartVo.setCartTotalPrice(cartTotalPrice);
        //计算购物车总数量
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        //有一个没有选中就没有全选
        cartVo.setSelectAll(selectAll);
        //添加所有购物车商品列表
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    /**
     * 更新逻辑
     *
     * @param userId         用户id
     * @param productId      商品id
     * @param cartUpdateForm 更新表单
     * @return list(userId)
     */
    @Override
    public ResponseVo<CartVo> update(Integer userId, Integer productId, CartUpdateForm cartUpdateForm) {
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, userId);//根据用户id获取redis中该用户购物车的key
        //根据key来获取cart对象
        Cart cart = JSON.parseObject(JSON.toJSONString(redisCache.getHashCache(redisKey, String.valueOf(productId))), Cart.class);
        if (null == cart) {
            //没有该商品，报错
            return ResponseVo.error(CART_PRODUCT_NOT_EXIST);
        }
        //有的话修改redis中的内容
        //数量不为空 并且 不能小于等于0
        if (!(null == cart.getQuantity() && cart.getQuantity() <= 0)) {
            //将该Cart对象进行set注入
            cart.setQuantity(cartUpdateForm.getQuantity());
        }
        if (cart.getProductSelected() != null && cart.getProductSelected()) {
            cart.setProductSelected(cartUpdateForm.getSelected());
        }

        //最终重新将Cart对象写入redis中
        redisCache.putHashCache(redisKey, String.valueOf(productId), cart);
        return list(userId);
    }

    @Override
    public ResponseVo<CartVo> del(Integer userId, CartDeleteForm cartDeleteForm) {
        Set<Integer> productIds = cartDeleteForm.getProductIds();
        log.info(cartDeleteForm.toString());
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, userId);//根据用户id获取redis中该用户购物车的key
        if (productIds.size() > 0) {
            for (Integer productId : productIds) {
                Cart cart = JSON.parseObject(JSON.toJSONString(redisCache.getHashCache(redisKey, String.valueOf(productId))), Cart.class);
                //根据key来获取cart对象
                if (null == cart) {
                    //没有该商品，报错
                    return ResponseVo.error(CART_PRODUCT_NOT_EXIST);
                }
                //最终重新将Cart对象写入redis中
                redisCache.delCacheMapValue(redisKey, String.valueOf(productId));
            }
        }
        return list(userId);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uId) {
        CartVo cartVo = new CartVo();//购物车vo对象
        List<Cart> carts = new ArrayList<>();//获取购物车Cart对象的集合
        Integer cartTotalQuantity = 0;//购物车商品总数变量
        BigDecimal cartTotalPrice = BigDecimal.ZERO;//购物车总价变量
        Set<Integer> productIdSet = new HashSet<>();//创建商品idSet集合
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, uId);//获取用户redis存储的key
        Map<String, Object> entries = redisCache.getCacheMap(redisKey);//拿到购物车所有商品map
        if (entries.size() == 0) {//判断是否未添加商品
            return ResponseVo.successByMsg("未添加任何商品");
        }
        for (Map.Entry<String, Object> entry : entries.entrySet()) {//遍历所有购物车的商品
            //找到所有在购物车里面的所有商品id
            Integer productId = Integer.valueOf(entry.getKey());
            //根据商品id 反序列化生成Cart对象
            Cart cart = JSON.parseObject(JSON.toJSONString(entry.getValue()), Cart.class);
            cart.setProductSelected(true);
            carts.add(cart);
            //TODO 需要优化使用mysql里面的in
            //添加id到set集合当中
            productIdSet.add(productId);
        }
        //更具productIdSet来获取 购物车中的商品集合
        List<Product> products = productMapper.selectByProductIdSet(productIdSet);
        //如果商品集合中没有元素的话 直接返回未添加任何商品
        if (0 == products.size()) {
            return ResponseVo.successByMsg("未添加任何商品");
        }
        //如果商品不为空的话 继续生成一个购物车商品VO对象的集合
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        //将购物车中的商品集合添加到购物车商品VO对象集合中
        for (Product product : products) {
            for (Cart cart : carts) {
                if (cart.getProductId().equals(product.getId())) {
                    cart.setProductSelected(true);
                    //创建一个新的购物车商品VO对象
                    CartProductVo cartProductVo = new CartProductVo(
                            product.getId(),//商品id
                            cart.getQuantity(),//商品数量
                            product.getName(),//商品名称
                            product.getSubtitle(),//商品子标题
                            product.getMainImage(),//购物车商品图片
                            product.getPrice(),//购物车单价
                            product.getStatus(),//购物车商品状态
                            //总价 单价乘以数量
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                            product.getStock(),//商品库存
                            cart.getProductSelected()//购物车商品是否被选中
                    );
                    cartTotalQuantity += cart.getQuantity();//计算总数
                    cartProductVoList.add(cartProductVo);//添加到购物车商品VO对象集合中
                    //这里是商品不被选中购物车当中 去redis中修改
                    //核心修改业务
                    redisCache.putHashCache(redisKey, String.valueOf(product.getId()),
                            JSON.toJSON(cart));
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());//计算选中的商品总价格
                }
            }
        }
        //只计算选中的商品
        cartVo.setCartTotalPrice(cartTotalPrice);
        //计算购物车总数量
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        //直接赋值为true
        cartVo.setSelectAll(true);
        //添加所有购物车商品列表
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uId) {
        CartVo cartVo = new CartVo();//购物车vo对象
        List<Cart> carts = new ArrayList<>();
        Boolean selectAll = false;//购物车商品标识标记
        Integer cartTotalQuantity = 0;//购物车商品总数变量
        BigDecimal cartTotalPrice = BigDecimal.ZERO;//购物车总价变量
        Set<Integer> productIdSet = new HashSet<>();//创建商品idSet集合
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, uId);//获取用户redis存储的key
        Map<String, Object> entries = redisCache.getCacheMap(redisKey);//拿到购物车所有商品map
        if (entries.size() == 0) {//判断是否未添加商品
            return ResponseVo.successByMsg("未添加任何商品");
        }
        for (Map.Entry<String, Object> entry : entries.entrySet()) {//遍历所有购物车的商品
            //找到所有在购物车里面的所有商品id
            Integer productId = Integer.valueOf(entry.getKey());
            //根据商品id 反序列化生成Cart对象
            Cart cart = JSON.parseObject(JSON.toJSONString(entry.getValue()), Cart.class);
            carts.add(cart);
            //TODO 需要优化使用mysql里面的in
            //添加id到set集合当中
            productIdSet.add(productId);
        }
        //更具productIdSet来获取 购物车中的商品集合
        List<Product> products = productMapper.selectByProductIdSet(productIdSet);
        //如果商品集合中没有元素的话 直接返回未添加任何商品
        if (0 == products.size()) {
            return ResponseVo.successByMsg("未添加任何商品");
        }
        //如果商品不为空的话 继续生成一个购物车商品VO对象的集合
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        //将购物车中的商品集合添加到购物车商品VO对象集合中
        for (Product product : products) {
            for (Cart cart : carts) {
                if (cart.getProductId().equals(product.getId())) {
                    cart.setProductSelected(selectAll);
                    //创建一个新的购物车商品VO对象
                    CartProductVo cartProductVo = new CartProductVo(
                            product.getId(),//商品id
                            cart.getQuantity(),//商品数量
                            product.getName(),//商品名称
                            product.getSubtitle(),//商品子标题
                            product.getMainImage(),//购物车商品图片
                            product.getPrice(),//购物车单价
                            product.getStatus(),//购物车商品状态
                            //总价 单价乘以数量
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                            product.getStock(),//商品库存
                            selectAll//购物车商品是否被选中
                    );
                    //redis设置
                    redisCache.putHashCache(redisKey, String.valueOf(product.getId()), JSON.toJSON(cart));
                    cartTotalQuantity += cart.getQuantity();//计算总数
                    cartProductVoList.add(cartProductVo);//添加到购物车商品VO对象集合中
                }
            }
        }
        //只计算选中的商品
        cartVo.setCartTotalPrice(BigDecimal.ZERO);
        //计算购物车总数量
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        //有一个没有选中就没有全选
        cartVo.setSelectAll(selectAll);
        //添加所有购物车商品列表
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }
    @Override
    public ResponseVo<Integer> sum(Integer uId) {
        List<Cart> carts = new ArrayList<>();
        Integer cartTotalQuantity = 0;//购物车商品总数变量
        Set<Integer> productIdSet = new HashSet<>();//创建商品idSet集合
        String redisKey = String.format(CacheConstants.CART_REDIS_KEY, uId);//获取用户redis存储的key
        Map<String, Object> entries = redisCache.getCacheMap(redisKey);//拿到购物车所有商品map
        if (entries.size() == 0) {//判断是否未添加商品
            return ResponseVo.successByMsg("未添加任何商品");
        }
        for (Map.Entry<String, Object> entry : entries.entrySet()) {//遍历所有购物车的商品
            //找到所有在购物车里面的所有商品id
            Integer productId = Integer.valueOf(entry.getKey());
            //根据商品id 反序列化生成Cart对象
            Cart cart = JSON.parseObject(JSON.toJSONString(entry.getValue()), Cart.class);
            carts.add(cart);
            //TODO 需要优化使用mysql里面的in
            //添加id到set集合当中
            productIdSet.add(productId);
        }
        //更具productIdSet来获取 购物车中的商品集合
        List<Product> products = productMapper.selectByProductIdSet(productIdSet);
        //如果商品集合中没有元素的话 直接返回未添加任何商品
        if (0 == products.size()) {
            return ResponseVo.success(0);
        }
        //将购物车中的商品集合添加到购物车商品VO对象集合中
        for (Product product : products) {
            for (Cart cart : carts) {
                if (cart.getProductId().equals(product.getId())) {
                    cartTotalQuantity += cart.getQuantity();//计算选中状态下的总数
                }
            }
        }
        return ResponseVo.success(cartTotalQuantity);
    }
}
