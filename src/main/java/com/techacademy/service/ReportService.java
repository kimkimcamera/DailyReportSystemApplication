package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;

    }
    
    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        ErrorKinds validationResult = validateReport(report);
        if (validationResult != ErrorKinds.CHECK_OK) {
            return validationResult;
        }
        
        try {
            reportRepository.save(report);
            return ErrorKinds.SUCCESS;
        } catch (Exception ex) {
            return ErrorKinds.DUPLICATE_EXCEPTION_ERROR;
        }
    }
        
        private ErrorKinds validateReport(Report report) {
            if (report.getReportDate() == null || report.getTitle().isEmpty() || report.getContent().isEmpty()) {
            return ErrorKinds.BLANK_ERROR;
        }
        
        if (report.getTitle().length() > 100) {
            return ErrorKinds.RANGECHECK_ERROR;
        }
        
        if (report.getContent().length() > 600) {
            return ErrorKinds.RANGECHECK_ERROR;
        }
        
        return ErrorKinds.CHECK_OK;
        }
}
//
//    
//    // 日報情報更新処理
//    @Transactional
//    public ErrorKinds update(String code, Report updateReport) {
//        Report reportToUpdate = findByCode(code);
//        
//        if (reportToUpdate == null) {
//            return ErrorKinds.BLANK_ERROR;
//        }
//        
//        reportToUpdate.set(updateReport.get());
//        reportToUpdate.setTitle(updateReport.getTitle());
//        reportToUpdate.setContent(updateReport.getContent());
//        
//        ReportRepository.save(reportToUpdate);
//        
//        return ErrorKinds.SUCCESS;
//    }
//
