package top.lhit.mall.module.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Order {
    private Integer id;
    private Long orderNo;
    private Integer userId;
    private Integer shippingId;
    private BigDecimal payment;
    private Integer paymentType;
    private Integer postage;
    private Integer status;
    private Date paymentTime;
    private Date sendTime;
    private Date endTime;
    private Date closeTime;
    private Date createTime;
    private Date updateTime;
}