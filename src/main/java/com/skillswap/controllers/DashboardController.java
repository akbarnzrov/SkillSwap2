package com.skillswap.controllers;

import com.skillswap.dao.ServiceDAO;
import com.skillswap.entities.TutoringService;
import com.skillswap.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;
import java.sql.SQLException;

public class DashboardController {

    @FXML private ListView<String> listView;
    @FXML private Label userLabel;
    @FXML private Label statusLabel;

    private User currentUser;
    private ServiceDAO serviceDAO = new ServiceDAO();

    private int currentMode = 1;

    public void initData(User user) {
        this.currentUser = user;
        userLabel.setText("User: " + user.getUsername());
        showMarket();
    }

    @FXML
    private void showMarket() {
        currentMode = 1;
        statusLabel.setText("Marketplace (Select a course & Click 'Enroll')");
        listView.getItems().setAll(serviceDAO.getAllServices());
    }

    @FXML
    private void searchCourse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search");
        dialog.setHeaderText("Find a course");
        dialog.setContentText("Enter keyword:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(keyword -> {
            currentMode = 1;
            statusLabel.setText("Search results for: " + keyword);
            listView.getItems().setAll(serviceDAO.searchServices(keyword));
        });
    }

    @FXML
    private void enrollCourse() {
        String selected = listView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Error", "Please select a course from the list first!");
            return;
        }

        if (currentMode != 1) {
            showAlert("Info", "Go to 'Market' tab to enroll.");
            return;
        }

        int courseId = extractId(selected, "ID: ");
        if (courseId != -1) {
            serviceDAO.bookCourse(currentUser.getId(), courseId);
            showAlert("Success", "You enrolled in course ID: " + courseId);
            showMyEnrollments();
        } else {
            showAlert("Error", "Could not read ID from selection.");
        }
    }

    @FXML
    private void showMyEnrollments() {
        currentMode = 2;
        statusLabel.setText("My Enrollments (Select & Click 'Delete My Course' to Unenroll)");
        listView.getItems().setAll(serviceDAO.getMyEnrollments(currentUser.getId()));
    }

    @FXML
    private void createCourse() {
        String title = askInput("New Course", "Enter Course Title:");
        if (title == null) return;

        String priceStr = askInput("New Course", "Enter Price (e.g. 50.0):");
        if (priceStr == null) return;

        String subject = askInput("New Course", "Enter Subject:");
        if (subject == null) return;

        try {
            double price = Double.parseDouble(priceStr);
            // Пытаемся сохранить. Если таблицы кривые - тут вылетит ошибка SQLException
            serviceDAO.addService(new TutoringService(title, price, subject, currentUser.getId()));

            // Если код дошел сюда, значит точно сохранилось
            showAlert("Success", "Course Created Successfully!");

            // Сразу показываем мои курсы, чтобы убедиться
            deleteMyCourse();

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price format! Use dot (e.g. 50.0).");
        } catch (SQLException e) {
            // ВОТ ГЛАВНОЕ ИСПРАВЛЕНИЕ: Мы видим реальную ошибку
            e.printStackTrace();
            showAlert("Database Error", "CRITICAL ERROR: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void showMyStudents() {
        currentMode = 4;
        statusLabel.setText("My Students (Select & Click 'Delete My Course' to Kick)");
        listView.getItems().setAll(serviceDAO.getMyStudents(currentUser.getId()));
    }

    @FXML
    private void deleteMyCourse() {
        if (currentMode != 3 && currentMode != 2 && currentMode != 4) {
            currentMode = 3;
            statusLabel.setText("MY CREATED COURSES (Select & Click again to DELETE)");
            listView.getItems().setAll(serviceDAO.getMyCreatedServices(currentUser.getId()));
            return;
        }

        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select an item first!");
            return;
        }

        if (currentMode == 2) {
            int courseId = extractId(selected, "ID: ");
            if (courseId != -1) {
                serviceDAO.cancelBooking(currentUser.getId(), courseId);
                showAlert("Success", "Unenrolled successfully.");
                showMyEnrollments();
            }
        }
        else if (currentMode == 4) {
            int studentId = extractId(selected, "Student_ID: ");
            int courseId = extractId(selected, "Course_ID: ");
            if (studentId != -1 && courseId != -1) {
                serviceDAO.cancelBooking(studentId, courseId);
                showAlert("Success", "Student removed from course.");
                showMyStudents();
            }
        }
        else if (currentMode == 3) {
            int courseId = extractId(selected, "ID: ");
            if (courseId != -1) {
                serviceDAO.deleteService(courseId, currentUser.getId());
                showAlert("Success", "Course deleted.");
                listView.getItems().setAll(serviceDAO.getMyCreatedServices(currentUser.getId()));
            }
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

    private String askInput(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        Optional<String> res = dialog.showAndWait();
        return res.orElse(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}