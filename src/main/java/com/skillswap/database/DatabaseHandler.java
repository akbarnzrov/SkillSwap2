package com.skillswap.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    private static DatabaseHandler instance;
    private Connection connection;

    private DatabaseHandler() {
        try {

            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = System.getProperty("user.name");
            String pass = "";

            connection = DriverManager.getConnection(url, user, pass);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() {

        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(50) NOT NULL, " +
                "phone VARCHAR(20))";


        String servicesTable = "CREATE TABLE IF NOT EXISTS services (" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(100), " +
                "price DOUBLE PRECISION, " +
                "type VARCHAR(50), " +
                "subject VARCHAR(50), " +
                "provider_id INTEGER REFERENCES users(id) ON DELETE CASCADE)";


        String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id SERIAL PRIMARY KEY, " +
                "student_id INTEGER REFERENCES users(id) ON DELETE CASCADE, " +
                "service_id INTEGER REFERENCES services(id) ON DELETE CASCADE)";


        String alterSeq = "ALTER SEQUENCE services_id_seq RESTART WITH 10000";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(servicesTable);
            stmt.execute(bookingsTable);

            try {
                stmt.execute(alterSeq);
            } catch (SQLException e) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}