package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShoppingCartController
 * @Package:com.sky.controller.user
 * @Description:
 * @author: Zihao
 * @date: 2024/12/12 - 16:45
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "client cart realtive interface")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("add goods to cart")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){

        log.info("add cart, goods :{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
