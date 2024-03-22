package com.techacademy.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService service) {
        this.reportService = service;
    }

    // 日報一覧画面
    @GetMapping("/list")
    public String getList(Model model, @AuthenticationPrincipal UserDetail userDetail) {

        if (userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            model.addAttribute("reportList", reportService.findAll());
            model.addAttribute("listSize", reportService.findAll().size());
        } else {
            model.addAttribute("reportList", reportService.findByEmployee(userDetail.getEmployee()));
            model.addAttribute("listSize", reportService.findByEmployee(userDetail.getEmployee()).size());
        }
        
        return "report/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/detail/{id}")
    public String getDetail(@PathVariable Integer id, Model model) {
    Optional<Report> optionalReport = reportService.findById(id);
    if (optionalReport.isPresent()) {
        Report report = optionalReport.get();
        model.addAttribute("report", report);
        return "report/detail";
    } else {
        // IDに対応するレポートが見つからない場合の処理
        return "redirect:/report/list";
    }
}

    //日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        report.setEmployee(userDetail.getEmployee());
        return "report/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // 入力チェック
        if (res.hasErrors()) {
            
            return create(report, userDetail, model);
        }
        
        report.setEmployee(userDetail.getEmployee());
        ErrorKinds result = reportService.save(report);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail, model);
        }

        return "redirect:/report/list";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {

        ErrorKinds result = reportService.delete(id, null);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id).orElse(null));
            return "report/detail";
        }

        return "redirect:/report/list";
    }

    // 日報情報更新処理
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable Integer id, Model model, UserDetail userDetail) {
        if (id != null) {
            Optional<Report> optionalReport = reportService.findById(id);
            if (optionalReport.isPresent()) {
                Report report = optionalReport.get();
                model.addAttribute("report" ,report);
                return "report/update";
            } else {
                return "redirect:/report/list";
            }
        }
        
        return "report/update";
    }
    
    @PostMapping(value = "/{id}/update")
    public String update(@ModelAttribute("report") @Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        if (res.hasErrors()) {
            model.addAttribute("report" ,report);
            return edit(null, model, userDetail);
        }
        ErrorKinds result = reportService.update(report.getId(), report, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report" ,report);
            return edit(null, model, userDetail);
        }
        return "redirect:/report/list";
        
    }
    
}

