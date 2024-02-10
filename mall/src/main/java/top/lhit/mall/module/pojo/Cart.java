package top.lhit.mall.module.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Cart {
    private Integer productId;
    private Integer quantity;
    private Boolean productSelected;
}
