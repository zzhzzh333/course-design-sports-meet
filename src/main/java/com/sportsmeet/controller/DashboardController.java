package com.sportsmeet.controller;

import com.sportsmeet.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        java.util.Map<String, Object> summary = dashboardService.getSummary();
        model.addAttribute("totalEvents", summary.get("totalEvents"));
        model.addAttribute("totalAthletes", summary.get("totalAthletes"));
        model.addAttribute("totalRegistrations", summary.get("totalRegistrations"));
        model.addAttribute("finishedEvents", summary.get("finishedEvents"));
        model.addAttribute("upcomingEvents", summary.get("upcomingEvents"));
        model.addAttribute("endedWithoutScores", summary.get("endedWithoutScores"));
        model.addAttribute("pendingScoreCount", summary.get("pendingScoreCount"));
        return "dashboard";
    }
}