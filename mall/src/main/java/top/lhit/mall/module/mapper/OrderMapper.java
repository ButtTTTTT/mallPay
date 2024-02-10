package top.lhit.mall.module.mapper;
import org.apache.ibatis.annotations.Mapper;
import top.lhit.mall.module.pojo.Order;
import java.util.List;

@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    List<Order> selectByUid(Integer uid);

    Order selectByOrderNo(Long orderNo);
}
