package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Category Management
 */
@RestController
@RequestMapping("/admin/category")
@Api(tags = "Classification related interfaces")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    @ApiOperation("Add new categories")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO){
        log.info("Add new categories：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }


    @GetMapping("/page")
    @ApiOperation("Category paging query")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("Category paging query：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }


    @DeleteMapping
    @ApiOperation("Delete Category")
    public Result<String> deleteById(Long id){
        log.info("Delete Category：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }


    @PutMapping
    @ApiOperation("Modify Category")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }


    @PostMapping("/status/{status}")
    @ApiOperation("Enable Disable Category")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id){
        categoryService.startOrStop(status,id);
        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("Search by type")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
