package com.digital.money.msvc.api.users.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;

@Configuration
public class Utilities {

    /**
     * Method to Mapper Objects
     * @return ObjectMapper
     */
    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper objectmapper = new ObjectMapper();
        objectmapper.registerModule(new JavaTimeModule());
        return objectmapper;
    }

    @Bean("bcryp")
    public BCryptPasswordEncoder passwordEnconder() {
        //SecureRandom secure = new SecureRandom();
        //return new BCryptPasswordEncoder($2B, 20, secure);
        return new BCryptPasswordEncoder();
    }

    public String decode(String encodedString){
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

}
