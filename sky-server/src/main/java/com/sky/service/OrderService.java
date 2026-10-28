package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO getOrdersDetail(Long id);

    void cancel(Long id);

    void repetition(Long id);

    // ---------- 管理端 Day09 ----------

    /** 条件分页查询（不按当前用户过滤） */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /** 待接单 / 待派送 / 派送中 数量 */
    OrderStatisticsVO statistics();

    /** 管理端订单详情（可不校验下单用户） */
    OrderVO adminGetDetail(Long id);

    /** 接单：待接单 → 已接单 */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /** 拒单 */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /** 管理端取消 */
    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    /** 派送：已接单 → 派送中 */
    void delivery(Long id);

    /** 完成：派送中 → 已完成 */
    void complete(Long id);
}
