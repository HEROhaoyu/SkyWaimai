package com.sky.task;
/*创建定时任务类，实时处理任务*/

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /*处理超时订单*/
//    @Scheduled(cron = "0 * * * * ? ")//每分钟出发一次
//    @Scheduled(cron = "1/5 * * * * ? ")//5秒触发一次
    public void processTimeOutOrder(){
        //定时处理超时订单
        log.info("定时处理超时订单:{}", LocalDateTime.now());

        //选择处于代付款状态，且下单时间比现在时间晚超过15分钟的订单，判断为超时订单
        List<Orders> list=orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,LocalDateTime.now().plusMinutes(-15));

        if(list!=null&&list.size()>0){
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /*处理一直处于派送中的订单*/
//    @Scheduled(cron = "0 0 1 * * ? ")//每天凌晨一点触发一次
//    @Scheduled(cron = "0/5 * * * * ? ")//5秒触发一次
    public void processDeliveryOrder(){
        log.info("处理一直处于派送中的订单:{}",LocalDateTime.now());
        //选择处于代付款状态，且下单时间比现在时间晚超过15分钟的订单，判断为超时订单
        List<Orders> list=orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now().plusHours(-1));

        if(list!=null&&list.size()>0){
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }

    }
}
