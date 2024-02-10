package top.lhit.mall.module.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lhit.mall.module.service.ICategoryService;
import top.lhit.mall.module.vo.CategoryVo;
import top.lhit.mall.module.vo.ResponseVo;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/categories")
    public ResponseVo<List<CategoryVo>> getCategories(){
            return categoryService.selectAllCategory();

    }

}
