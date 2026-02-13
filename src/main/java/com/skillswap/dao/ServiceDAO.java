package com.skillswap.dao;

import com.skillswap.database.DatabaseHandler;
import com.skillswap.entities.TutoringService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServiceDAO {
    private Connection connection;
    private Random random = new Random(); // Генератор чисел

    public ServiceDAO() {
        connection = DatabaseHandler.getInstance().getConnection();
    }


    public void addService(TutoringService service) throws SQLException {

        int randomId = 10000 + random.nextInt(90000);


        String query = "INSERT INTO services (id, title, price, type, subject, provider_id) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, randomId);          // <-- Наш случайный ID
        statement.setString(2, service.getTitle());
        statement.setDouble(3, service.getPrice());
        statement.setString(4, "TUTORING");
        statement.setString(5, service.getSubject());
        statement.setInt(6, service.getProviderId());

        statement.executeUpdate();
        System.out.println("Service published with ID: " + randomId);
    }

    public void deleteService(int serviceId, int userId) {
        String query = "DELETE FROM services WHERE id = ? AND provider_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, serviceId);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Service deleted successfully.");
            else System.out.println("Error: You can only delete your own services.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteAnyService(int serviceId) {
        String query = "DELETE FROM services WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, serviceId);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Service deleted by Admin.");
            else System.out.println("Error: Service not found.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<String> getAllServices() {
        List<String> list = new ArrayList<>();
        String query = "SELECT s.id, s.title, s.price, s.subject, u.username, u.phone " +
                "FROM services s JOIN users u ON s.provider_id = u.id ORDER BY s.id";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                list.add("ID: " + rs.getInt("id") + " | " + rs.getString("title") +
                        " (" + rs.getDouble("price") + "$) | Author: " +
                        rs.getString("username"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String> searchServices(String keyword) {
        List<String> list = new ArrayList<>();
        String query = "SELECT s.id, s.title, s.price, s.subject, u.username, u.phone " +
                "FROM services s JOIN users u ON s.provider_id = u.id " +
                "WHERE LOWER(s.title) LIKE LOWER(?) OR LOWER(s.subject) LIKE LOWER(?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add("ID: " + rs.getInt("id") + " | " + rs.getString("title") +
                        " (" + rs.getDouble("price") + "$) | Author: " +
                        rs.getString("username"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void bookCourse(int studentId, int serviceId) {
        String query = "INSERT INTO bookings (student_id, service_id) VALUES (?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, studentId);
            ps.setInt(2, serviceId);
            ps.executeUpdate();
            System.out.println("You have successfully enrolled!");
        } catch (SQLException e) {
            System.out.println("Error: Already enrolled or invalid ID.");
        }
    }

    public void cancelBooking(int studentId, int serviceId) {
        String query = "DELETE FROM bookings WHERE student_id = ? AND service_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, studentId);
            ps.setInt(2, serviceId);
            ps.executeUpdate();
            System.out.println("Enrollment canceled.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<String> getMyEnrollments(int studentId) {
        List<String> list = new ArrayList<>();
        String query = "SELECT s.id, s.title, u.phone FROM bookings b " +
                "JOIN services s ON b.service_id = s.id " +
                "JOIN users u ON s.provider_id = u.id " +
                "WHERE b.student_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add("ID: " + rs.getInt("id") + " | " + rs.getString("title") +
                        " | Contact: " + rs.getString("phone"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String> getMyCreatedServices(int providerId) {
        List<String> list = new ArrayList<>();
        String query = "SELECT id, title, price, subject FROM services WHERE provider_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, providerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add("ID: " + rs.getInt("id") + " | " + rs.getString("title") +
                        " | " + rs.getString("subject"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String> getMyStudents(int providerId) {
        List<String> list = new ArrayList<>();
        String query = "SELECT b.student_id, b.service_id, s.title, u.username, u.phone FROM bookings b " +
                "JOIN services s ON b.service_id = s.id " +
                "JOIN users u ON b.student_id = u.id " +
                "WHERE s.provider_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, providerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add("Student_ID: " + rs.getInt("student_id") +
                        " | Course_ID: " + rs.getInt("service_id") +
                        " | Course: " + rs.getString("title") +
                        " -> Student: " + rs.getString("username") +
                        " | Phone: " + rs.getString("phone"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}