package com.sky.controller.user;

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
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "Store related interfaces")
@Slf4j
public class ShopController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/status")
    @ApiOperation("Get store opening status")
    public Result<Integer> getStatus(){
        String status = stringRedisTemplate.opsForValue().get("SHOP_STATUS");
        Integer statu = Integer.valueOf(status);
        log.info("get store opening status: {}", statu==1 ?"opening":"close");
        return Result.success(statu);
    }
}
