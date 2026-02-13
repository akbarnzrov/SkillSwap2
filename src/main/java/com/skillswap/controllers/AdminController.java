package com.skillswap.controllers;

import com.skillswap.dao.ServiceDAO;
import com.skillswap.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class AdminController {

    @FXML private ListView<String> listView;
    @FXML private Label statusLabel;
    @FXML private Button deleteButton;

    private ServiceDAO serviceDAO = new ServiceDAO();
    private UserDAO userDAO = new UserDAO();


    private int currentMode = 0;

    @FXML
    private void showUsers() {
        currentMode = 1;
        statusLabel.setText("All Registered Users");
        deleteButton.setVisible(true);
        deleteButton.setText("DELETE SELECTED USER");
        listView.getItems().setAll(userDAO.getAllUsers());
    }

    @FXML
    private void showCourses() {
        currentMode = 2;
        statusLabel.setText("All Courses in System");
        deleteButton.setVisible(true);
        deleteButton.setText("DELETE SELECTED COURSE");
        listView.getItems().setAll(serviceDAO.getAllServices());
    }

    @FXML
    private void deleteSelectedItem() {
        String selected = listView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Error", "Please select an item from the list first!");
            return;
        }


        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Irreversible Action");
        confirm.setContentText("Are you sure you want to delete:\n" + selected);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {


            int id = extractId(selected, "ID: ");

            if (id == -1) {
                showAlert("Error", "Could not parse ID from selection.");
                return;
            }

            if (currentMode == 1) {
                userDAO.deleteUser(id);
                showUsers(); // Обновляем список
                showAlert("Success", "User deleted.");
            } else if (currentMode == 2) {
                serviceDAO.deleteAnyService(id);
                showCourses(); // Обновляем список
                showAlert("Success", "Course deleted.");
            }
        }
    }


    private int extractId(String raw, String prefix) {
        try {
            int start = raw.indexOf(prefix);
            if (start == -1) return -1;
            start += prefix.length();
            int end = raw.indexOf(" |", start);
            if (end == -1) end = raw.length();
            return Integer.parseInt(raw.substring(start, end).trim());
        } catch (Exception e) {
            return -1;
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}