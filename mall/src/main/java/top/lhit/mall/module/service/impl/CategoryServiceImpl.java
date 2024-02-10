package top.lhit.mall.module.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lhit.mall.common.consts.MallConsts;
import top.lhit.mall.module.mapper.CategoryMapper;
import top.lhit.mall.module.pojo.Category;
import top.lhit.mall.module.service.ICategoryService;
import top.lhit.mall.module.vo.CategoryVo;
import top.lhit.mall.module.vo.ResponseVo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 耗时： http(请求微信api)> 硬盘 > java 程序 jvm 虚拟机 也就是内存中
     * mysql(内网+硬盘)
     * @return
     */
    @Override
    public ResponseVo<List<CategoryVo>> selectAllCategory() {
        //从数据库中获取全部catrgory 对象
        List<Category> categories = categoryMapper.selectAll();
        //查出parent_id=0
        //lambda + stream
        List<CategoryVo> categoryVoList = categories.stream()
                .filter(e -> e.getParentId().equals(MallConsts.ROOT_PARENT_ID))
                .map(this::category2CategoryVo)//调用下面的复制方法
                //对一级目录进行排序
                .sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
                .collect(Collectors.toList());
        //查询子目录
        findSubCategory(categoryVoList, categories);
        return ResponseVo.success(categoryVoList);
    }
    public void findSubCategoryId(Integer id,Set<Integer> resultSet,List<Category> categories) {
        for (Category category : categories) {
            if (category.getParentId().equals(id)){
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(),resultSet,categories);
            }

        }

    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id,resultSet,categories);
    }

    //复制pojo和vo对象
    private CategoryVo category2CategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }

    private void findSubCategory(List<CategoryVo> categoryVoList, List<Category> categories) {

        for (CategoryVo categoryVo : categoryVoList) {
            List<CategoryVo> subCategoryVoList = new ArrayList<>();

            //以这个集合中的categoryVO对象中的id
            for (Category category : categories) {
                //来查询所有parent_id一致的categroy对象
                if (categoryVo.getId().equals(category.getParentId())) {
                    CategoryVo subCategoryVo = category2CategoryVo(category);
                    subCategoryVoList.add(subCategoryVo);
                }
                //子类目集合 进行按照sortOrder来排序默认是升序 可以通过.reversed来进行反转
                subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
                //将子类目集合set注入到categoryVo对象中作为ResponseVo 中的data对象进行返回
                categoryVo.setSubCategories(subCategoryVoList);
                //递归设置子类目
                findSubCategory(subCategoryVoList,categories);
            }
        }
    }
}
