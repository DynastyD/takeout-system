package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @ClassName: OrderService
 * @Package:com.sky.service
 * @Description:
 * @author: Zihao
 * @date: 2024/12/12 - 23:58
 */
public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
