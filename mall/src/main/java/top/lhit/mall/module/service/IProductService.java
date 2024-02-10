package top.lhit.mall.module.service;

import com.github.pagehelper.PageInfo;
import top.lhit.mall.module.pojo.Product;
import top.lhit.mall.module.vo.ProductDetailVo;
import top.lhit.mall.module.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface IProductService {
    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);
    ResponseVo<ProductDetailVo> detail(Integer productId);
    List<Product> getProductListByIdSet(Set<Integer> ids);
}
