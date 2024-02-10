package top.lhit.mall.module.service.impl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.lhit.mall.MallApplicationTests;
import top.lhit.mall.common.utils.RedisCache;
import top.lhit.mall.framework.form.cart.CartAddForm;
import top.lhit.mall.framework.form.cart.CartDeleteForm;
import top.lhit.mall.framework.form.cart.CartUpdateForm;
import top.lhit.mall.module.pojo.Cart;
import top.lhit.mall.module.service.ICartService;
import top.lhit.mall.module.vo.CartVo;
import top.lhit.mall.module.vo.ResponseVo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
public class CartServiceImplTest extends MallApplicationTests {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private ICartService cartService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testListForUser(){
        List<Cart> carts = cartService.listForUserCart(1);
        log.info("list={}",gson.toJson(carts));
    }
    @Test
    public void testSelectAll(){
        ResponseVo<CartVo> cartVoResponseVo = cartService.selectAll(1);
        log.info("list={}",gson.toJson(cartVoResponseVo));
    }

    @Test
    public void testSum(){
        ResponseVo<Integer> sum = cartService.sum(1);
        log.info("Sum={}",gson.toJson(sum));
    }
    @Test
    public void testUnSelectAll(){
        ResponseVo<CartVo> cartVoResponseVo = cartService.unSelectAll(1);
        log.info("list={}",gson.toJson(cartVoResponseVo));
    }

    @Test
    public void testDel(){
        Set<Integer> ids = new HashSet<>();
        ids.add(26);
        ids.add(27);
        ResponseVo<CartVo> del = cartService.del(1, new CartDeleteForm(ids));
        log.info("list={}",gson.toJson(del));
    }

    @Test
    public void testUpdate(){
        ResponseVo<CartVo> update = cartService.update(1, 26, new CartUpdateForm(10));
        log.info("list={}",gson.toJson(update));
    }

    @Test
    public void testRedis(){
        redisCache.setCacheObject("1111","afdsafsdfdas");
    }
    @Test
    public void testAdd() {
        CartAddForm cartAddForm = new CartAddForm(27, false);
        ResponseVo<CartVo> add = cartService.add(1, cartAddForm);
        log.info("list={}",gson.toJson(add));
    }
    @Test
    public void testRedisGet(){
        String redisKey  = String.format("cart_%d", 1);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String cart = opsForHash.get(redisKey, String.valueOf(26));
        System.out.println(cart);
        System.out.println(redisKey);
    }


    @Test
    public void testCartVoList(){

        ResponseVo<CartVo> list = cartService.list(1);
        log.info("list={}",gson.toJson(list));

    }
}