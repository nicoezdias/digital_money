package com.digital.money.msvc.api.users.services.impl;

import com.digital.money.msvc.api.users.entities.User;
import com.digital.money.msvc.api.users.repositorys.IUserRepository;
import com.digital.money.msvc.api.users.services.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emisor;

    @Override
    public void sendVericationMail(User user, Integer codigo) {
        try {

            String  receptor = user.getEmail();

            String asunto ="Bienvenido a Digital Money";
            String texto= "Hola "+user.getName()+", este mail confirma que se ha registrado exitosamente en Digital Money.\nPara poder verificar su cuenta por favor ingrese el siguiente código: "+codigo;

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(emisor);
            simpleMailMessage.setTo(receptor);
            simpleMailMessage.setSubject(asunto);
            simpleMailMessage.setText(texto);

            javaMailSender.send(simpleMailMessage);

        }
        catch (Exception e){

        }
    }

    @Override
    public void sendForgotPasswordEmail(User user, String link) {
        try {
            String  receptor = user.getEmail();
            String asunto ="Recuperar contraseña";
            String texto= "Hola "+user.getName()+", este mail confirma que se ha solicitado recuperar la contraseña de su cuenta en Digital Money.\nPara poder recuperar su contraseña por favor ingrese al siguiente link: "+link;

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(emisor);
            simpleMailMessage.setTo(receptor);
            simpleMailMessage.setSubject(asunto);
            simpleMailMessage.setText(texto);

            javaMailSender.send(simpleMailMessage);
        }
        catch (Exception e){ }
    }

}
