package com.magicgate.link.handler;

import com.magicgate.link.exception.LLMChatException;
import com.magicgate.link.utils.FluxUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

/**
 * 统一异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 捕获 @RequestBody 校验失败（DTO 参数）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidException(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body("参数错误：" + msg);
    }

    // 捕获 @RequestParam、路径变量等校验失败
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleParamException(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body("参数错误：" + msg);
    }

    /**
     * 处理 RuntimeException 异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException occurred: ", ex);
        return new ResponseEntity<>("Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理 Exception 异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Exception occurred: ", ex);
        return new ResponseEntity<>("Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理特定的业务异常，例如余额不足异常
     * webClient调用的时候不要走这里处理异常，负责会默认复制http200，导致报错
     */
    //@ExceptionHandler(LLMChatException.class)
    //public Object handleLLMChatException(LLMChatException ex, HandlerMethod handlerMethod) {
    //    log.warn("捕获到LLM异常: {}, 发生在方法: {}", ex.getMessage(), handlerMethod.getMethod().getName());
    //    // 获取触发异常的 Controller 方法的返回类型
    //    Class<?> returnType = handlerMethod.getReturnType().getParameterType();
    //
    //    // 1. 如果方法返回的是 Flux (流式接口，如 stream, debuggingLibrary)
    //    if (Flux.class.isAssignableFrom(returnType)) {
    //        // 对于流式响应，返回一个只包含错误对象的 Flux，然后结束流
    //        // 前端 SSE 客户端会收到一个事件，然后连接关闭
    //        log.info("方法 【{}】 的返回类型为 Flux，返回包含错误信息的单个事件。", handlerMethod.getMethod().getName());
    //        return Flux.just(
    //                ServerSentEvent.<String>builder().data(ex.getMessage()).build()
    //        );
    //        //return FluxUtils.buildReturn(just);
    //    }
    //
    //    // 2. 如果方法返回的是 ResponseEntity (例如 vision 接口)
    //    if (ResponseEntity.class.isAssignableFrom(returnType)) {
    //        log.info("方法 【{}】 的返回类型为 ResponseEntity，封装错误信息后返回。", handlerMethod.getMethod().getName());
    //        return ResponseEntity.badRequest().body(ex.getMessage());
    //    }
    //
    //    // 4. 默认降级处理：对于其他所有未知返回类型，直接返回标准错误 DTO
    //    // Spring MVC 会自动将其序列化为 JSON
    //    log.warn("方法 【{}】 的返回类型未做特定适配，使用默认错误响应。", handlerMethod.getMethod().getName());
    //    return ResponseEntity.badRequest().body(ex.getMessage());
    //}

}
