package com.sportsmeet.controller;

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
        List<Registration> registrations = registrationService.findByEventId(eventId);
        model.addAttribute("registrations", registrations);
        model.addAttribute("event", eventService.findById(eventId));
        return "score/form";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam Long eventId,
                         @RequestParam(required = false) List<Long> athleteIds,
                         @RequestParam(required = false) List<Integer> ranks,
                         @RequestParam(required = false) List<String> results,
                         RedirectAttributes redirectAttributes) {
        List<Map<String, String>> scoreData = new ArrayList<>();
        if (athleteIds != null) {
            for (int i = 0; i < athleteIds.size(); i++) {
                Map<String, String> row = new HashMap<>();
                row.put("athleteId", String.valueOf(athleteIds.get(i)));
                if (ranks != null && i < ranks.size() && ranks.get(i) != null) {
                    row.put("rank", String.valueOf(ranks.get(i)));
                }
                if (results != null && i < results.size()) {
                    row.put("result", results.get(i));
                }
                scoreData.add(row);
            }
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
        model.addAttribute("scores", scoreService.findByEventId(eventId));
        model.addAttribute("event", eventService.findById(eventId));
        return "score/view";
    }
}