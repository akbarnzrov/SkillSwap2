package com.skillswap.controllers;

import com.skillswap.dao.UserDAO;
import com.skillswap.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private Label statusLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            statusLabel.setText("Fill all fields!");
            return;
        }

        // Проверяем, не занято ли имя (простая проверка через попытку логина,
        // но лучше бы добавить checkUserExists в DAO, пока так)
        try {
            userDAO.registerUser(new User(username, password, phone));
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Success! Go back to login.");
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: Username might be taken.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}