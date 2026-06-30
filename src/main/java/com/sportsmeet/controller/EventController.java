package com.sportsmeet.controller;

import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
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

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        String safeCategory = SecurityValidator.validCategory(category, false);
        String safeKeyword = SecurityValidator.cleanKeyword(keyword);
        List<Event> events = eventService.findAll(safeCategory, safeKeyword);
        Map<Long, List<Registration>> regMap = new HashMap<>();
        for (Event e : events) {
            regMap.put(e.getId(), registrationService.findByEventId(e.getId()));
        }
        model.addAttribute("events", events);
        model.addAttribute("regMap", regMap);
        model.addAttribute("category", safeCategory);
        model.addAttribute("keyword", safeKeyword);
        return "event/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("event", new Event());
        return "event/add";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        validateEvent(event, false);
        eventService.save(event);
        redirectAttributes.addFlashAttribute("msg", "项目添加成功");
        return "redirect:/event/list";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        SecurityValidator.validId(id, "项目编号");
        Event event = eventService.findById(id);
        if (event == null) {
            throw new BusinessException("比赛项目不存在");
        }
        model.addAttribute("event", event);
        return "event/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        validateEvent(event, true);
        if (eventService.findById(event.getId()) == null) {
            throw new BusinessException("比赛项目不存在");
        }
        eventService.update(event);
        redirectAttributes.addFlashAttribute("msg", "项目更新成功");
        return "redirect:/event/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteByGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "删除操作必须通过页面按钮提交，不允许直接修改URL执行");
        return "redirect:/event/list";
    }

    @GetMapping("/finish/{id}")
    public String finishByGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "结束比赛必须通过页面按钮提交，不允许直接修改URL执行");
        return "redirect:/event/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        SecurityValidator.validId(id, "项目编号");
        if (eventService.findById(id) == null) {
            throw new BusinessException("比赛项目不存在");
        }
        if (eventMapper.countRegistrationByEventId(id) > 0) {
            redirectAttributes.addFlashAttribute("error", "该项目已有报名记录，不可删除");
            return "redirect:/event/list";
        }
        eventService.deleteById(id);
        redirectAttributes.addFlashAttribute("msg", "项目删除成功");
        return "redirect:/event/list";
    }

    @PostMapping("/finish")
    public String finish(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        SecurityValidator.validId(id, "项目编号");
        Event event = eventService.findById(id);
        if (event == null) {
            throw new BusinessException("比赛项目不存在");
        }
        event.setStatus(2);
        eventService.update(event);
        redirectAttributes.addFlashAttribute("msg", "比赛已结束：" + event.getName());
        return "redirect:/event/list";
    }

    private void validateEvent(Event event, boolean requireId) {
        if (requireId) {
            SecurityValidator.validId(event.getId(), "项目编号");
        }
        event.setName(SecurityValidator.validText(event.getName(), "项目名称", 50));
        event.setCategory(SecurityValidator.validCategory(event.getCategory(), true));
        event.setTopN(SecurityValidator.validTopN(event.getTopN()));
        event.setLocation(SecurityValidator.validText(event.getLocation(), "比赛地点", 50));
        if (event.getEventDate() == null) {
            throw new BusinessException("比赛日期不能为空");
        }
        if (event.getEventTime() == null) {
            throw new BusinessException("比赛时间不能为空");
        }
        if (requireId) {
            event.setStatus(SecurityValidator.validStatus(event.getStatus()));
        } else {
            event.setStatus(event.getStatus() == null ? 0 : SecurityValidator.validStatus(event.getStatus()));
        }
    }
}