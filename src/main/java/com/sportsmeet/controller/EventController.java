package com.sportsmeet.controller;

import com.sportsmeet.entity.Event;
import com.sportsmeet.mapper.EventMapper;
import com.sportsmeet.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        model.addAttribute("events", eventService.findAll(category, keyword));
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
}