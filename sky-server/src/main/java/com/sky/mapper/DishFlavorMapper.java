package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName: DishFlavorMapper
 * @Package:com.sky.mapper
 * @Description:
 * @author: Zihao
 * @date: 2024/12/10 - 20:41
 */
@Mapper
public interface DishFlavorMapper {

    void insertBatch(List<DishFlavor> flavors);
}
