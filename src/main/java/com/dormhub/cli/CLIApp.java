package com.dormhub.cli;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.dormhub.model.Resident;
import com.dormhub.service.ResidentService;
import com.dormhub.service.Impl.ResidentServiceImpl;

public class CLIApp {
    private final Scanner scanner;
    private final ResidentService residentService;

    public CLIApp(Scanner scanner) {
        this(scanner, new ResidentServiceImpl());
    }

    public CLIApp(Scanner scanner, ResidentService residentService) {
        this.scanner = scanner;
        this.residentService = residentService;
    }

    public void run() {
        boolean running = true;

        while (running) {
            switch (choice()) {
                case 1:
                    residentMenu();
                    break;
                case 2:
                    System.out.println("Room Manager selected.");
                    break;
                case 3:
                    System.out.println("Assignment Manager selected.");
                    break;
                case 4:
                    System.out.println("Payment Manager selected.");
                    break;
                case 5:
                    System.out.println("Dorm Pass Manager selected.");
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting DormHub...");
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

        System.out.println("You entered: " + choice);
        System.out.println();
        return choice;
    }

    private void residentMenu() {
        boolean managingResidents = true;

        while (managingResidents) {
            System.out.println("==== RESIDENT MANAGER ====");
            System.out.println("1. Add Resident");
            System.out.println("2. Update Resident");
            System.out.println("3. Delete Resident");
            System.out.println("4. Search Resident by Last Name");
            System.out.println("5. List All Residents");
            System.out.println("6. Back");
            System.out.print("Choose Option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                continue;
            }

            int option = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (option) {
                    case 1:
                        addResident();
                        break;
                    case 2:
                        updateResident();
                        break;
                    case 3:
                        deleteResident();
                        break;
                    case 4:
                        searchResidentByLastName();
                        break;
                    case 5:
                        listResidents(residentService.findAllResidents());
                        break;
                    case 6:
                        managingResidents = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Validation error: " + e.getMessage());
            }
        }
    }

    private void addResident() {
        int residentId = readInt("Resident ID: ");
        String lastName = readRequired("Last name: ");
        String firstName = readRequired("First name: ");
        String contactNo = readRequired("Contact no (11 digits): ");
        int yearLevel = readInt("Year level: ");
        String program = readRequired("Program: ");
        Date moveInDate = readDateOrNull("Move in date (YYYY-MM-DD, blank to skip): ");

        residentService.addResident(residentId, lastName, firstName, contactNo, yearLevel, program, moveInDate);
        System.out.println("Resident created.");
    }

    private void updateResident() {
        int residentId = readInt("Resident ID to update: ");
        Resident existing = residentService.findById(residentId);
        if (existing == null) {
            System.out.println("Resident not found: " + residentId);
            return;
        }

        String lastName = readRequired("Last name [" + existing.getLastName() + "]: ");
        String firstName = readRequired("First name [" + existing.getFirstName() + "]: ");
        String contactNo = readRequired("Contact no [" + existing.getContactNo() + "]: ");
        int yearLevel = readInt("Year level [" + existing.getYearLevel() + "]: ");
        String program = readRequired("Program [" + existing.getProgram() + "]: ");
        Date moveInDate = readDateOrNull(
                "Move in date [" + existing.getMoveInDate() + "] (YYYY-MM-DD, blank to keep): ");

        if (moveInDate == null) {
            moveInDate = existing.getMoveInDate();
        }

        residentService.updateResident(residentId, lastName, firstName, contactNo, yearLevel, program, moveInDate);
        System.out.println("Resident updated.");
    }

    private void deleteResident() {
        int residentId = readInt("Resident ID to delete: ");
        residentService.deleteResident(residentId);
        System.out.println("Resident deleted.");
    }

    private void searchResidentByLastName() {
        String lastName = readRequired("Last name to search: ");
        List<Resident> residents = residentService.findByLastName(lastName);
        listResidents(residents);
    }

    private void listResidents(List<Resident> residents) {
        if (residents.isEmpty()) {
            System.out.println("No residents found.");
            return;
        }

        System.out.println("ID | Last Name | First Name | Contact | Year | Program | Move-in Date");
        for (Resident resident : residents) {
            System.out.printf("%d | %s | %s | %s | %d | %s | %s%n",
                    resident.getResidentId(),
                    resident.getLastName(),
                    resident.getFirstName(),
                    resident.getContactNo(),
                    resident.getYearLevel(),
                    resident.getProgram(),
                    resident.getMoveInDate());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }
            System.out.println("Please enter a valid number.");
            scanner.nextLine();
        }
    }

    private String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    private Date readDateOrNull(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                return null;
            }

            try {
                return Date.valueOf(LocalDate.parse(value));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }
}
