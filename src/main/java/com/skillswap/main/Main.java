package com.skillswap.main;

import com.skillswap.dao.ServiceDAO;
import com.skillswap.dao.UserDAO;
import com.skillswap.entities.TutoringService;
import com.skillswap.entities.User;
import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ServiceDAO serviceDAO = new ServiceDAO();
        UserDAO userDAO = new UserDAO();

        User currentUser = null;

        System.out.println("--- WELCOME TO SKILLSWAP 2.0 ---");

        while (true) {

            while (currentUser == null) {
                System.out.println("\n--- LOGIN MENU ---");
                System.out.println("1. Register (with Phone)");
                System.out.println("2. Login");
                System.out.println("0. Exit App");
                System.out.print("Choice: ");

                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice == 1) {
                        System.out.print("Username: ");
                        String name = scanner.nextLine();
                        System.out.print("Password: ");
                        String pass = scanner.nextLine();
                        System.out.print("Phone Number: ");
                        String phone = scanner.nextLine();

                        userDAO.registerUser(new User(name, pass, phone));

                    } else if (choice == 2) {
                        System.out.print("Username: ");
                        String name = scanner.nextLine();
                        System.out.print("Password: ");
                        String pass = scanner.nextLine();

                        currentUser = userDAO.loginUser(name, pass);

                        if (currentUser != null) {
                            System.out.println("Welcome, " + currentUser.getUsername() + "!");
                        } else {
                            System.out.println("Error: Wrong credentials.");
                        }

                    } else if (choice == 0) System.exit(0);
                } else scanner.nextLine();
            }

            boolean logout = false;

            if (currentUser.getUsername().equals("masteradmin")) {
                while (!logout) {
                    System.out.println("\n--- ADMIN PANEL ---");
                    System.out.println("1. View All Users");
                    System.out.println("2. View All Courses");
                    System.out.println("3. Delete User (by ID)");
                    System.out.println("4. Delete Course (by ID)");
                    System.out.println("0. Log Out");
                    System.out.print("Admin Choice: ");

                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice) {
                            case 1:
                                for (String u : userDAO.getAllUsers()) System.out.println(u);
                                break;
                            case 2:
                                for (String s : serviceDAO.getAllServices()) System.out.println(s);
                                break;
                            case 3:
                                System.out.print("Enter User ID to DELETE: ");
                                int uId = scanner.nextInt();
                                userDAO.deleteUser(uId);
                                break;
                            case 4:
                                System.out.print("Enter Course ID to DELETE: ");
                                int cId = scanner.nextInt();
                                serviceDAO.deleteAnyService(cId);
                                break;
                            case 0:
                                logout = true;
                                currentUser = null;
                                System.out.println("Admin Logged out.");
                                break;
                        }
                    } else scanner.nextLine();
                }
            } else {
                while (!logout) {
                    System.out.println("\n--- MAIN MENU ---");
                    System.out.println("1. Market: Show All Courses");
                    System.out.println("2. Market: Enroll in a Course");
                    System.out.println("3. Profile: My Enrollments");
                    System.out.println("4. Profile: Create New Course");
                    System.out.println("5. Profile: My Students");
                    System.out.println("6. Profile: Delete My Course");
                    System.out.println("7. Search Course (NEW)");
                    System.out.println("0. Log Out");
                    System.out.print("Choice: ");

                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice) {
                            case 1:
                                for (String s : serviceDAO.getAllServices()) System.out.println(s);
                                break;
                            case 2:
                                System.out.print("Enter Course ID to enroll: ");
                                int courseId = scanner.nextInt();
                                serviceDAO.bookCourse(currentUser.getId(), courseId);
                                break;
                            case 3:
                                System.out.println("--- Courses I am taking ---");
                                for(String s : serviceDAO.getMyEnrollments(currentUser.getId())) {
                                    System.out.println(s);
                                }
                                System.out.println("To cancel, enter Course ID (or 0 to go back): ");
                                int cancelId = scanner.nextInt();
                                if(cancelId != 0) serviceDAO.cancelBooking(currentUser.getId(), cancelId);
                                break;
                            case 4:
                                System.out.print("Title: ");
                                String title = scanner.nextLine();
                                System.out.print("Price: ");
                                double price = scanner.nextDouble();
                                scanner.nextLine();
                                System.out.print("Subject: ");
                                String subject = scanner.nextLine();
                                serviceDAO.addService(new TutoringService(title, price, subject, currentUser.getId()));
                                break;
                            case 5:
                                System.out.println("--- People enrolled in MY courses ---");
                                for(String s : serviceDAO.getMyStudents(currentUser.getId())) {
                                    System.out.println(s);
                                }
                                break;
                            case 6:
                                System.out.print("Enter Your Course ID to delete: ");
                                int delId = scanner.nextInt();
                                serviceDAO.deleteService(delId, currentUser.getId());
                                break;
                            case 7:
                                System.out.print("Enter keyword (e.g. IELTS): ");
                                String keyword = scanner.nextLine();
                                List<String> results = serviceDAO.searchServices(keyword);
                                if (results.isEmpty()) {
                                    System.out.println("No courses found.");
                                } else {
                                    System.out.println("--- Search Results ---");
                                    for (String res : results) System.out.println(res);
                                }
                                break;
                            case 0:
                                logout = true;
                                currentUser = null;
                                System.out.println("Logged out.");
                                break;
                        }
                    } else scanner.nextLine();
                }
            }
        }
    }
}