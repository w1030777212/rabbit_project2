package com.dxl.resturant.dao;


import com.dxl.resturant.po.ProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductMapper {

    @Select("SELECT id,name,price,restaurant_id restaurantId,status,date FROM product WHERE id = #{id}")
    ProductPO selsctProduct(Integer id);
}
