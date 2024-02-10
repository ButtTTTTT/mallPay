package top.lhit.mall.module.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lhit.mall.common.consts.MallConsts;
import top.lhit.mall.framework.form.order.OrderCreateForm;
import top.lhit.mall.module.service.IOrderService;
import top.lhit.mall.module.vo.OrderVo;
import top.lhit.mall.module.pojo.User;
import top.lhit.mall.module.vo.ResponseVo;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @PostMapping("orders")
    public ResponseVo<OrderVo> createOrder(@Valid @RequestBody OrderCreateForm orderCreateForm,
                                           HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return orderService.create(user.getId(), orderCreateForm.getShippingId());
    }

    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return orderService.list(user.getId(), pageNum, pageSize);
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable Long orderNo,
                                      HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return orderService.detail(user.getId(), orderNo);
    }

    @PutMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> cancel(@PathVariable Long orderNo,
                                      HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return orderService.cancel(user.getId(), orderNo);
    }
}
