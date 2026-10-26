package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
