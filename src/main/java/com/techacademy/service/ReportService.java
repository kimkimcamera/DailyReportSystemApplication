package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import com.techacademy.constants.ErrorKinds;
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
    
    public Optional<Report> findById(Integer id) {
        return reportRepository.findById(id);
    }
    
    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
        
        LocalDate reportDate = report.getReportDate();
        
        // ログイン中の従業員のコードと入力された日付の日報を検索する
        Report existingReport = reportRepository.findByReportDateAndEmployee(reportDate, report.getEmployee());
        if (existingReport != null) {
            // 既存の日報が存在する場合はエラーを返す
            return ErrorKinds.DATECHECK_ERROR;
        }
        
        
        //createdAtフィールドに現在の日時をセットする
        report.setCreatedAt(LocalDateTime.now());
        
        //updatedAtフィールドに現在の日時をセットする
        report.setUpdatedAt(LocalDateTime.now());
        
        report.setDeleteFlg(false);
        
        try {
            reportRepository.save(report);
            return ErrorKinds.SUCCESS;
        } catch (Exception ex) {
            return ErrorKinds.DUPLICATE_EXCEPTION_ERROR;
        }
        
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {
        try {
            Optional<Report> optionalReport = reportRepository.findById(id);
            if (optionalReport.isPresent()) {
            Report report = optionalReport.get();
            report.setDeleteFlg(true);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.delete(report);
            return ErrorKinds.SUCCESS;
        } else {
            return ErrorKinds.BLANK_ERROR;
        }
            } catch (DataIntegrityViolationException e) {
            return ErrorKinds.BLANK_ERROR;
        }
    }
  
    // 日報情報更新処理
    @Transactional
    public ErrorKinds update(Integer id, Report updateReport, UserDetail userDetail) {
        
        Optional<Report> optionalReport = reportRepository.findById(id);
        
        if (optionalReport.isPresent()) {
            Report reportToUpdate = optionalReport.get();
            
            if(!reportToUpdate.getReportDate().equals(updateReport.getReportDate())) {
             // ログイン中の従業員のコードと入力された日付の日報を検索する
                Report existingReport = reportRepository.findByReportDateAndEmployee(updateReport.getReportDate(), updateReport.getEmployee());
                if (existingReport != null) {
                    // 既存の日報が存在する場合はエラーを返す
                    return ErrorKinds.DATECHECK_ERROR;
                }
            }
            reportToUpdate.setReportDate(updateReport.getReportDate());
            reportToUpdate.setTitle(updateReport.getTitle());
            reportToUpdate.setContent(updateReport.getContent());
            reportToUpdate.setUpdatedAt(updateReport.getUpdatedAt());
        
            //updatedAtフィールドに現在の日時をセットする
            reportToUpdate.setUpdatedAt(LocalDateTime.now());
            
            reportRepository.save(reportToUpdate);
        
            return ErrorKinds.SUCCESS;
        } else {
            return ErrorKinds.BLANK_ERROR;
        }
    
    }
}

