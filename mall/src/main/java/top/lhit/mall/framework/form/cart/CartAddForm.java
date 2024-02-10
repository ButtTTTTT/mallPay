package top.lhit.mall.framework.form.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 购物车添加商品表单验证
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CartAddForm {
    @NotNull
    private Integer productId;
    private Boolean selected = true;
}
