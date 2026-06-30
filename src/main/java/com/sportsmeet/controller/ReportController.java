package com.sportsmeet.controller;

import com.sportsmeet.service.EventService;
import com.sportsmeet.service.ScoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ScoreService scoreService;
    private final EventService eventService;

    public ReportController(ScoreService scoreService, EventService eventService) {
        this.scoreService = scoreService;
        this.eventService = eventService;
    }

    @GetMapping("/team")
    public String teamReport(Model model) {
        model.addAttribute("teamScores", scoreService.findTeamScoreSummary());
        return "report/team";
    }

    @GetMapping("/personal")
    public String personalReport(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("scores", scoreService.findPersonalScores(keyword.trim()));
        }
        model.addAttribute("keyword", keyword);
        return "report/personal";
    }

    @GetMapping("/event")
    public String eventReport(@RequestParam(required = false) Long eventId, Model model) {
        model.addAttribute("events", eventService.findAll(null, null));
        if (eventId != null) {
            model.addAttribute("scores", scoreService.findByEventId(eventId));
            model.addAttribute("event", eventService.findById(eventId));
        }
        model.addAttribute("eventId", eventId);
        return "report/event";
    }
}