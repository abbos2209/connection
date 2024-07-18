package uz.app.repository;

import lombok.SneakyThrows;
import uz.app.entity.History;
import uz.app.entity.Product;
import uz.app.entity.Role;
import uz.app.entity.User;
import uz.app.utils.TestConnection;
import static uz.app.utils.Context.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


public class AuthRepository {
    TestConnection testConnection = TestConnection.getInstance();


    public void save(User user) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into users(name,email,password,enabled,role,balance,sms_code) values('%s','%s','%s','%b','%s','%s','%s')",
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getEnabled(),
                    user.getRole().toString(),
                    user.getBalance().toString(),
                    user.getSmsCode()
                    );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Product product){
        Statement statement = testConnection.getStatement();
        String query = String.format("insert into products(name,price) values('%s','%s')",
                product.getName(),
                product.getPrice());
        try {
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public List<User> getAllUsers() {
        try {
            Statement statement = testConnection.getStatement();
            return getUsers(statement.executeQuery(String.format("select * from users;")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Product> getAllProducts(){
        Statement statement = testConnection.getStatement();
        try {
           return getProducts(statement.executeQuery(String.format("select * from products")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Optional<User> getByEmail(String email) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from users where email = '%s';", email));

            if (resultSet.next()){
            System.out.println(resultSet.getString("password"));
            int row = resultSet.getRow();
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setEnabled(resultSet.getBoolean("enabled"));
            Role role = Role.valueOf(resultSet.getString("role"));
            user.setBalance(resultSet.getInt("balance"));
            user.setRole(role);
            user.setSmsCode(resultSet.getString("sms_code"));
            return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
//        return users.stream().filter(user -> user.getEmail().equals(email)).findFirst();

    }

    public List<User> getUsers(ResultSet resultSet) {
        List<User> users = new ArrayList<>();
        try {
            while (true) {
                if (!resultSet.next()) break;
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setEnabled(resultSet.getBoolean("enabled"));
                user.setBalance(resultSet.getInt("balance"));
                user.setRole(Role.valueOf(resultSet.getString("role")));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<Product> getProducts(ResultSet resultSet){
        List<Product> products = new ArrayList<>();
        while (true){
            try {
                if (!resultSet.next()) break;
                Product product = new Product();
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getInt("price"));
                products.add(product);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
            return products;
    }

    public void deleteProduct(String in_name){
        Statement statement = testConnection.getStatement();
        String query = String.format("delete from products where name = '%s'",in_name);
        try {
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product addToBasket(String in_name){
        Statement statement = testConnection.getStatement();
        String query = String.format("select * from products where name = '%s' limit 1",in_name);
        try {
           return addProd(statement.executeQuery(query));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product addProd(ResultSet resultSet){
        Product product = new Product();
        while (true){
            try {
                if (!resultSet.next()) break;
                product.setName(resultSet.getString("name"));
                product.setPrice(resultSet.getInt("price"));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return product;
    }
@SneakyThrows
    public void saveProd(List<Product> productList){
        Statement statement = testConnection.getStatement();
        String query;
        for (Product product : productList) {
            query = String.format("insert into history(product_name,price,user_name) values('%s','%s','%s')",
                    product.getName(),product.getPrice().toString(),currentUser.getEmail());
                statement.execute(query);
        }
    }
@SneakyThrows
    public List<History> history(){
        Statement statement = testConnection.getStatement();
        String query = "select * from history";
        ResultSet resultSet;
            resultSet = statement.executeQuery(query);
        List<History> histories = new ArrayList<>();
        while (true){
            if (!resultSet.next()) break;
            History history = new History();
            history.setProductName(resultSet.getString("product_name"));
            history.setPrice(resultSet.getInt("price"));
            history.setUserName(resultSet.getString("user_name"));
            histories.add(history);
        }
        return histories;
    }
    @SneakyThrows
    public void deleteUser(String userName){
        Statement statement = testConnection.getStatement();
        String query = String.format("delete from users where email = '%s'",userName);
        statement.execute(query);
    }
    private static AuthRepository instance;

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }
}

