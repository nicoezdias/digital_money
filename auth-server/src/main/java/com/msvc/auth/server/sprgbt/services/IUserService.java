package com.msvc.auth.server.sprgbt.services;

import com.msvc.auth.server.sprgbt.dtos.UserDTO;

public interface IUserService {

    UserDTO findUseByEmail(String email);

    void updateUser(Long dni, boolean enabled, int attempts);
}
