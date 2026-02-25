package com.jupyter.order_service.Controller;

import com.jupyter.order_service.Client.UserClient;
import com.jupyter.order_service.DTO.UserDTO;
import com.jupyter.order_service.Entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private UserClient userClient;

    @PostMapping
    public String createOrder(@RequestBody Order order) {

        UserDTO user = userClient.getUser(order.getUserId());

        return "Pedido criado para o usuário: " + user.name();
    }
}
