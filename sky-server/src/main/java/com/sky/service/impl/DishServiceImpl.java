package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
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
}
