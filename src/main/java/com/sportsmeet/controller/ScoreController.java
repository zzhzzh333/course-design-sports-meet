package com.sportsmeet.controller;

import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
import com.sportsmeet.entity.Event;
import com.sportsmeet.entity.Registration;
import com.sportsmeet.service.EventService;
import com.sportsmeet.service.RegistrationService;
import com.sportsmeet.service.ScoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService scoreService;
    private final RegistrationService registrationService;
    private final EventService eventService;

    public ScoreController(ScoreService scoreService, RegistrationService registrationService, EventService eventService) {
        this.scoreService = scoreService;
        this.registrationService = registrationService;
        this.eventService = eventService;
    }

    @GetMapping("/input")
    public String inputPage(Model model) {
        model.addAttribute("events", eventService.findAll(null, null));
        return "score/input";
    }

    @GetMapping("/input/{eventId}")
    public String inputForm(@PathVariable Long eventId, Model model) {
        SecurityValidator.validId(eventId, "项目编号");
        Event event = eventService.findById(eventId);
        if (event == null) {
            throw new BusinessException("比赛项目不存在");
        }
        List<Registration> registrations = registrationService.findByEventId(eventId);
        model.addAttribute("registrations", registrations);
        model.addAttribute("event", event);
        return "score/form";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam Long eventId,
                         @RequestParam(required = false) List<Long> athleteIds,
                         @RequestParam(required = false) List<Integer> ranks,
                         @RequestParam(required = false) List<String> results,
                         RedirectAttributes redirectAttributes) {
        SecurityValidator.validId(eventId, "项目编号");
        validateScoreData(athleteIds, ranks, results);
        List<Map<String, String>> scoreData = new ArrayList<>();
        for (int i = 0; i < athleteIds.size(); i++) {
            Map<String, String> row = new HashMap<>();
            row.put("athleteId", String.valueOf(athleteIds.get(i)));
            row.put("rank", String.valueOf(ranks.get(i)));
            row.put("result", results.get(i));
            scoreData.add(row);
        }
        try {
            scoreService.submitScores(eventId, scoreData);
            redirectAttributes.addFlashAttribute("msg", "成绩提交成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "成绩提交失败: " + e.getMessage());
        }
        return "redirect:/score/view/" + eventId;
    }

    @GetMapping("/view/{eventId}")
    public String view(@PathVariable Long eventId, Model model) {
        SecurityValidator.validId(eventId, "项目编号");
        Event event = eventService.findById(eventId);
        if (event == null) {
            throw new BusinessException("比赛项目不存在");
        }
        model.addAttribute("scores", scoreService.findByEventId(eventId));
        model.addAttribute("event", event);
        return "score/view";
    }

    private void validateScoreData(List<Long> athleteIds, List<Integer> ranks, List<String> results) {
        if (athleteIds == null || ranks == null || results == null || athleteIds.isEmpty()) {
            throw new BusinessException("没有可提交的成绩数据");
        }
        if (athleteIds.size() != ranks.size() || athleteIds.size() != results.size()) {
            throw new BusinessException("成绩数据不完整");
        }
        for (int i = 0; i < athleteIds.size(); i++) {
            SecurityValidator.validId(athleteIds.get(i), "运动员编号");
            ranks.set(i, SecurityValidator.validRank(ranks.get(i)));
            results.set(i, SecurityValidator.validText(results.get(i), "成绩", 50));
        }
    }
}