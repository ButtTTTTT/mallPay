package top.lhit.mall.module.service.impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.lhit.mall.common.emums.ProductStatusEnum;
import top.lhit.mall.common.emums.ResponseEnum;
import top.lhit.mall.framework.form.cart.CartDeleteForm;
import top.lhit.mall.module.mapper.OrderItemMapper;
import top.lhit.mall.module.mapper.OrderMapper;
import top.lhit.mall.module.mapper.ProductMapper;
import top.lhit.mall.module.mapper.ShippingMapper;
import top.lhit.mall.module.pojo.*;
import top.lhit.mall.module.service.ICartService;
import top.lhit.mall.module.service.IOrderService;
import top.lhit.mall.module.service.IProductService;
import top.lhit.mall.module.service.IShippingService;
import top.lhit.mall.module.vo.CartVo;
import top.lhit.mall.module.vo.OrderItemVo;
import top.lhit.mall.module.vo.OrderVo;
import top.lhit.mall.module.vo.ResponseVo;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static top.lhit.mall.common.emums.OrderStatusEnum.CANCELED;
import static top.lhit.mall.common.emums.OrderStatusEnum.NO_PAY;
import static top.lhit.mall.common.emums.PaymentTypeEnum.PAY_ONLINE;
import static top.lhit.mall.common.emums.ResponseEnum.*;
@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private ICartService cartService;
    @Autowired
    private IShippingService shippingService;
    @Autowired
    private IProductService productService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Override
    @Transactional//默认是出现runtimeExcption 就回滚
    public ResponseVo<OrderVo> create(Integer uId, Integer shippingId) {
        //收货地址校验(要查出来收货地址)
        Shipping shipping = shippingService.getOrderShipping(uId, shippingId);
        if (null == shipping) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }
        ResponseVo<CartVo> list = cartService.list(uId);

        //通过userId 来获取购物车，校验 是否有商品 库存是否充足

        List<Cart> carts = cartService
                .listForUserCart(uId)//调用方法
                .stream()
                .filter(Cart::getProductSelected)//过滤所有选中的对象
                .collect(Collectors.toList());//转成list
        if (CollectionUtils.isEmpty(carts)) {
            return ResponseVo.error(CART_SELECTED_IS_EMPTY);
        }
        //计算该订单的价格 总价格  只计算被选中的商品
        BigDecimal orderTotalPrice = BigDecimal.ZERO;
        //获取carts里面的productIds
        Set<Integer> productIds = carts.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toSet());
        List<Product> products = productService.getProductListByIdSet(productIds);
        Map<Integer, Product> map = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
        //生产orderItem集合 给OrderVo对象进行赋值
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        //生成订单号
        Long orderNo = generateOrderNo();
        for (Cart cart : carts) {
            //根据productId来查数据库
            Product product = map.get(cart.getProductId());
            if (null == product) {
                return ResponseVo.error(PRODUCT_NOT_EXIST
                        , "商品无效.productId=" + cart.getProductId());
            }
            //商品上下架状态验证
            if (!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())) {
                return ResponseVo.error(PRODUCT_OFF_SALE_OR_DELETE, "商品不是在售状态" + product.getName());
            }
            //库存是否充足
            if (product.getStock() < cart.getQuantity()) {
                return ResponseVo.error(PROODUCT_STOCK_ERROR
                        , "库存不正确,productId=" + product.getName());
            }

            //构建Order 和OrderItem 对象
            OrderItem item = buildOrderItem(uId, cart.getQuantity(), orderNo, product);
            orderItems.add(item);

            //减少库存
            //下单成功后要减少库存
            product.setStock(product.getStock() - cart.getQuantity());
            int mallProductResult = productMapper.updateByPrimaryKeySelective(product);
            if (mallProductResult <= 0) {
                return ResponseVo.error(ERROR);
            }
        }
        //计算总价，只计算选中的商品
        //生成订单，入库： order 和 order_item
        // 这两张表必须全部都执行成功/不然直接回滚  通过事务来控制
        Order order = buildOrder(uId, orderNo, shippingId, orderItems);

        //将这个订单写入到mall_order 表中
        int mallOrderResult = orderMapper.insertSelective(order);
        if (mallOrderResult <= 0) {
            return ResponseVo.error(ERROR);
        }
        //写入order_item表中
        int mallOrderItemResult = orderItemMapper.batchInsert(orderItems);
        if (mallOrderItemResult <= 0) {
            return ResponseVo.error(ERROR);
        }


        //清空被生成订单的购物车 更新购物车
        //redis有事务(打包命令)，不能回滚
        Set<Integer> cartDelIds = new HashSet<>();
        for (Cart cart : carts) {
            cartDelIds.add(cart.getProductId());
        }
        //统一删除购物车里这个商品
        cartService.del(uId, new CartDeleteForm(cartDelIds));
        //构造OrderVo对象返回给前端
        OrderVo orderVo = buildOrderVo(order, orderItems, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uId, Long orderNo) {
        //根据orderNo查询order
        Order order = orderMapper.selectByOrderNo(orderNo);
        //验证订单及用户是否拥有这个订单
        if (null==order||!order.getUserId().equals(uId)){
            return ResponseVo.error(ORDER_NOT_EXIST);
        }
        HashSet<Long> orderNoSet = new HashSet<>();
        orderNoSet.add(orderNo);
        //验证成功的话
        //查询OrderItemList 
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNoSet(orderNoSet);
        //查询地址
        Shipping shipping = shippingMapper.selectByUidAndShippingId(uId, order.getShippingId());
        if (orderItems.size()<=0 || null==shipping){
            return ResponseVo.error(PRODUCT_NOT_EXIST);
        }
        //构建OrderVo对象
        OrderVo orderVo = buildOrderVo(order, orderItems, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uId, Integer pageNum, Integer pageSize) {
        //开启分页 传入页码页数
        PageHelper.startPage(pageNum, pageSize);
        //传给前端的数据是OrderVo list集合
        List<OrderVo> orderVos = new ArrayList<>();
        //先查出来该用户的所有订单Order的集合
        List<Order> orders = orderMapper.selectByUid(uId);
        //创建orderIds set 来添加所有的order
        Set<Long> orderNoSet = orders.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toSet());
        //根据orderNoSet获取所有的OrderItem
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        //根据orderItemList来获取其中的所有<k:orderNo,v:List<OrderItem>>
        Map<Long,List<OrderItem>> orderItemMap = orderItemList
                .stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));


        //从orders集合中获取所有的shippingId
        Set<Integer> shippingIdSet = orders
                .stream()
                .map(Order::getShippingId)
                .collect(Collectors.toSet());
        //根据shippingIdSet来获取所有的Shipping集合
        List<Shipping> shippings = shippingMapper.selectByIdSet(shippingIdSet);
        //根据Shippings 集合 生成一个map<k:shippingId,v:Shipping>
        Map<Integer,Shipping> shippingMap = shippings
                .stream()
                .collect(Collectors.toMap((Shipping::getId),shipping -> shipping));
        //循环构建OrderVo对象
        for (Order order : orders) {


            //循环添加构建完的OrderVo对象
                orderVos.add(buildOrderVo(order,
                        orderItemMap.get(order.getOrderNo()), //如果拿不到这个orderNo的话建议是告警
                        shippingMap.get(order.getShippingId())));


        }

        PageInfo pageInfo = new PageInfo(orders);
        pageInfo.setList(orderVos);
        return ResponseVo.success(pageInfo);

    }
    @Override
    public ResponseVo cancel(Integer uId, Long orderNo) {
        //根据订单号查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        //订单校验 订单是否存在  订单的用户是否正确
        if (order==null||!order.getUserId().equals(uId)){
            return ResponseVo.error(ORDER_NOT_EXIST);
        }
        //只有【未付款】订单可以取消，看业务场景
        if (!order.getStatus().equals(NO_PAY.getCode())){
            return ResponseVo.error(ORDER_STATUS_ERROR);
        }
        order.setStatus(CANCELED.getCode());
        order.setCloseTime(new Date());
        int mallOrderResult = orderMapper.updateByPrimaryKeySelective(order);
        if (mallOrderResult<=0){
            return ResponseVo.error(ERROR);
        }
        return ResponseVo.success();
    }
    private OrderItem buildOrderItem(Integer uid,
                                     Integer quantity,
                                     Long orderNo,
                                     Product product) {
        OrderItem item = new OrderItem();
        item.setUserId(uid);
        item.setOrderNo(orderNo);
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setCurrentUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    /**
     * 正式生产业务的写法：
     * 分布式唯一id：
     * twitter雪花，jdk1.5 uuid，
     *
     * @return orderNo
     */
    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private Order buildOrder(Integer uId,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uId);
        order.setShippingId(shippingId);
        order.setPayment(orderItemList
                .stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setPaymentType(PAY_ONLINE.getCode());
        order.setStatus(NO_PAY.getCode());
        return order;
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);

        List<OrderItemVo> OrderItemVoList = orderItemList.stream().map(e -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(e, orderItemVo);
            return orderItemVo;
        }).collect(Collectors.toList());
        orderVo.setOrderItemVoList(OrderItemVoList);

        if (shipping != null) {
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }

        return orderVo;
    }
}
