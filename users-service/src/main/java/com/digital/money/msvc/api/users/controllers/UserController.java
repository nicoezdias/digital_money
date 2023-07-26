package com.digital.money.msvc.api.users.controllers;

import com.digital.money.msvc.api.users.controllers.requestDto.CreateUserRequestDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.NewPassDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.VerficationRequestDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.update.UpdateUserRequestDTO;
import com.digital.money.msvc.api.users.dtos.UserDTO;
import com.digital.money.msvc.api.users.exceptions.*;
import com.digital.money.msvc.api.users.services.impl.UserService;
import jakarta.validation.Valid;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registrar Usuario
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestDTO userRequestDTO) throws Exception {
        if (Objects.isNull(userRequestDTO)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        UserDTO userDTO = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    /**
     * Actualizar informacion del usuario.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId,
                                        @Valid @RequestBody final UpdateUserRequestDTO userDto,
                                        @RequestHeader("Authorization") String token) throws UserNotFoundException, HasAlreadyBeenRegistred, PasswordNotChangedException, BadRequestException, ForbiddenException, JSONException {

        if (Objects.isNull(userDto)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        UserDTO userUpdate = userService.updateUser(userId, userDto, token);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByUserId(@PathVariable("id") Long userId,
                                          @RequestHeader("Authorization") String token) throws UserNotFoundException, ForbiddenException, JSONException {
        return ResponseEntity.ok(userService.getUserById(userId, token));
    }

    @GetMapping("/dni")
    public ResponseEntity<?> findByDni(@RequestParam("dni") Long dni,
                                       @RequestHeader("Authorization") String token) throws UserNotFoundException, ForbiddenException, JSONException {
        return ResponseEntity.ok(userService.getUserByDni(dni,token));
    }

    @GetMapping("/email")
    public ResponseEntity<?> findByEmail(@RequestParam("email") String email,
                                         @RequestHeader("Authorization") String token) throws UserNotFoundException, ForbiddenException, JSONException {
        return ResponseEntity.ok(userService.getUserByEmail(email, token));
    }

    @GetMapping("/loging")
    public ResponseEntity<?> findByEmailLoging(@RequestParam("email") String email) throws UserNotFoundException{
        return ResponseEntity.ok(userService.getUserByEmailLoging(email));
    }

    @PutMapping("/update/attempts/{dni}")
    public ResponseEntity<?> updateUserAttempts(@PathVariable Long dni,
                                                @RequestParam("enabled") boolean enabled,
                                                @RequestParam("attempts") int attempts) throws UserNotFoundException {

        userService.updateAttempsFromUser(dni, enabled, attempts);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/resend")
        public ResponseEntity<?> resendVerificationMail(@RequestHeader("Authorization") String token) throws Exception {
        userService.resendVerificationMail(token);
        return ResponseEntity.ok("Please check your inbox. You will receive an email with a new verification code");
    }

    @PutMapping("/verificate")
    public ResponseEntity<?> verificateCode(@RequestBody VerficationRequestDTO verficationRequestDTO,
                                            @RequestHeader("Authorization") String token) throws JSONException {
        return userService.verificateUser(verficationRequestDTO, token);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) throws UserNotFoundException {

        userService.forgotPassword(email);
        return ResponseEntity.ok("Please check your inbox. You will receive an email with a link to reset your password");
    }

    @PutMapping("/reset-password/{recoveryCode}")
    public ResponseEntity<?> resetPassword(
            @PathVariable("recoveryCode") String recoveryCode,
            @RequestBody NewPassDTO passwords)
            throws PasswordNotChangedException {

        userService.resetPassword(recoveryCode, passwords);
        return ResponseEntity.ok("Your password has been successfully updated");
    }
}
