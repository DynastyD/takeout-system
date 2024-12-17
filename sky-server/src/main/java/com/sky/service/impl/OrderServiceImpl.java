package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;

    private Orders orders;

    @Autowired
    private WebSocketServer webSocketServer;


    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //Handling various exceptions: address book is empty, shopping cart is empty
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // Insert a record into the order table
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        this.orders = orders;
        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        //Insert n records into the orderDetail table
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //Clean data of shopping cart
        shoppingCartMapper.deleteByUserId(userId);
        //Encapsulation return result
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId()).orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber()).orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }

    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {

        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //Call the WeChat payment interface to generate a prepayment transaction order
//        JSONObject json Object = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(),
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("The order has been paid");
//        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;
        Integer OrderPaidStatus = Orders.PAID;
        LocalDateTime check_out_time = LocalDateTime.now();
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, this.orders.getId());



        Map hashMap = new HashMap();
        hashMap.put("type",1); //1 means order reminder, 2 means customer urging order
        hashMap.put("orderId",this.orders.getId());
        hashMap.put("content","Order number: " + this.orders.getNumber());
        String json = JSON.toJSONString(hashMap);
        webSocketServer.sendToAllClient(json);

        return vo;

    }


    public void paySuccess(String outTradeNo) {

        // Query orders by order number
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // Update order status, payment method, payment status, and checkout time based on order ID
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

//        //Push messages to client browsers via websocket : type, orderId, content
//        HashMap hashMap = new HashMap();
//        hashMap.put("type",1); //1 means order reminder, 2 means customer urging order
//        hashMap.put("orderId",this.orders.getId());
//        hashMap.put("content","Order number: " + this.orders.getNumber());
//
//        //map to json
//        String json = JSON.toJSONString(hashMap);
//        webSocketServer.sendToAllClient(json);


    }

    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        //Setting up paging
        PageHelper.startPage(pageNum,pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        //Paging condition query
        Page<Orders> page =  orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();
        if (page != null && page.getTotal() > 0){
            for (Orders orders : page) {
                Long orderId = orders.getId();
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }

        }

        return new PageResult(page.getTotal(),list);
    }

    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public void userCancelById(Long id) throws Exception {
        Orders ordersDB = orderMapper.getById(id);

        if (ordersDB == null ){
            throw  new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //Order status 1 Waiting for payment 2 Waiting for order 3 Received 4 Delivering 5 Completed 6 Cancelled
        if (ordersDB.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        //The order is cancelled while it is in the pending order status and a refund is required
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
//            weChatPayUtil.refund(
//                    ordersDB.getNumber(), //Merchant order number
//                    ordersDB.getNumber(), //Merchant refund order number
//                    new BigDecimal(0.01),//Refund amount, unit: RMB
//                    new BigDecimal(0.01));//Original order amount

            orders.setPayStatus(Orders.REFUND);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("User Cancellation");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        // Query the current user id
        Long userId = BaseContext.getCurrentId();

        //Query the current order details based on the order ID
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        //Convert order details object to shoppingCart object
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            //Copy the product information in the original order details back to the shopping cart object
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        //Add shopping cart objects to the database in batches
        shoppingCartMapper.insertBatch(shoppingCartList);

    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        //For some order status, additional order dish information needs to be returned to convert Orders into OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(),orderVOList);
    }



    private List<OrderVO> getOrderVOList(Page<Orders> page){
        //Need to return order dish information and customize OrderVO response results
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)){
            for (Orders orders : ordersList) {
                // Copy the common fields to OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                //Encapsulate the order information into orderVO and add it to orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }

        return orderVOList;
    }

    private String getOrderDishesStr(Orders orders){
        //Query order dish details (dishes and quantities in the order)
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        //Concatenate each order dish information into a string (format: Kung Pao Chicken*3;)
        List<String> orderDishList = orderDetailList.stream().map(x ->{
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        return String.join("", orderDishList);

    }

    @Override
    public OrderStatisticsVO statistics() {
        //According to the status, query the number of orders waiting to be received,
        // waiting to be delivered, and being delivered.
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId());
        // The order can only be rejected if it exists and its status is 2 (pending order).
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

//        Integer payStatus = ordersDB.getPayStatus();
//        if (payStatus == Orders.PAID){
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("Request a Refund：{}", refund);
//        }

        //If the order is rejected and a refund is required, the order status, rejection reason, and cancellation time will be updated according to the order ID.
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());
        Integer payStatus = ordersDB.getPayStatus();
//        if (payStatus == 1){
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
//        }
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // Update order status, change to delivery
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        Orders ordersDB = orderMapper.getById(id);

        if (ordersDB == null || !ordersDB.getStatus().equals(4)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

            }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    @Override
    public void reminder(Long id) {
        Orders ordersDB = orderMapper.getById(id);

        if (ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map map = new HashMap();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content","order number: " + ordersDB.getNumber());


        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

}
