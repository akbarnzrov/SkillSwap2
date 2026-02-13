package com.skillswap.main;

import com.skillswap.dao.ServiceDAO;
import com.skillswap.dao.UserDAO;
import com.skillswap.entities.TutoringService;
import com.skillswap.entities.User;
import java.util.Scanner;
import java.util.List; // <--- ВОТ ЧЕГО НЕ ХВАТАЛО

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ServiceDAO serviceDAO = new ServiceDAO();
        UserDAO userDAO = new UserDAO();

        User currentUser = null;

        System.out.println("--- WELCOME TO SKILLSWAP 2.0 (CONSOLE MODE) ---");

        while (true) {
            while (currentUser == null) {
                System.out.println("\n--- LOGIN MENU ---");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Exit");
                System.out.print("Choice: ");

                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice == 1) {
                        System.out.print("Username: ");
                        String name = scanner.nextLine();
                        System.out.print("Password: ");
                        String pass = scanner.nextLine();
                        System.out.print("Phone: ");
                        String phone = scanner.nextLine();
                        userDAO.registerUser(new User(name, pass, phone));
                    } else if (choice == 2) {
                        System.out.print("Username: ");
                        String name = scanner.nextLine();
                        System.out.print("Password: ");
                        String pass = scanner.nextLine();
                        currentUser = userDAO.loginUser(name, pass);
                        if (currentUser != null) System.out.println("Welcome!");
                        else System.out.println("Error!");
                    } else if (choice == 0) System.exit(0);
                } else scanner.nextLine();
            }
            // Упрощенный выход, так как мы переходим на графику
            System.out.println("Logged in via Console. Please run App.java for GUI.");
            break;
        }
    }
}