package top.lhit.mall.module.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lhit.mall.common.consts.MallConsts;
import top.lhit.mall.framework.form.shipping.ShippingForm;
import top.lhit.mall.module.pojo.User;
import top.lhit.mall.module.service.IShippingService;
import top.lhit.mall.module.vo.ResponseVo;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@RestController
public class ShippingController {
    @Autowired
    IShippingService iShippingService;

    @PostMapping("/shippings")
    public ResponseVo add(@Valid @RequestBody ShippingForm form,
                          HttpSession session){
        User attribute = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return iShippingService.add(attribute.getId(),form);
    }
    @DeleteMapping("/shippings/{shipingId}")
    public ResponseVo add(@PathVariable Integer shipingId,
                          HttpSession session){
        User attribute = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return iShippingService.del(attribute.getId(),shipingId);
    }
    @PutMapping("/shippings/{shipingId}")
    public ResponseVo update(@PathVariable Integer shipingId,
            @Valid @RequestBody ShippingForm form,
                          HttpSession session){
        User attribute = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return iShippingService.update(attribute.getId(),shipingId,form);
    }
    @GetMapping("/shippings")
    public ResponseVo selectAll(@RequestParam(required = false,defaultValue = "1")Integer pageNum,
                                @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                          HttpSession session){
        User attribute = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return iShippingService.list(attribute.getId(),pageNum,pageSize);
    }
}
