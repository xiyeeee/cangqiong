package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

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
}
