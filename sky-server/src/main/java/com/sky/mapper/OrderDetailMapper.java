package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName: OrderDetailMapper
 * @Package:com.sky.mapper
 * @Description:
 * @author: Zihao
 * @date: 2024/12/13 - 0:07
 */
@Mapper
public interface OrderDetailMapper {
    void insertBatch(List<OrderDetail> orderDetailList);

    @Select("select * from order_detail where order_id = #{orderId} ")
    List<OrderDetail> getByOrderId(Long orderId);
}
