package com.dxl.resturant.dao;


import com.dxl.resturant.po.RestaurantPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RestaurantMapper {

    @Select("SELECT id,name,address,status,settlement_id settlementId,date FROM restaurant WHERE id = #{id}")
    RestaurantPO selsctRestaurant(Integer id);
}
