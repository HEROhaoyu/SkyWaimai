package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;


public interface ReportService {
    //统计指定时间内的营业额数据
    TurnoverReportVO getTurnoverStatistics(LocalDate begin,LocalDate end);


    UserReportVO getuserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);
}
