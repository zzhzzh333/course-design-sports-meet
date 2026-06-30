package com.sportsmeet.service.impl;

import com.sportsmeet.entity.Event;
import com.sportsmeet.service.AthleteService;
import com.sportsmeet.service.DashboardService;
import com.sportsmeet.service.EventService;
import com.sportsmeet.service.RegistrationService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EventService eventService;
    private final AthleteService athleteService;
    private final RegistrationService registrationService;

    public DashboardServiceImpl(EventService eventService, AthleteService athleteService, RegistrationService registrationService) {
        this.eventService = eventService;
        this.athleteService = athleteService;
        this.registrationService = registrationService;
    }

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEvents", eventService.countTotal());
        summary.put("totalAthletes", athleteService.countTotal());
        summary.put("totalRegistrations", registrationService.countTotal());
        summary.put("finishedEvents", eventService.countByStatus(2));
        List<Event> upcoming = eventService.findUpcoming(5);
        summary.put("upcomingEvents", upcoming);
        List<Event> endedWithoutScores = eventService.findEndedWithoutScores();
        summary.put("endedWithoutScores", endedWithoutScores);
        summary.put("pendingScoreCount", endedWithoutScores.size());
        return summary;
    }
}