package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: OrderController
 * @Package:com.sky.controller.user
 * @Description:
 * @author: Zihao
 * @date: 2024/12/12 - 23:54
 */
@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "User-side related interfaces")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("User places an order")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    @ApiOperation("Order Payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("Order Payment：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("Generate prepayment transaction order：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("Historical order query")
    public Result<PageResult> page(int page, int pageSize, Integer status){
      PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);

      return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("Check order details")
    public Result<OrderVO> details(@PathVariable ("id") Long id){
     OrderVO orderVO = orderService.details(id);
     return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("Cancel order")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    @PostMapping("repetition/{id}")
    @ApiOperation("One more order")
    public Result repetition(@PathVariable Long id){
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("Customer reminder")
    public Result reminder(@PathVariable("id") Long id){
        orderService.reminder(id);
        return Result.success();
    }
}
