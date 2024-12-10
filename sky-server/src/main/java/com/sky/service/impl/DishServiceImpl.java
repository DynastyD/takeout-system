package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName: DishServiceImpl
 * @Package:com.sky.service.impl
 * @Description:
 * @author: Zihao
 * @date: 2024/12/10 - 20:21
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //add one data in dish table
        dishMapper.insert(dish);

        /**
         * 这里的useGenerateKeys="true"在DishFlavorMapper.xml里，表示需要这个主键值
         * 再通过 keyProperty=“id”,产生的主键值会赋给id属性
         * 就是insert语句执行完，产生的主键值会赋给id属性，这样service就能获取到生成的主键值
         */
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //Add N data to the flavor table
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());

    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //Determine whether the current dish can be deleted. Are there any dishes that are currently on sale?
        for (Long id : ids) {
           Dish dish = dishMapper.getById(id);
           if (dish.getStatus().equals(StatusConstant.ENABLE)){
               //The current dish is on sale and cannot be deleted
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }
        }
        //Determine whether the current dish can be deleted. Is it associated with a package?
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0){
            //The current dish is associated with a package
            throw  new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //Delete dish data in dishTable
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //Delete the flavor data associated with a dish
            dishFlavorMapper.deleteByDishId(id);
        }

    }
}
