package top.lhit.mall.module.vo;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
/**
 * 购物车Vo层对象
 */
public class CartVo {
       private List<CartProductVo> cartProductVoList;
       private Boolean selectAll;
       private BigDecimal cartTotalPrice;
       private Integer cartTotalQuantity;
}
