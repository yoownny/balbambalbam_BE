package com.potato.balbambalbam.myReport.report.service;

import com.potato.balbambalbam.myReport.report.dto.ReportInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportInfoService {
    public ReportInfoDto getMyReportInfo(Long userId) {
        ReportInfoDto reportInfoDto = new ReportInfoDto();

        return reportInfoDto;
    }
}
