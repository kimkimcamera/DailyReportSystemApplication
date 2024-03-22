package com.techacademy.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    Report findByReportDateAndEmployee(LocalDate reportDate, Employee employee);
    
}