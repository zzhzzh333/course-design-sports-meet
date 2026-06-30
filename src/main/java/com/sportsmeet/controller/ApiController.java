package com.sportsmeet.controller;

import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.Result;
import com.sportsmeet.common.SecurityValidator;
import com.sportsmeet.entity.SysUser;
import com.sportsmeet.service.EventService;
import com.sportsmeet.service.ScoreService;
import com.sportsmeet.service.SysUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SysUserService sysUserService;
    private final EventService eventService;
    private final ScoreService scoreService;

    public ApiController(SysUserService sysUserService, EventService eventService, ScoreService scoreService) {
        this.sysUserService = sysUserService;
        this.eventService = eventService;
        this.scoreService = scoreService;
    }

    @PostMapping("/login")
    public Result<SysUser> login(@RequestParam String username,
                                  @RequestParam String password,
                                  HttpSession session) {
        try {
            username = SecurityValidator.validText(username, "用户名", 30);
            password = SecurityValidator.validText(password, "密码", 50);
            SysUser user = sysUserService.login(username, password);
            if (user == null) {
                return Result.error("用户名或密码错误");
            }
            session.setAttribute("loginUser", user);
            return Result.success(user);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/events")
    public Result<?> events(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String keyword) {
        try {
            String safeCategory = SecurityValidator.validCategory(category, false);
            String safeKeyword = SecurityValidator.cleanKeyword(keyword);
            return Result.success(eventService.findAll(safeCategory, safeKeyword));
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/report/team")
    public Result<?> teamReport() {
        return Result.success(scoreService.findTeamScoreSummary());
    }
}