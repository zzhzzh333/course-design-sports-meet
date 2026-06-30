package com.sportsmeet.controller;

import com.sportsmeet.common.BusinessException;
import com.sportsmeet.common.SecurityValidator;
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

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public String list(@RequestParam(required = false) Long deptId,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        Long safeDeptId = deptId == null ? null : SecurityValidator.validId(deptId, "院系编号");
        String safeKeyword = SecurityValidator.cleanKeyword(keyword);
        model.addAttribute("athletes", athleteService.findAll(safeDeptId, safeKeyword));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("deptId", safeDeptId);
        model.addAttribute("keyword", safeKeyword);
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
        validateAthlete(athlete, false);
        athleteService.save(athlete);
        redirectAttributes.addFlashAttribute("msg", "运动员添加成功");
        return "redirect:/athlete/list";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        SecurityValidator.validId(id, "运动员编号");
        Athlete athlete = athleteService.findById(id);
        if (athlete == null) {
            throw new BusinessException("运动员不存在");
        }
        model.addAttribute("athlete", athlete);
        model.addAttribute("departments", departmentService.findAll());
        return "athlete/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Athlete athlete, RedirectAttributes redirectAttributes) {
        validateAthlete(athlete, true);
        if (athleteService.findById(athlete.getId()) == null) {
            throw new BusinessException("运动员不存在");
        }
        athleteService.update(athlete);
        redirectAttributes.addFlashAttribute("msg", "运动员信息更新成功");
        return "redirect:/athlete/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteByGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "删除操作必须通过页面按钮提交，不允许直接修改URL执行");
        return "redirect:/athlete/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        SecurityValidator.validId(id, "运动员编号");
        if (athleteService.findById(id) == null) {
            throw new BusinessException("运动员不存在");
        }
        if (athleteService.hasRegistration(id)) {
            redirectAttributes.addFlashAttribute("error", "该运动员已有报名记录，不可删除");
            return "redirect:/athlete/list";
        }
        athleteService.deleteById(id);
        redirectAttributes.addFlashAttribute("msg", "运动员删除成功");
        return "redirect:/athlete/list";
    }

    private void validateAthlete(Athlete athlete, boolean requireId) {
        if (requireId) {
            SecurityValidator.validId(athlete.getId(), "运动员编号");
        }
        athlete.setStudentNo(SecurityValidator.validText(athlete.getStudentNo(), "学号", 30));
        athlete.setName(SecurityValidator.validText(athlete.getName(), "姓名", 30));
        athlete.setGender(SecurityValidator.validGender(athlete.getGender()));
        athlete.setDeptId(SecurityValidator.validId(athlete.getDeptId(), "院系编号"));
        athlete.setPhone(SecurityValidator.validOptionalText(athlete.getPhone(), "联系电话", 20));
    }
}