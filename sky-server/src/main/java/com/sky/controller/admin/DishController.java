package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: DishController
 * @Package:com.sky.controller.admin
 * @Description:
 * @author: Zihao
 * @date: 2024/12/10 - 20:16
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "Food related interface")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("New dishes")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("New dishes: {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("Dish paging query")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("Dish paging query: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("Batch delete dishes")
    public Result delete(@RequestParam List<Long> ids){
        log.info("Batch delete dishes: {}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

}
