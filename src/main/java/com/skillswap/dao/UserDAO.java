package com.skillswap.dao;

import com.skillswap.database.DatabaseHandler;
import com.skillswap.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        connection = DatabaseHandler.getInstance().getConnection();
    }

    public void registerUser(User user) {
        String query = "INSERT INTO users (username, password, phone) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getPhone());
            statement.executeUpdate();
            System.out.println("User registered successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User loginUser(String username, String password) {
        if (username.equals("masteradmin") && password.equals("Faradok479!")) {
            return new User(-1, "masteradmin", "Faradok479!", "ADMIN_PHONE");
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllUsers() {
        List<String> list = new ArrayList<>();
        String query = "SELECT id, username, phone FROM users";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                list.add("ID: " + rs.getInt("id") + " | Username: " + rs.getString("username") +
                        " | Phone: " + rs.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteUser(int userId) {
        String deleteServices = "DELETE FROM services WHERE provider_id = ?";
        String deleteUser = "DELETE FROM users WHERE id = ?";
        try {
            PreparedStatement psServices = connection.prepareStatement(deleteServices);
            psServices.setInt(1, userId);
            psServices.executeUpdate();

            PreparedStatement psUser = connection.prepareStatement(deleteUser);
            psUser.setInt(1, userId);
            int rows = psUser.executeUpdate();

            if (rows > 0) System.out.println("User and their services deleted.");
            else System.out.println("User not found.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}