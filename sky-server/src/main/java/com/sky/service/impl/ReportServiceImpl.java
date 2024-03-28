package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    UserMapper userMapper;

    //指定时间范围的营业额统计
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //该集合用于存放从begin到end的日期
        List<LocalDate> dateList=new ArrayList<>();
        LocalDate temp=begin;
        while(!temp.equals(end)){
            dateList.add(temp);
            temp=temp.plusDays(1);
        }
        String join = StringUtils.join(dateList, ",");

        //集合用于确定每一天的营业额数据,营业额是所有状态为已完成的订单的金额的合计
        List<Double> moneyList=new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);//一天内的时间最小值
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);//一天内的时间最大值
            Double v = orderMapper.sumBtTimeAndStatus(beginTime, endTime, Orders.COMPLETED);
            if(v==null){
                v=0.0;
            }

//      Map map=new HashMap();
////            map.put("beginTime",beginTime);
////            map.put("endTime",endTime);
////            map.put("status",Orders.COMPLETED);
////
////            Double v = orderMapper.sumByMap(map);
////            if(v==null){
////                v=0.0;
////            }
//
            moneyList.add(v);
        }
        String join_money = StringUtils.join(moneyList, ",");


        return TurnoverReportVO.builder()
                .dateList(join)
                .turnoverList(join_money)
                .build();
    }


    //统计指定时间内的用户数据
    @Override
    public UserReportVO getuserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        LocalDate temp=begin;
        while(!temp.equals(end)){
            dateList.add(temp);
            temp=temp.plusDays(1);
        }
        String join_date = StringUtils.join(dateList, ",");

        List<Integer> addUserNum=new ArrayList<>();
        List<Integer> totalUserNum=new ArrayList<>();

        for (LocalDate localDate : dateList) {
            Map useMap=new HashMap();
            useMap.put("end",LocalDateTime.of(localDate, LocalTime.MIN));

            totalUserNum.add(userMapper.countByMap(useMap));

        }
        String join_total_num = StringUtils.join(totalUserNum, ",");

        for(int i=0;i<totalUserNum.size();i++){
            if(i==0){
                addUserNum.add(totalUserNum.get(0));
            }
            else{
                addUserNum.add(totalUserNum.get(i)-totalUserNum.get(i-1));
            }
        }

        String join_add_num = StringUtils.join(addUserNum, ",");

        return UserReportVO.builder()
                .dateList(join_date)
                .newUserList(join_add_num)
                .totalUserList(join_total_num)
                .build();

    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //该集合用于存放从begin到end的日期
        List<LocalDate> dateList=new ArrayList<>();
        LocalDate temp=begin;
        while(!temp.equals(end)){
            dateList.add(temp);
            temp=temp.plusDays(1);
        }
        String join_date = StringUtils.join(dateList, ",");


        //遍历datelist集合，获得每天的总订单数和有效订单数
        List<Integer> totalNumList=new ArrayList<>();
        List<Integer> validlNumList=new ArrayList<>();
        Integer tempTotalNum;
        Integer totalNum=0;
        Integer tempValidlNum;
        Integer validlNum=0;
        for (LocalDate localDate : dateList) {

            //查询每天的订单总数
            Map map=new HashMap();
            map.put("begin",LocalDateTime.of(begin,LocalTime.MIN));
            map.put("end",LocalDateTime.of(end,LocalTime.MAX));

            tempTotalNum=orderMapper.countByMap(map);
            totalNumList.add(tempTotalNum);
            totalNum+=tempTotalNum;

            //查询每天的有效订单数
            map.put("status",Orders.COMPLETED);
            tempValidlNum=orderMapper.countByMap(map);
            validlNumList.add(tempValidlNum);
            validlNum+=tempValidlNum;

        }
        String join_total = StringUtils.join(totalNumList, ",");
        String join_valid = StringUtils.join(totalNumList, ",");

        Double valid_rate= ((double)validlNum/totalNum);



        return OrderReportVO.builder()
                .dateList(join_date)
                .totalOrderCount(totalNum)
                .orderCountList(join_total)
                .validOrderCountList(join_valid)
                .validOrderCount(validlNum)
                .orderCompletionRate(valid_rate)
                .build();
    }


    //统计指定时间内的销量前十
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {

        List<GoodsSalesDTO> top10 = orderMapper.getTop10(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        List<String> names=new ArrayList<>();
        List<Integer> numbers=new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : top10) {
            names.add(goodsSalesDTO.getName());
            numbers.add(goodsSalesDTO.getNumber());
        }
        String join_name = StringUtils.join(names, ",");
        String join_number = StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder()
                .nameList(join_name)
                .numberList(join_number)
                .build();


    }


}
