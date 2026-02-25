package com.jupyter.order_service.Client;

import com.jupyter.order_service.Config.FeignAuthConfig;
import com.jupyter.order_service.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignAuthConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserDTO getUser(@PathVariable Long id);
}
