package com.digital.money.msvc.api.users.services;

import com.digital.money.msvc.api.users.controllers.requestDto.CreateUserRequestDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.NewPassDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.VerficationRequestDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.update.UpdateUserRequestDTO;
import com.digital.money.msvc.api.users.dtos.AuthUserDTO;
import com.digital.money.msvc.api.users.dtos.UserDTO;
import com.digital.money.msvc.api.users.dtos.UserWithAccountDTO;
import com.digital.money.msvc.api.users.exceptions.*;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;

public interface IUserService {

    UserDTO createUser(CreateUserRequestDTO userRequestDTO) throws Exception;

    UserDTO updateUser(Long userId, UpdateUserRequestDTO userDto, String token) throws UserNotFoundException, HasAlreadyBeenRegistred, PasswordNotChangedException, BadRequestException, ForbiddenException, JSONException;

    UserWithAccountDTO getUserById(Long userId, String token) throws UserNotFoundException, ForbiddenException, JSONException;

    UserDTO getUserByDni(Long dni, String token) throws UserNotFoundException, JSONException, ForbiddenException ;

    AuthUserDTO getUserByEmail(String email, String token) throws UserNotFoundException, JSONException, ForbiddenException;

    void updateAttempsFromUser(Long userId, boolean enabled, int attempts) throws UserNotFoundException;

    void sendVerificationMail(String email);

    void resendVerificationMail(String token) throws Exception;
    ResponseEntity<String> verificateUser(VerficationRequestDTO verficationRequestDTO, String token) throws JSONException;
    void forgotPassword(String email) throws UserNotFoundException;
    void resetPassword(String recoveryCode, NewPassDTO passwords) throws PasswordNotChangedException;
}
