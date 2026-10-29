package com.sky.service.impl;/**
 * @Description
 * @Author xiye
 * @Date 2026/10/29
 */

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @Description
 * @Author xiye
 * @Date 2026/10/29
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        for (LocalDate localDate : dateList) {
            // 算开始和结束时间
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            // 状态已完成订单的金额
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        String d = StringUtils.join(dateList, ",");
        String t = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder().dateList(d).turnoverList(t).build();

    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        for (LocalDate localDate : dateList) {
            // 算开始和结束时间
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            // 状态已完成订单的金额
            Map map = new HashMap();
            map.put("end", endTime);

            // 先找总的用户 ,end在前面
            Integer totalUser = userMapper.countByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;

            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }
        String d = StringUtils.join(dateList, ",");
        String t = StringUtils.join(totalUserList, ",");
        String n = StringUtils.join(newUserList, ",");
        return UserReportVO.builder().dateList(d).totalUserList(t).newUserList(n).build();
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> validaOrderCountList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        for (LocalDate localDate : dateList) {
            // 算开始和结束时间
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer orderCount = orderMapper.orderSumByMap(map);
            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(orderCount);
            map.put("status", Orders.COMPLETED);
            Integer validaOrderCount = orderMapper.orderSumByMap(map);
            validaOrderCount = validaOrderCount == null ? 0 : validaOrderCount;
            validaOrderCountList.add(validaOrderCount);
        }

        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validaOrderCountList.stream().reduce(Integer::sum).get();
        Double rate = 0.0;
        if (totalOrderCount > 0) {
            rate = validOrderCount.doubleValue() / totalOrderCount;

        }
        String d = StringUtils.join(dateList, ",");
        String o = StringUtils.join(orderCountList, ",");
        String v = StringUtils.join(validaOrderCountList, ",");
        return OrderReportVO.builder().
                dateList(d).
                orderCountList(o).
                validOrderCountList(v).
                validOrderCount(validOrderCount).
                totalOrderCount(totalOrderCount).
                orderCompletionRate(rate).
                build();
    }
}
