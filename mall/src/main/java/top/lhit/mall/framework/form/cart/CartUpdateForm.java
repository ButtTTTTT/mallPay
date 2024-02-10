package top.lhit.mall.framework.form.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CartUpdateForm {

    private Integer quantity;

    private Boolean selected;

    public CartUpdateForm(Boolean selected) {
        this.selected = selected;
    }

    public CartUpdateForm(Integer quantity) {
        this.quantity = quantity;
    }
}
