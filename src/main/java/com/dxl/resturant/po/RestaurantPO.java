package com.dxl.resturant.po;


import com.dxl.resturant.enumoperation.ResturantStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class RestaurantPO {
    private Integer id;
    private String name;
    private String address;
    private ResturantStatus status;
    private Date date;
}
