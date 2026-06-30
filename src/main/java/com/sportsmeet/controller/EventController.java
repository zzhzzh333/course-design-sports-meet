package com.sportsmeet.controller;

import com.sportsmeet.entity.Event;
import com.sportsmeet.entity.Registration;
import com.sportsmeet.mapper.EventMapper;
import com.sportsmeet.service.EventService;
import com.sportsmeet.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final RegistrationService registrationService;

    public EventController(EventService eventService, EventMapper eventMapper, RegistrationService registrationService) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.registrationService = registrationService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        List<Event> events = eventService.findAll(category, keyword);
        Map<Long, List<Registration>> regMap = new HashMap<>();
        for (Event e : events) {
            regMap.put(e.getId(), registrationService.findByEventId(e.getId()));
        }
        model.addAttribute("events", events);
        model.addAttribute("regMap", regMap);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        return "event/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("event", new Event());
        return "event/add";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        eventService.save(event);
        redirectAttributes.addFlashAttribute("msg", "项目添加成功");
        return "redirect:/event/list";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.findById(id));
        return "event/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        eventService.update(event);
        redirectAttributes.addFlashAttribute("msg", "项目更新成功");
        return "redirect:/event/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (eventMapper.countRegistrationByEventId(id) > 0) {
            redirectAttributes.addFlashAttribute("error", "该项目已有报名记录，不可删除");
            return "redirect:/event/list";
        }
        eventService.deleteById(id);
        redirectAttributes.addFlashAttribute("msg", "项目删除成功");
        return "redirect:/event/list";
    }

    @GetMapping("/finish/{id}")
    public String finish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Event event = eventService.findById(id);
        if (event == null) {
            redirectAttributes.addFlashAttribute("error", "比赛项目不存在");
            return "redirect:/event/list";
        }
        event.setStatus(2);
        eventService.update(event);
        redirectAttributes.addFlashAttribute("msg", "比赛已结束：" + event.getName());
        return "redirect:/event/list";
    }
}