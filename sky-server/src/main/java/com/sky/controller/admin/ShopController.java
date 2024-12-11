package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: ShopController
 * @Package:com.sky.controller.admin
 * @Description:
 * @author: Zihao
 * @date: 2024/12/11 - 15:16
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "Store related interfaces")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("Set store opening status")
    public Result setStatus(@PathVariable Integer status){
        log.info("Set store opening status: {}", status==1 ?"opening":"close");
        stringRedisTemplate.opsForValue().set(KEY, String.valueOf(status));
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("Get store opening status")
    public Result<Integer> getStatus(){
        String status = stringRedisTemplate.opsForValue().get(KEY);
        Integer statu = Integer.valueOf(status);
        log.info("get store opening status: {}", statu==1 ?"opening":"close");
        return Result.success(statu);
    }
}
