package com.msvc.auth.server.sprgbt.clients;

import com.msvc.auth.server.sprgbt.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.QueryParam;

@FeignClient(name = "msvc-users-api")
public interface IUserFeignClient {

    @PutMapping("/users/update/attempts/{dni}")
    void updateUserAttempts(@PathVariable Long dni,
                            @RequestParam("enabled") boolean enabled,
                            @RequestParam("attempts") int attempts);

    @GetMapping("/users/loging")
    UserDTO findByEmail(@RequestParam String email);
}
