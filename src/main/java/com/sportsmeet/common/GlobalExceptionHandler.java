package com.sportsmeet.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:" + fallbackPath(request.getRequestURI());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public Object handleBadRequest(Exception e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Result.error("参数格式不正确"));
        }
        redirectAttributes.addFlashAttribute("error", "参数格式不正确");
        return "redirect:" + fallbackPath(request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (isApiRequest(request)) {
            return ResponseEntity.internalServerError().body(Result.error("系统异常，请稍后再试"));
        }
        redirectAttributes.addFlashAttribute("error", "系统异常，请稍后再试");
        return "redirect:" + fallbackPath(request.getRequestURI());
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI() != null && request.getRequestURI().startsWith("/api");
    }

    private String fallbackPath(String uri) {
        if (uri == null) {
            return "/dashboard";
        }
        if (uri.startsWith("/event")) {
            return "/event/list";
        }
        if (uri.startsWith("/athlete")) {
            return "/athlete/list";
        }
        if (uri.startsWith("/registration")) {
            return "/registration/list";
        }
        if (uri.startsWith("/score")) {
            return "/score/input";
        }
        if (uri.startsWith("/report/event")) {
            return "/report/event";
        }
        if (uri.startsWith("/report/personal")) {
            return "/report/personal";
        }
        if (uri.startsWith("/report")) {
            return "/report/team";
        }
        return "/dashboard";
    }
}