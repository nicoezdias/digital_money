package com.digital.money.msvc.api.users.services.impl;

import com.digital.money.msvc.api.users.clients.IAccountClient;
import com.digital.money.msvc.api.users.clients.dtos.AccountDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.CreateUserRequestDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.NewPassDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.VerficationRequestDTO;
import com.digital.money.msvc.api.users.controllers.requestDto.update.UpdateUserRequestDTO;
import com.digital.money.msvc.api.users.dtos.AuthUserDTO;
import com.digital.money.msvc.api.users.dtos.UserDTO;
import com.digital.money.msvc.api.users.dtos.UserWithAccountDTO;
import com.digital.money.msvc.api.users.entities.Role;
import com.digital.money.msvc.api.users.entities.User;
import com.digital.money.msvc.api.users.entities.Verified;
import com.digital.money.msvc.api.users.exceptions.*;
import com.digital.money.msvc.api.users.mappers.UserMapper;
import com.digital.money.msvc.api.users.repositorys.IRoleRepository;
import com.digital.money.msvc.api.users.repositorys.IUserRepository;
import com.digital.money.msvc.api.users.services.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {

    private static final int ROLE_USER = 2;
    private final IAccountClient accountClient;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bcrypt;
    private final EmailServiceImpl emailService;
    private final VerificationServiceImpl verificationService;

    public UserService(IAccountClient accountClient, IUserRepository userRepository, IRoleRepository roleRepository, UserMapper userMapper,
                       BCryptPasswordEncoder bcrypt, EmailServiceImpl emailService1, VerificationServiceImpl verificationService1) {
        this.accountClient = accountClient;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.bcrypt = bcrypt;
        this.emailService = emailService1;
        this.verificationService = verificationService1;
    }

    @Transactional
    @Override
    public UserDTO createUser(CreateUserRequestDTO userRequestDTO) throws Exception {

        Optional<User> entityResponseDni = userRepository.findByDni(userRequestDTO.getDni());
        Optional<User> entityResponseEmail = userRepository.findByEmail(userRequestDTO.getEmail());

        if (entityResponseDni.isPresent() || entityResponseEmail.isPresent()) {
            throw new HasAlreadyBeenRegistred(String
                    .format("The user %s is already registered", userRequestDTO.getName()));
        }

        Role role = roleRepository.findById(ROLE_USER).get();

        User userEntity = userMapper.mapToEntity(userRequestDTO);
        userEntity.setEmail(userRequestDTO.getEmail().toLowerCase());
        userEntity.setEnabled(true);
        userEntity.setAttempts(0);
        userEntity.setRole(role);
        userEntity.setPassword(bcrypt.encode(userEntity.getPassword()));
        userEntity.setVerified(false);

        User createdUser = userRepository.save(userEntity);
        AccountDTO account = accountClient.createAccount(createdUser.getUserId());
        createdUser.setAccountId(account.getAccountId());
        User completeInformationUser = userRepository.save(createdUser);

        sendVerificationMail(completeInformationUser.getEmail());

        return userMapper.mapToDto(completeInformationUser, account.getCvu(), account.getAlias());
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long userId, UpdateUserRequestDTO userDto, String token) throws UserNotFoundException, HasAlreadyBeenRegistred, PasswordNotChangedException, BadRequestException, ForbiddenException, JSONException {
        Optional<User> userEntity = userRepository.findByUserId(userId);
        if (userEntity.isEmpty()) {
            throw new UserNotFoundException("The user is not registered");
        }
        String tokenUserId = decodeToken(token, "user_id");
        Long tokenUserIdL = Long.valueOf(tokenUserId);
        if(userId!=tokenUserIdL){
            throw new ForbiddenException("You don't have access to that user");
        }

        Optional<User> entityResponseDni = userRepository.findByDni(userDto.getDni());
        Optional<User> entityResponseEmail = userRepository.findByEmail(userDto.getEmail());

        if (entityResponseDni.isPresent()) {
            throw new HasAlreadyBeenRegistred("The dni number is already registered");
        }

        if (entityResponseEmail.isPresent()) {
            throw new HasAlreadyBeenRegistred("The email address is already registered");
        }

        Optional<Long> dni = Optional.ofNullable(userDto.getDni());

        Optional<Integer> phone = Optional.ofNullable(userDto.getPhone());

        User user = userEntity.get();

        if (validateRequestObject(userDto.getName())) {
            user.setName(userDto.getName());
        }

        if (validateRequestObject(userDto.getLastName())) {
            user.setLastName(userDto.getLastName());
        }
        if (dni.isPresent()) {
            user.setDni(userDto.getDni());
        }

        if (phone.isPresent()) {
            user.setPhone(userDto.getPhone());
        }

        if (validateRequestObject(userDto.getEmail())) {
            user.setEmail(userDto.getEmail().toLowerCase());
        }

        if (validateRequestObject(userDto.getPassword())) {
            if (bcrypt.matches(userDto.getPassword(), user.getPassword())) {
            throw new PasswordNotChangedException ("The new password must be different than the previous one");
            } else { user.setPassword(bcrypt.encode(userDto.getPassword()));
            }
        }

        User userResponse = userRepository.save(user);
        AccountDTO account = accountClient.getAccountById(user.getAccountId(), token);

        return userMapper.mapToDto(userResponse, account.getCvu(), account.getAlias());
    }

    @Transactional(readOnly = true)
    @Override
    public UserWithAccountDTO getUserById(Long userId, String token) throws UserNotFoundException, ForbiddenException, JSONException {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundException(String
                        .format("The user with Id %d was not found", userId))
        );
        String tokenUserId = decodeToken(token, "user_id");
        Long tokenUserIdL = Long.valueOf(tokenUserId);
        if(userId!=tokenUserIdL){
            throw new ForbiddenException("You don't have access to that user");
        }
        AccountDTO account = accountClient.getAccountById(user.getAccountId(), token);
        System.out.println(account.toString());
        UserDTO userResponse = userMapper.mapToDto(user, account.getCvu(), account.getAlias());

        return UserWithAccountDTO.builder()
                .user(userResponse)
                .account(account)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUserByDni(Long dni, String token) throws UserNotFoundException, JSONException, ForbiddenException {
        User user = userRepository.findByDni(dni).orElseThrow(
                () -> new UserNotFoundException(String
                        .format("The user with dni %d was not found", dni))
        );
        String tokenUserDni = decodeToken(token, "dni");
        Long tokenUserDniL = Long.valueOf(tokenUserDni);
        if(!Objects.equals(dni, tokenUserDniL)){
            throw new ForbiddenException("You don't have access to that user");
        }

        AccountDTO account = accountClient.getAccountById(user.getAccountId(), token);
        return userMapper.mapToDto(user, account.getCvu(), account.getAlias());

    }

    @Transactional(readOnly = true)
    @Override
    public AuthUserDTO getUserByEmail(String email, String token) throws UserNotFoundException, JSONException, ForbiddenException {

        String emailInLowercase = email.toLowerCase();

        User user = userRepository.findByEmail(emailInLowercase).orElseThrow(
                () -> new UserNotFoundException(String
                        .format("The user with email %s was not found", email))
        );

        String tokenEmail = decodeToken(token, "email");
        if(!email.equals(tokenEmail)){
            throw new ForbiddenException("You don't have access to that user");
        }

        return userMapper.mapToAuthUserDto(user);
    }

    @Transactional(readOnly = true)
    public AuthUserDTO getUserByEmailLoging(String email) throws UserNotFoundException{
        String emailInLowercase = email.toLowerCase();

        User user = userRepository.findByEmail(emailInLowercase).orElseThrow(
                () -> new UserNotFoundException(String
                        .format("The user with email %s was not found", email))
        );

        return userMapper.mapToAuthUserDto(user);
    }

    @Transactional
    @Override
    public void updateAttempsFromUser(Long userId, boolean enabled, int attempts) throws UserNotFoundException {
        Optional<User> userEntity = userRepository.findByDni(userId);

        if (userEntity.isEmpty()) {
            throw new UserNotFoundException("The user is not registered");
        }

        User user = userEntity.get();
        user.setEnabled(enabled);
        user.setAttempts(attempts);

        userRepository.save(user);
    }

    @Override
    public void sendVerificationMail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        Integer codigo = verificationService.createVerificationCode(user.getUserId());
        emailService.sendVericationMail(user, codigo);
    }

    @Override
    public ResponseEntity<String> verificateUser(VerficationRequestDTO verficationRequestDTO, String token) throws JSONException {

        String[] jwtParts = token.split("\\.");
        JSONObject payload = new JSONObject(decodeToken(jwtParts[1]));
        String email = payload.getString("email");

        User user = userRepository.findByEmail(email).get();

        Verified verified = new Verified(user.getUserId(), verficationRequestDTO.getVerificationCode());
        Boolean checkedCode = verificationService.verificateCode(verified);

        if(!checkedCode)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The code entered is incorrect");

        user.setVerified(true);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("Your email has been successfully verified");
    }

    public void resendVerificationMail(String token) throws Exception {
        String[] jwtParts = token.split("\\.");
        JSONObject payload = new JSONObject(decodeToken(jwtParts[1]));
        String email = payload.getString("email");
        Optional<User> user = userRepository.findByEmail(email);
        if (!(user.get().getVerified())) {
                sendVerificationMail(email);
            } else throw new BadRequestException("The user email is already verified");
    }

    private static String decodeToken(String token) {
        return new String(Base64.getUrlDecoder().decode(token));
    }

    @Override
    public void forgotPassword(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String link = verificationService.createRecoverPasswordLink(user.get().getUserId());
            emailService.sendForgotPasswordEmail(user.get(),link);
        } else throw new UserNotFoundException("Please provide a valid email address in order to recover your password");
    }

    @Override
    public void resetPassword(String recoveryLink, NewPassDTO passDTO) throws PasswordNotChangedException {

        if(!passDTO.getPass().equals(passDTO.getPassRep()))
            throw new PasswordNotChangedException("The passwords don't match");

        String newPassword = passDTO.getPass();

        Boolean codigoVerificado = verificationService.verificateRecoveryLink(recoveryLink);

        if (!codigoVerificado)
            throw new PasswordNotChangedException("The link does not exist");

        String strUserId = recoveryLink.substring(0, recoveryLink.length()-6);
        Long userId = Long.parseLong(strUserId);

        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);

        if (bcrypt.matches(newPassword, user.getPassword())) {
            throw new PasswordNotChangedException ("The new password must be different than the previous one");
        }
        user.setPassword(bcrypt.encode(newPassword));
        userRepository.save(user);
    }

    private boolean validateRequestObject(String value) {
        return StringUtils.isNotBlank(value);
    }

    private boolean validatePassword(String newPassword, String oldPassword) {
        return bcrypt.matches(newPassword, oldPassword);
    }

    private AccountDTO buildAccount(String alias, String cvu) {
        double initialBalance = 0.0;
        return AccountDTO.builder()
                .alias(alias)
                .cvu(cvu)
                .availableBalance(initialBalance)
                .build();
    }

    //* ///////// UTILS ///////// *//
    private String decodeToken(String token, String search) throws JSONException {
        String[] jwtParts = token.split("\\.");
        JSONObject payload = new JSONObject(new String(Base64.getUrlDecoder().decode(jwtParts[1])));
        String keyInfo = payload.getString(search);
        return keyInfo;
    }

}
