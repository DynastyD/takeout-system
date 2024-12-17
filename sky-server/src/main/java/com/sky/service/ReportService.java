package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

/**
 * @ClassName: ReportService
 * @Package:com.sky.service
 * @Description:
 * @author: Zihao
 * @date: 2024/12/17 - 18:19
 */
public interface ReportService {

    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
