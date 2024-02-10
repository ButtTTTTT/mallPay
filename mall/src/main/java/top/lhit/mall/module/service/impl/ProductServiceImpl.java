package top.lhit.mall.module.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lhit.mall.common.emums.ResponseEnum;
import top.lhit.mall.module.mapper.ProductMapper;
import top.lhit.mall.module.pojo.Product;
import top.lhit.mall.module.service.ICategoryService;
import top.lhit.mall.module.service.IProductService;
import top.lhit.mall.module.vo.ProductDetailVo;
import top.lhit.mall.module.vo.ProductVo;
import top.lhit.mall.module.vo.ResponseVo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static top.lhit.mall.common.emums.ProductStatusEnum.DELETE;
import static top.lhit.mall.common.emums.ProductStatusEnum.OFF_SALE;
import static top.lhit.mall.common.emums.ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE;

@Service@Slf4j
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        //初始化类目id 集合
        Set<Integer> categoryIdSet = new HashSet<>();

        if (!(null==categoryId)){
        //这是查询子类的id
        categoryService.findSubCategoryId(categoryId, categoryIdSet);
        //添加自身的id
        categoryIdSet.add(categoryId);
        }
        //调用分页组件来做查询分页
        PageHelper.startPage(pageNum,pageSize);
        List<Product>  productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        //根据类目查询到商品
        List<ProductVo> productVoList = productMapper.selectByCategoryIdSet(categoryIdSet).stream()
                .map(e -> {
                    ProductVo productVo = new ProductVo();
                    BeanUtils.copyProperties(e, productVo);
                    return productVo;
                })
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        //只对确定性的判断 最好就针对这两种状态进行判断 以免后续遇到其他状态
        if (product.getStatus().equals(OFF_SALE.getCode())||product.getStatus().equals(DELETE.getCode())){
            return ResponseVo.error(PRODUCT_OFF_SALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        //用来给显示的库存进行限制
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());

        return ResponseVo.success(productDetailVo);
    }

    @Override
    public List<Product> getProductListByIdSet(Set<Integer> ids) {
        return productMapper.selectByProductIdSet(ids);
    }


}
