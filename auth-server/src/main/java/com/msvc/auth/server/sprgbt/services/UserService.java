package com.msvc.auth.server.sprgbt.services;

import com.msvc.auth.server.sprgbt.clients.IUserFeignClient;
import com.msvc.auth.server.sprgbt.dtos.UserDTO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService implements IUserService, UserDetailsService {

    private IUserFeignClient userClient;

    @Autowired
    public UserService(IUserFeignClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {
            UserDTO user = this.userClient.findByEmail(email);

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
            log.info("Authenticated user: " + user.getName());

            return new User(user.getEmail(),
                    user.getPassword(),
                    user.isEnabled(),
                    true,
                    true,
                    true,
                    authorities);

        } catch (FeignException e) {
            String error =
                    String.format("Error in the login, the user %s is not registered in the system", email);
            log.error(error);

            throw new UsernameNotFoundException(error);
        }
    }

    @Override
    public UserDTO findUseByEmail(String email) {
        return this.userClient.findByEmail(email);
    }

    @Override
    public void updateUser(Long dni, boolean enabled, int attempts) {
        this.userClient.updateUserAttempts(dni, enabled, attempts);
    }
}
