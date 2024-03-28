package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
class ReportController {

    @Autowired
    ReportService reportService;


    @GetMapping("/turnoverStatistics")
    @ApiOperation("指定时间范围的营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额数据统计：{},{}",begin,end);
        return Result.success(reportService.getTurnoverStatistics(begin,end));

    }
    @GetMapping("/userStatistics")
    @ApiOperation("指定时间范围的用户数量统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户数统计，{},{}",begin,end);

        return Result.success(reportService.getuserStatistics(begin,end));

    }


    @GetMapping("/ordersStatistics")
    @ApiOperation("统计订单完成情况")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单完成情况，{},{}",begin,end);

        return Result.success(reportService.getOrdersStatistics(begin,end));

    }

    /**
     * 销量排名top10
     *
     *
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("销量排名top10")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("销量排名top10，{},{}",begin,end);

        return Result.success(reportService.top10(begin,end));

    }

}
