package top.lhit.mall.module.service;
import top.lhit.mall.module.vo.CategoryVo;
import top.lhit.mall.module.vo.ResponseVo;
import java.util.List;
import java.util.Set;

public interface ICategoryService {
    ResponseVo<List<CategoryVo>> selectAllCategory();
    void findSubCategoryId(Integer id, Set<Integer> resultSet);

}
