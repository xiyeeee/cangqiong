package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-订单管理（Day09）
 */
@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单条件查询（分页）
     * 传参：Query 参数，见 {@link OrdersPageQueryDTO}
     * 示例：/admin/order/conditionSearch?page=1&pageSize=10&status=2&phone=138&beginTime=2022-06-01 00:00:00&endTime=2022-06-30 23:59:59
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单条件查询")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单条件查询：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计（待接单、待派送、派送中）
     */
    @GetMapping("/statistics")
    @ApiOperation("订单数量统计")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO vo = orderService.statistics();
        return Result.success(vo);
    }

    /**
     * 查询订单详情
     *
     * @param id 订单 id
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        OrderVO orderVO = orderService.adminGetDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     * Body: { "id": 1, "status": 3 }
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单：{}", ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * Body: { "id": 1, "rejectionReason": "库存不足" }
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单：{}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 管理端取消订单
     * Body: { "id": 1, "cancelReason": "客户要求取消" }
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("管理端取消订单：{}", ordersCancelDTO);
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送
     *
     * @param id 订单 id
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送")
    public Result delivery(@PathVariable Long id) {
        log.info("派送订单：{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成
     *
     * @param id 订单 id
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成")
    public Result complete(@PathVariable Long id) {
        log.info("完成订单：{}", id);
        orderService.complete(id);
        return Result.success();
    }
}
