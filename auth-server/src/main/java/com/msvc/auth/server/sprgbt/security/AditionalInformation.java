package com.msvc.auth.server.sprgbt.security;

import com.msvc.auth.server.sprgbt.dtos.UserDTO;
import com.msvc.auth.server.sprgbt.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AditionalInformation implements TokenEnhancer {

    private IUserService userService;

    @Autowired
    public AditionalInformation(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        UserDTO userInfo = userService.findUseByEmail(authentication.getName());

        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put("user_id", userInfo.getUserId());
        mapInfo.put("dni", userInfo.getDni());
        mapInfo.put("name", userInfo.getName());
        mapInfo.put("email", userInfo.getEmail());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(mapInfo);

        return accessToken;
    }
}
