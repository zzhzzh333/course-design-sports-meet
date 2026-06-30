package com.sportsmeet.controller;

import com.sportsmeet.entity.Athlete;
import com.sportsmeet.service.AthleteService;
import com.sportsmeet.service.DepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/athlete")
public class AthleteController {

    private final AthleteService athleteService;
    private final DepartmentService departmentService;

    public AthleteController(AthleteService athleteService, DepartmentService departmentService) {
        this.athleteService = athleteService;
        this.departmentService = departmentService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) Long deptId,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        model.addAttribute("athletes", athleteService.findAll(deptId, keyword));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("deptId", deptId);
        model.addAttribute("keyword", keyword);
        return "athlete/list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("athlete", new Athlete());
        model.addAttribute("departments", departmentService.findAll());
        return "athlete/add";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Athlete athlete, RedirectAttributes redirectAttributes) {
        athleteService.save(athlete);
        redirectAttributes.addFlashAttribute("msg", "运动员添加成功");
        return "redirect:/athlete/list";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("athlete", athleteService.findById(id));
        model.addAttribute("departments", departmentService.findAll());
        return "athlete/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Athlete athlete, RedirectAttributes redirectAttributes) {
        athleteService.update(athlete);
        redirectAttributes.addFlashAttribute("msg", "运动员信息更新成功");
        return "redirect:/athlete/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (athleteService.hasRegistration(id)) {
            redirectAttributes.addFlashAttribute("error", "该运动员已有报名记录，不可删除");
            return "redirect:/athlete/list";
        }
        athleteService.deleteById(id);
        redirectAttributes.addFlashAttribute("msg", "运动员删除成功");
        return "redirect:/athlete/list";
    }
}