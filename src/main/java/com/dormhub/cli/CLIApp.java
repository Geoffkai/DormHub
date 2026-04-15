package com.dormhub.cli;

import java.util.Scanner;

public class CLIApp {
    private Scanner scanner;

    public CLIApp(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;

        while (running) {
            switch (choice()) {
                case 1:
                    System.out.println("");
                    break;
                case 2:
                    System.out.println("");
                    break;
                case 3:
                    System.out.println("");
                    break;
                case 4:
                    System.out.println("");
                    break;
                case 5:
                    System.out.println("");
                    break;
                case 6:
                    running = false;
                    System.out.println("");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private int choice() {
        System.out.println("\n==== DORMHUB MENU ====");
        System.out.println("1. Resident Manager");
        System.out.println("2. Room Manager");
        System.out.println("3. Assignment Manager");
        System.out.println("4. Payment Manager");
        System.out.println("5. Dorm Pass Manager");
        System.out.println("6. Exit");
        System.out.print("Choose Option: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
            return -1;
        }

        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.println();
        return choice;
    }
}
