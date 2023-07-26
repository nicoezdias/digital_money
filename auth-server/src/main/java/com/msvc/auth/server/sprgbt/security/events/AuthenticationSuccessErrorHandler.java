package com.msvc.auth.server.sprgbt.security.events;

import com.msvc.auth.server.sprgbt.dtos.UserDTO;
import com.msvc.auth.server.sprgbt.services.IUserService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

    private IUserService userService;

    @Autowired
    public AuthenticationSuccessErrorHandler(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {

        if (authentication.getDetails() instanceof WebAuthenticationDetails ||
                authentication.getName().equalsIgnoreCase("app")) { //ClientId
            return;
        }

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String mensaje = "Success Login: " + user.getUsername();
        log.info(mensaje);

        UserDTO userResponse = userLogin(authentication.getName());

        if (userResponse.getAttempts() > 0) {
            userResponse.setAttempts(0);
            updateUserAttemp(userResponse.getDni(), userResponse);
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        String mensaje = "Error in the Login: " + exception.getMessage();
        log.error(mensaje);

        try {
            UserDTO userResponse = userLogin(authentication.getName());

            //Attempts logic
            userResponse.setAttempts(userResponse.getAttempts() + 1);
            if (userResponse.getAttempts() >= 3) {
                userResponse.setEnabled(false);
                log.info(String.format("Maximum attempts allowed, the user %s was disabled", userResponse.getName()));
            }
            updateUserAttemp(userResponse.getDni(), userResponse);

        } catch (FeignException e) {
            log.error(String.format("The user %s is not registered in the system", authentication.getName()));
        }
    }

    private UserDTO userLogin(String email) {
        return this.userService.findUseByEmail(email);
    }

    private void updateUserAttemp(Long dni, UserDTO userDto) {
        this.userService.updateUser(dni, userDto.isEnabled(), userDto.getAttempts());
    }
}
