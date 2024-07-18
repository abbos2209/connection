package uz.app.service;

import uz.app.entity.Product;
import uz.app.entity.Role;
import uz.app.entity.User;
import uz.app.repository.AuthRepository;
import static uz.app.utils.Context.*;

import java.awt.*;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import static uz.app.utils.Utill.*;

public class AuthService {
    AuthRepository authRepository =AuthRepository.getInstance();
    SimpleMailSender simpleMailSender = new SimpleMailSender();
    public void signUp(User user) {
        Optional<User> optionalUser = authRepository.getByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            System.out.println("this email is already in use");
            return;
        }
        user.setSmsCode(generateCode());
        simpleMailSender.sendSmsToUser(user.getEmail(),user.getSmsCode());
        System.out.println("please check your email for confirmation");
        authRepository.save(user);

    }
    public void signIn(String email, String password) {
        Optional<User> optionalUser = authRepository.getByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                if (!user.getEnabled()){
                    System.out.println("please confirm your accaunt, check your email");
                    return;
                }
                if (user.getRole() == Role.ADMIN){
                    adminMenu();
                } else if (user.getRole() == Role.USER){
                    setCurrentUser(user);
                    userMenu();
                }
                System.out.println("welcome to system");
            }else {
                System.out.println("wrong password");
            }
            return;
        }else {
            System.out.println("no such email");
        }
    }

    private void userMenu() {
        while (true) {
            System.out.println("""
                    0 -> exit
                    1 -> show products
                    2 -> add product to basket
                    3 -> buy all
                    4 -> balance
                    """);
            switch (getInteger("")){
                case 0 -> {return;}
                case 1 -> {
                    allProducts();
                }
                case 2 -> {
                    currentUser.setBasket(new ArrayList<>());
                    String name = getString("enter name product");
                    Product product = authRepository.addToBasket(name);
                    currentUser.getBasket().add(product);
                }
                case 3 -> {
                    Integer sum = 0;
                    for (Product product : currentUser.getBasket()) {
                       sum += product.getPrice();
                    }
                    if (currentUser.getBalance() < sum) {
                        System.out.println("There are not enough funds on your balance");
                        break;
                    } else {
                            authRepository.saveProd(currentUser.getBasket());
                            currentUser.setBalance(currentUser.getBalance() - sum);
                        System.out.println("success!");
                    }
                }
            }
        }
    }

    private static AuthService authService;
    public static AuthService getInstance() {
        if (authService == null) {
            authService = new AuthService();
        }
        return authService;
    }

    public void adminMenu(){
        while (true) {
            System.out.println("""
                    0 -> exit
                    1 -> add product
                    2 -> delete product
                    3 -> show history
                    """);
            switch (getInteger("")){
                case 0 -> {break;}
                case 1 -> {
                    String name = getString("enter name product");
                    Integer price = getInteger("enter price product");
                    Product product = new Product(name,price);
                    authRepository.addProduct(product);
                }
                case 2 -> {
                    allProducts();
                    String name = getString("enter name product");
                    authRepository.deleteProduct(name);
                }
                case 3 -> {
                    authRepository.history().forEach(System.out::println);
                }
            }
        }
    }

    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(0, 10));
        }
        return sb.toString();
    }

    public void allUsers() {
        authRepository.getAllUsers().forEach(System.out::println);
    }

    public void allProducts(){
        authRepository.getAllProducts().forEach(System.out::println);
    }
}
