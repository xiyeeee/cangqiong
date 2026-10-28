package com.sky.task;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/28
 */

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/28
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    OrderMapper orderMapper;

    // 处理超时订单
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void processTimeOutOrder() {
        log.info("超时定时任务:{}", LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, localDateTime);
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时, 自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    // 处理 一直在派送中的
    // 每天1点处理
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void processDeliveryOrder() {
        log.info(" 定时处理 一直在派送中的 任务:{}", LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, localDateTime);
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }

}
