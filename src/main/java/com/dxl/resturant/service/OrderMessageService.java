package com.dxl.resturant.service;

import com.dxl.resturant.dao.ProductMapper;
import com.dxl.resturant.dao.RestaurantMapper;
import com.dxl.resturant.dto.OrderMessageDTO;
import com.dxl.resturant.enumoperation.ProductStatus;
import com.dxl.resturant.enumoperation.ResturantStatus;
import com.dxl.resturant.po.ProductPO;
import com.dxl.resturant.po.RestaurantPO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    RestaurantMapper restaurantMapper;

    @Async
    public void handleMessage(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){
            channel.queueDeclare(
                    "queue.resturant",
                    true,
                    false,
                    false,
                    null
            );
            channel.queueBind(
                    "queue.resturant",
                    "exchange.order.resturant",
                    "key.resturant"
            );
            channel.basicConsume(
                    "queue.resturant",
                    true,
                    deliverCallback,
                    consumerTag -> {}
                    );
            while (true){
                Thread.sleep(100000);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        byte[] body = message.getBody();
        final ObjectMapper objectMapper = new ObjectMapper();
        final OrderMessageDTO orderMessageDTO = objectMapper.readValue(body, OrderMessageDTO.class);
        final Integer productId = orderMessageDTO.getProductId();
        ProductPO product = productMapper.selsctProduct(productId);
        RestaurantPO resturant = null;
        if (product != null) {
            resturant = restaurantMapper.selsctRestaurant(product.getRestaurantId());
        }
        if (product!=null && product.getStatus()== ProductStatus.AVALIABLE
            && resturant!=null && resturant.getStatus() == ResturantStatus.OPEN){
            orderMessageDTO.setPrice(product.getPrice());
            orderMessageDTO.setConfirmed(true);
        }else {
            orderMessageDTO.setConfirmed(false);
        }
        final String sendMessage = objectMapper.writeValueAsString(orderMessageDTO);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){
            channel.basicPublish(
                    "exchange.order.resturant",
                    "key.order",
                    null,
                    sendMessage.getBytes());
        }catch (Exception e){
            log.error(e.getMessage());
        }
    });
}
