package com.digital.money.msvc.api.users.services;

import com.digital.money.msvc.api.users.entities.Verified;

public interface IVerificationService {

    Integer createVerificationCode(Long userId);
    Boolean verificateCode(Verified verified);

    String createRecoverPasswordLink(Long userId);
    Boolean verificateRecoveryLink(String link);
}
