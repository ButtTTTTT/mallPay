package top.lhit.mall.module.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lhit.mall.common.consts.MallConsts;
import top.lhit.mall.framework.form.cart.CartAddForm;
import top.lhit.mall.framework.form.cart.CartDeleteForm;
import top.lhit.mall.framework.form.cart.CartUpdateForm;
import top.lhit.mall.module.service.ICartService;
import top.lhit.mall.module.vo.CartVo;
import top.lhit.mall.module.vo.ResponseVo;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import top.lhit.mall.module.pojo.User;
@RestController
public class CartController {
    @Autowired
    private ICartService cartService;

    @GetMapping("/carts/list")
    public ResponseVo<CartVo> list(HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);

        return cartService.list(user.getId());
    }

    @PostMapping("/carts")
    public ResponseVo<CartVo> add(@Valid @RequestBody CartAddForm cartAddForm,
                                  HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);

        return cartService.add(user.getId(), cartAddForm);
    }

    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo> update(@PathVariable Integer productId,
                                     @Valid @RequestBody CartUpdateForm cartUpdateForm,
                                     HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);

        return cartService.update(user.getId(), productId, cartUpdateForm);
    }

    @DeleteMapping("/carts/{productIds}")
    public ResponseVo<CartVo> del(@PathVariable CartDeleteForm cartDeleteForm,
                                  HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);

        return cartService.del(user.getId(), cartDeleteForm);
    }

    @PutMapping("/carts/selectAll")
    public ResponseVo<CartVo> selectAll(
            HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);

        return cartService.selectAll(user.getId());
    }

    @PutMapping("/carts/unSelectAll")
    public ResponseVo<CartVo> unSelectAll(
            HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);

        return cartService.unSelectAll(user.getId());
    }

    @GetMapping("/carts/products/sum")
    public ResponseVo<Integer> sum(HttpSession session) {
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return cartService.sum(user.getId());
    }
}
