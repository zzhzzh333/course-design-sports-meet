package com.sportsmeet.controller;

import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
import com.sportsmeet.entity.Event;
import com.sportsmeet.service.EventService;
import com.sportsmeet.service.ScoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/personal", method = {RequestMethod.GET, RequestMethod.POST})
    public String personalReport(@RequestParam(required = false) String keyword, Model model) {
        String safeKeyword = SecurityValidator.cleanKeyword(keyword);
        if (safeKeyword != null) {
            model.addAttribute("scores", scoreService.findPersonalScores(safeKeyword));
        }
        model.addAttribute("keyword", safeKeyword);
        return "report/personal";
    }

    @RequestMapping(value = "/event", method = {RequestMethod.GET, RequestMethod.POST})
    public String eventReport(@RequestParam(required = false) Long eventId, Model model) {
        Long safeEventId = eventId == null ? null : SecurityValidator.validId(eventId, "项目编号");
        model.addAttribute("events", eventService.findAll(null, null));
        if (safeEventId != null) {
            Event event = eventService.findById(safeEventId);
            if (event == null) {
                throw new BusinessException("比赛项目不存在");
            }
            model.addAttribute("scores", scoreService.findByEventId(safeEventId));
            model.addAttribute("event", event);
        }
        model.addAttribute("eventId", safeEventId);
        return "report/event";
    }
}