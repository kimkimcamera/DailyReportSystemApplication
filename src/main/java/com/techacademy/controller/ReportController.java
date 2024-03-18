package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String getList(Model model) {
        model.addAttribute("reportList", reportService.findAll());
        return "report/list";
    }

    // 日報詳細画面
 // @GetMapping(value = "/{title}/detail")
 // public String getDetail(@PathVariable String title, Model model) {

 // model.addAttribute("report", reportService.findByTitle(title));
//        return "report/detail";
//    }

    //日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail) {
        report.setEmployee(userDetail.getEmployee());
        return "report/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail);
        }
        
        report.setEmployee(userDetail.getEmployee());
        ErrorKinds result = reportService.save(report, userDetail);
        if(result != ErrorKinds.SUCCESS) {
            return create(report, userDetail);
        }

        return "redirect:/report/list";
    }

//    // 日報削除処理
//    @PostMapping(value = "/delete")
//    public String delete(@PathVariable String title, Model model) {
//
//        ErrorKinds result = reportService.delete(title, );
//
//        if (ErrorMessage.contains(result)) {
//            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
//            model.addAttribute("report", reportService.findByTitle(title));
//            return detail(title, model);
//        }
//
//        return "redirect:/report/list";
//    }
//    
//    // 日報情報更新処理
//    @GetMapping(value = "/update")
//    public String edit(@PathVariable String content, Model model, Report report) {
//        if (content != null) {
//        report = reportService.findByTitle(title);
//        model.addAttribute("report" ,report);
//        } else {
//            model.addAttribute("report" ,report);
//        }
//        
//        return "report/update";
//        
//    }
//    
//    @PostMapping(value = "/update")
//    public String update(@PathVariable String title, @Validated Report report, BindingResult res, Model model) {
//        if (res.hasErrors()) {
//            return edit(null, model, report);
//        }
//        ErrorKinds result = reportService.update(title, report);
//
//        if (ErrorMessage.contains(result)) {
//            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
//            return create(report);
//        }
//        return "redirect:/report/list";
//        
//    }
    
}

