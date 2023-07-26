package com.digital.money.msvc.api.users.services.impl;

import com.digital.money.msvc.api.users.entities.Verified;
import com.digital.money.msvc.api.users.repositorys.IVerificationRepository;
import com.digital.money.msvc.api.users.services.IVerificationService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class VerificationServiceImpl implements IVerificationService {

    @Autowired
    private IVerificationRepository verificationRepository;

    @Override
    public Integer createVerificationCode(Long userId) {

        int min = 123123, max = 987987;

        Double dNumeroRand = Math.random()*(max-min)+min;
        Integer numero = dNumeroRand.intValue();

        Verified verified = new Verified(userId,numero);

        verificationRepository.save(verified);

        return numero;
    }

    @Override
    public Boolean verificateCode(Verified userVerification) {

        Verified dbSavedVerification = verificationRepository.findById(userVerification.getUserId()).orElseThrow(NoSuchElementException::new);

        if(!userVerification.getVerificationCode().equals(dbSavedVerification.getVerificationCode())){
            return Boolean.FALSE;
        }

        verificationRepository.delete(dbSavedVerification);

        return Boolean.TRUE;

    }

    @Override
    public String createRecoverPasswordLink(Long userId) {

            int min = 123123, max = 987987;

            Double dNumeroRand = Math.random()*(max-min)+min;

            Integer numero = dNumeroRand.intValue();

            Verified verified = new Verified(userId,numero);

            verificationRepository.save(verified);

            return "http://localhost:8080/users/reset-password/"+userId+numero;
    }

    public Boolean verificateRecoveryLink(String recoveryLink){

        String strUserId = recoveryLink.substring(0, recoveryLink.length()-6);
        String strRecoveryCode = recoveryLink.substring(recoveryLink.length()-6);

        Long userId = Long.parseLong(strUserId);
        Integer recoveryCode = Integer.parseInt(strRecoveryCode);

        Verified verified = verificationRepository.findById(userId).orElseThrow(NotFoundException::new);

        if(verified.getUserId().equals(userId) && verified.getVerificationCode().equals(recoveryCode))
            return Boolean.TRUE;

        return Boolean.FALSE;

    }

}
