package top.lhit.mall.module.mapper;
import org.apache.ibatis.annotations.Mapper;
import top.lhit.mall.module.pojo.Category;
import java.util.List;
@Mapper
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectAll();
}