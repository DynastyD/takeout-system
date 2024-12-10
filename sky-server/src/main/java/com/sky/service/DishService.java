package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * @ClassName: DishService
 * @Package:com.sky.service
 * @Description:
 * @author: Zihao
 * @date: 2024/12/10 - 20:20
 */
public interface DishService {

    /**
     * Add new dishes and corresponding flavors
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);
}
