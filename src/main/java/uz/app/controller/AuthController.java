package uz.app.controller;


import uz.app.entity.Role;
import uz.app.entity.User;
import uz.app.repository.AuthRepository;
import uz.app.service.AuthService;
import uz.app.service.Confirmation;
import uz.app.service.SimpleMailSender;

import java.util.ArrayList;
import java.util.Optional;

import static uz.app.utils.Utill.getInteger;
import static uz.app.utils.Utill.getString;

public class AuthController {
    AuthService authService=AuthService.getInstance();
    AuthRepository authRepository =AuthRepository.getInstance();

    public void  service(){
        while (true){
            switch (getInteger("""
                    0 exit
                    1 sign in
                    2 sign up
                    3 users
                    4 confirm sms
                    """)){
                case 0->{
                    System.out.println("see you soon!");
                    return;
                }
                case 1->{
                    String email = getString("enter email");
                    String password = getString("enter password");
                    authService.signIn(email,password);
                }
                case 2->{
                    String name = getString("enter name");
                    String email = getString("enter email");
                    String password = getString("enter password");
                    Integer balance = getInteger("enter balance");
                    User user =new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setRole(Role.USER);
                    user.setBalance(balance);
                    user.setEnabled(false);
                    authService.signUp(user);
                }
                case 3->{
                    authService.allUsers();
                }

                case 4 -> {
                    String email = getString("enter your email");
                    String code = getString("enter your code");
                    checkSmsConfirmation(new Confirmation(email,code));
                }

            }
        }
    }
    public void checkSmsConfirmation(Confirmation confirmation) {
        Optional<User> optionalUser = authRepository.getByEmail(confirmation.email());
        System.out.println(optionalUser.get());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.getSmsCode().equals(confirmation.code())){
                user.setEnabled(true);
                authRepository.deleteUser(user.getEmail());
                authRepository.save(user);
                return;
            }
        }
        System.out.println("wrong email");
    }
}
