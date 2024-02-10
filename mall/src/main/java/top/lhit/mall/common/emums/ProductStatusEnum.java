package top.lhit.mall.common.emums;

import lombok.Getter;
import top.lhit.mall.module.pojo.Product;

@Getter
public enum ProductStatusEnum {
    ON_SALE(1),
    OFF_SALE(0),
    DELETE(3)
    ;
    Integer code;
    ProductStatusEnum(Integer code){
        this.code=code;
    }

}
