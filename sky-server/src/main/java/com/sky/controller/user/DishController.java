package com.sky.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C-end-dish browsing interface")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("Query dishes by category id")
    public Result<List<DishVO>> list(Long categoryId) {
        //Constructing redis key  regular: dish_categoryId
        String key = "dish_" + categoryId;
        //Determine if redis have dish data
        String s = stringRedisTemplate.opsForValue().get(key);
        List<DishVO> list = null;
        if (s != null && !s.isEmpty()) {
            try {
                list = JSONObject.parseArray(s, DishVO.class);

            } catch (Exception e) {
                System.err.println("JSON parsing error: " + e.getMessage());
            }
        }
        if (list != null && !list.isEmpty()) {
            return Result.success(list);
        }
        //If not , query in database and save in redis
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//Check out the dishes on sale

        list = dishService.listWithFlavor(dish);
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));

        return Result.success(list);

    }

}
