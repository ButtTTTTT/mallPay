package top.lhit.mall.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.lhit.mall.common.emums.ResponseEnum;
import top.lhit.mall.module.vo.ResponseVo;

import java.util.Objects;

import static top.lhit.mall.common.emums.ResponseEnum.ERROR;
import static top.lhit.mall.common.emums.ResponseEnum.PARAM_ERROR;

@ControllerAdvice
public class RuntimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
//    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseVo handle(RuntimeException e){
        return ResponseVo.error(ERROR,e.getMessage());
    }

//    @ExceptionHandler(UserLoginException.class)
//    @ResponseBody
//    public ResponseVo userLoginHandle() {
//        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
//    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo noVaildExceptionHandle(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        Objects.requireNonNull(bindingResult.getFieldError());
        return ResponseVo.error(PARAM_ERROR,
                bindingResult.getFieldError().getField()+" "+bindingResult.getFieldError().getDefaultMessage());
    }
}
