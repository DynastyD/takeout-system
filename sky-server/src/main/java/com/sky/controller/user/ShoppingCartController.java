package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    @ApiOperation("research shopping cart")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list =  shoppingCartService.showShoppingCart();
        return  Result.success(list);
    }

    @DeleteMapping("/clean")
    @ApiOperation("clean shopping cart")
    public Result clean(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }
}
