package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShopCartService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sky.entity.Orders.PENDING_PAYMENT;
import static com.sky.entity.Orders.UN_PAID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ShopCartMapper shopCartMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private ShopCartService shopCartService;

    private Long setOrders(OrdersSubmitDTO ordersSubmitDTO, Long userId) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        // 微信支付
        orders.setPayMethod(1);
        //订单状态 待付款
        orders.setStatus(PENDING_PAYMENT);
        // 支付状态
        orders.setPayStatus(UN_PAID);
        // 根据地址写入对应的信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new RuntimeException("地址簿不存在");
        }
        // 收货人 手机号 地址
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName()
                + addressBook.getDistrictName() + addressBook.getDetail());
        orders.setUserId(userId);
        // 订单号：时间戳 + 用户 id，保证唯一、便于展示与支付对账
        orders.setNumber(System.currentTimeMillis() + String.valueOf(userId));
        orderMapper.insert(orders);
        return orders.getId();
    }

    private void setOrderDetail(Long userId, Long orderId) {
        List<ShoppingCart> shoppingCartList = shopCartMapper.getListByUserId(userId);
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

    }

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        Long userId = BaseContext.getCurrentId();

        // 写入订单表
        Long orderId = setOrders(ordersSubmitDTO, userId);

        // 写入订单明细表
        setOrderDetail(userId, orderId);
        // 清空购物车
        shopCartMapper.clean(userId);

        // 返回数据
        orderSubmitVO.setId(orderId);
        Orders ordersById = orderMapper.getOrderById(orderId);
        orderSubmitVO.setOrderAmount(ordersById.getAmount());
        orderSubmitVO.setOrderNumber(ordersById.getNumber());
        orderSubmitVO.setOrderTime(ordersById.getOrderTime());
        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    @Transactional
    public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        long total = page.getTotal();
        List<OrderVO> list = new ArrayList<>();
        for (Orders orders : page) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.getOrdersDetailByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);
            list.add(orderVO);
        }


        return new PageResult(total, list);
    }

    @Override
    public OrderVO getOrdersDetail(Long id) {
        Long userId = BaseContext.getCurrentId();


        OrderVO orderVO = new OrderVO();
        Orders orders = orderMapper.getOrderById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!Objects.equals(userId, orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrdersDetailByOrderId(orders.getId());
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        // 校验本人通过后才取消
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!Objects.equals(userId, order.getUserId())) {
            throw new RuntimeException("非用户本人");
        }

        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason("用户取消");
        orderMapper.update(order);
    }

    @Override
    @Transactional
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getOrderById(id);
        if (!Objects.equals(userId, orders.getUserId())) {
            throw new RuntimeException("非用户本人");
        }

        List<OrderDetail> ordersDetailList = orderDetailMapper.getOrdersDetailByOrderId(id);
        for (OrderDetail orderDetail : ordersDetailList) {
            ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
            shoppingCartDTO.setDishId(orderDetail.getDishId());
            shoppingCartDTO.setSetmealId(orderDetail.getSetmealId());
            shoppingCartDTO.setDishFlavor(orderDetail.getDishFlavor());
            // 原订单买了 number 份，就调用 number 次 add（每次 +1 或合并）
            int count = orderDetail.getNumber() == null ? 1 : orderDetail.getNumber();
            for (int i = 0; i < count; i++) {
                shopCartService.add(shoppingCartDTO);
            }
        }
    }

    // ---------- 管理端 Day09（业务逻辑请在此补全） ----------

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // TODO: PageHelper + pageQuery（不要 setUserId），封装 OrderVO 列表，可选 setOrderDishes
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        long total = page.getTotal();
        List<OrderVO> list = new ArrayList<>();
        for (Orders orders : page) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.getOrdersDetailByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);
            list.add(orderVO);
        }
        return new PageResult(total, list);
    }

    @Override
    @Transactional
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Integer tobeconfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        Integer inprogress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);
        orderStatisticsVO.setToBeConfirmed(tobeconfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(inprogress);
        return orderStatisticsVO;
    }

    @Override
    public OrderVO adminGetDetail(Long id) {
        return getOrdersDetail(id);
    }

    @Override
    @Transactional
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders order = orderMapper.getOrderById(ordersConfirmDTO.getId());
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (!Objects.equals(ordersConfirmDTO.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.CONFIRMED);
        orderMapper.update(order);
    }

    @Override
    @Transactional
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.getOrderById(ordersRejectionDTO.getId());
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 待接单 已付款  可拒单
        if (!Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.CANCELLED);
        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        //  已付款  拒单 退款
        if (Objects.equals(order.getPayStatus(), Orders.PAID)) {
            order.setStatus(Orders.REFUND);
        }
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    @Transactional
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders order = orderMapper.getOrderById(ordersCancelDTO.getId());
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 待接单 待付款  可取消
        if (!Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED) && !Objects.equals(order.getStatus(), Orders.PENDING_PAYMENT)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason(ordersCancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    @Transactional
    public void delivery(Long id) {
        Orders order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!Objects.equals(order.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    @Override
    @Transactional
    public void complete(Long id) {
        Orders order = orderMapper.getOrderById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!Objects.equals(order.getStatus(), Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.COMPLETED);
        orderMapper.update(order);
    }
}
