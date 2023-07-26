package com.digital.money.msvc.api.users.mappers;

import com.digital.money.msvc.api.users.controllers.requestDto.CreateUserRequestDTO;
import com.digital.money.msvc.api.users.dtos.AuthUserDTO;
import com.digital.money.msvc.api.users.dtos.UserDTO;
import com.digital.money.msvc.api.users.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Entity ->  mapToDto
     */
    public UserDTO mapToDto(User userEntity, String cvu, String alias) {

        log.info("*** userEntity {}", userEntity);
        UserDTO dto = objectMapper.convertValue(userEntity, UserDTO.class);
        dto.setCvu(cvu);
        dto.setAlias(alias);
        log.info("*** UserDTO {}", dto);

        return dto;
    }

    public AuthUserDTO mapToAuthUserDto(User userEntity) {

        log.info("*** userEntity {}", userEntity);
        AuthUserDTO dto = objectMapper.convertValue(userEntity, AuthUserDTO.class);
        log.info("*** UserDTO {}", dto);

        return dto;
    }

    /**
     * mapToDto -> Entity
     */
    public User mapToEntity(CreateUserRequestDTO userRequestDTO) {

        log.info("*** UserDTO Dto {}", userRequestDTO);
        User entity = objectMapper.convertValue(userRequestDTO, User.class);
        log.info("*** user Entity {}", entity);

        return entity;
    }
}
