package com.sportsmeet.controller;

import com.sportsmeet.common.SecurityValidator;
import com.sportsmeet.entity.Event;
import com.sportsmeet.service.AthleteService;
import com.sportsmeet.service.EventService;
import com.sportsmeet.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AthleteService athleteService;
    private final EventService eventService;

    public RegistrationController(RegistrationService registrationService, AthleteService athleteService, EventService eventService) {
        this.registrationService = registrationService;
        this.athleteService = athleteService;
        this.eventService = eventService;
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@RequestParam(required = false) Long eventId,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        Long safeEventId = eventId == null ? null : SecurityValidator.validId(eventId, "项目编号");
        String safeKeyword = SecurityValidator.cleanKeyword(keyword);
        model.addAttribute("registrations", registrationService.findAll(safeEventId, safeKeyword));
        model.addAttribute("events", eventService.findAll(null, null));
        model.addAttribute("eventId", safeEventId);
        model.addAttribute("keyword", safeKeyword);
        return "registration/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("athletes", athleteService.findAll(null, null));
        List<Event> availableEvents = eventService.findAll(null, null).stream()
                .filter(e -> e.getStatus() != 2)
                .collect(Collectors.toList());
        model.addAttribute("events", availableEvents);
        return "registration/add";
    }

    @PostMapping("/register")
    public String register(@RequestParam Long athleteId,
                           @RequestParam Long eventId,
                           RedirectAttributes redirectAttributes) {
        SecurityValidator.validId(athleteId, "运动员编号");
        SecurityValidator.validId(eventId, "项目编号");
        try {
            registrationService.register(athleteId, eventId);
            redirectAttributes.addFlashAttribute("msg", "报名成功");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/registration/list";
    }

    @GetMapping("/cancel/{id}")
    public String cancelByGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "取消报名必须通过页面按钮提交，不允许直接修改URL执行");
        return "redirect:/registration/list";
    }

    @PostMapping("/cancel")
    public String cancel(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        SecurityValidator.validId(id, "报名编号");
        registrationService.cancel(id);
        redirectAttributes.addFlashAttribute("msg", "报名已取消");
        return "redirect:/registration/list";
    }
}