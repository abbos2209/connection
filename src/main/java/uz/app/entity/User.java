package uz.app.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor@NoArgsConstructor@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Boolean enabled;
    private Role role;
    private List<Product> basket;
    private Integer balance;
    private String smsCode;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", role=" + role +
                ", balance=" + balance +
                '}';
    }
}
