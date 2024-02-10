package top.lhit.mall.framework.form.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
/**
 * 删除购物车商品表单
 */
public class CartDeleteForm {
    @NotEmpty
    private Set<Integer> productIds;
}
