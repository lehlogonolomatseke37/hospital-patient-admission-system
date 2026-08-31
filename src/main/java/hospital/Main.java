package hospital;

import hospital.exceptions.BedNotAvailableException;
import hospital.exceptions.DuplicatePatientIdException;
import hospital.exceptions.InvalidBedException;
import hospital.exceptions.PatientNotFoundException;

import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the MediCare Hospital Patient Admission System.
 *
 * This class is intentionally "thin" - it only handles menu display and
 * keyboard input/output. All real logic lives in {@link PatientManagementSystem}
 * and {@link Ward}, which keeps the code testable (Feature 5) and follows
 * good separation of concerns.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final PatientManagementSystem system = new PatientManagementSystem();

    public static void main(String[] args) {
        boolean running = true;
        printWelcome();

        while (running) {
            printMenu();
            String choice = promptString("Enter your choice: ");

            try {
                switch (choice) {
                    case "1":
                        registerPatient();
                        break;
                    case "2":
                        searchPatient();
                        break;
                    case "3":
                        updatePatient();
                        break;
                    case "4":
                        deletePatient();
                        break;
                    case "5":
                        displayAllPatients();
                        break;
                    case "6":
                        allocateBed();
                        break;
                    case "7":
                        releaseBed();
                        break;
                    case "8":
                        System.out.println(system.getWard().displayWardLayout());
                        break;
                    case "9":
                        displayBeds(system.getWard().getAvailableBeds(), "AVAILABLE BEDS");
                        break;
                    case "10":
                        displayBeds(system.getWard().getOccupiedBeds(), "OCCUPIED BEDS");
                        break;
                    case "11":
                        System.out.println(system.generatePatientReport());
                        break;
                    case "12":
                        System.out.println(system.generateBedOccupancyReport());
                        break;
                    case "13":
                        displayPatientList(system.sortPatientsBySurname(), "PATIENTS SORTED BY SURNAME");
                        break;
                    case "14":
                        displayPatientList(system.sortPatientsByPatientId(), "PATIENTS SORTED BY PATIENT ID");
                        break;
                    case "0":
                        running = false;
                        System.out.println("Thank you for using the MediCare Hospital system. Goodbye!");
                        break;
                    default:
                        System.out.println(">> Invalid choice. Please select an option from the menu.");
                }
            } catch (DuplicatePatientIdException e) {
                // Checked, business-rule exception: expected, user-facing error.
                System.out.println(">> Error: " + e.getMessage());
            } catch (PatientNotFoundException e) {
                System.out.println(">> Error: " + e.getMessage());
            } catch (InvalidBedException e) {
                System.out.println(">> Error: " + e.getMessage());
            } catch (BedNotAvailableException e) {
                System.out.println(">> Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                // Thrown by Patient/Inpatient/Ward setters on invalid input.
                System.out.println(">> Invalid input: " + e.getMessage());
            } catch (Exception e) {
                // Safety net so a typo or unexpected error never crashes the console app.
                System.out.println(">> An unexpected error occurred: " + e.getMessage());
            }

            if (running) {
                System.out.println();
            }
        }
        scanner.close();
    }

    // ------------------------------------------------------------
    // Menu display
    // ------------------------------------------------------------

    private static void printWelcome() {
        System.out.println("=======================================================");
        System.out.println("   MEDICARE HOSPITAL - PATIENT ADMISSION SYSTEM");
        System.out.println("=======================================================");
    }

    private static void printMenu() {
        System.out.println("-------------------------------------------------------");
        System.out.println(" PATIENT MANAGEMENT");
        System.out.println("   1. Register a new patient");
        System.out.println("   2. Search for a patient");
        System.out.println("   3. Update patient details");
        System.out.println("   4. Delete a patient");
        System.out.println("   5. Display all registered patients");
        System.out.println(" BED MANAGEMENT");
        System.out.println("   6. Allocate a bed to an inpatient");
        System.out.println("   7. Release a bed");
        System.out.println("   8. Display ward layout");
        System.out.println("   9. Display available beds");
        System.out.println("  10. Display occupied beds");
        System.out.println(" REPORTS");
        System.out.println("  11. Patient report");
        System.out.println("  12. Bed occupancy report");
        System.out.println("  13. Display patients sorted by surname");
        System.out.println("  14. Display patients sorted by Patient ID");
        System.out.println("   0. Exit");
        System.out.println("-------------------------------------------------------");
    }

    // ------------------------------------------------------------
    // Menu actions
    // ------------------------------------------------------------

    private static void registerPatient() throws DuplicatePatientIdException {
        System.out.println("--- Register a New Patient ---");
        String id = promptString("Patient ID: ");
        String firstName = promptString("First Name: ");
        String lastName = promptString("Last Name: ");
        int age = promptInt("Age: ");
        String gender = promptString("Gender: ");
        String condition = promptString("Medical Condition: ");

        System.out.println("Patient Category - " + PatientCategory.menuOptions());
        String categoryInput = promptString("Choose category: ");
        PatientCategory category = PatientCategory.fromInput(categoryInput);

        Patient patient;
        if (category == PatientCategory.INPATIENT) {
            patient = new Inpatient(id, firstName, lastName, age, gender, condition);
        } else {
            patient = new Patient(id, firstName, lastName, age, gender, condition, category);
        }

        system.registerPatient(patient);
        System.out.println(">> Patient " + patient.getFullName() + " (" + patient.getPatientId() + ") registered successfully.");

        if (category == PatientCategory.INPATIENT) {
            String assignNow = promptString("Allocate a bed to this patient now? (Y/N): ");
            if (assignNow.equalsIgnoreCase("Y")) {
                try {
                    String bed = system.allocateNextAvailableBed(patient.getPatientId());
                    System.out.println(">> Bed " + bed + " allocated to " + patient.getFullName() + ".");
                } catch (BedNotAvailableException e) {
                    System.out.println(">> Could not allocate a bed: " + e.getMessage());
                } catch (PatientNotFoundException e) {
                    System.out.println(">> Could not allocate a bed: " + e.getMessage());
                }
            }
        }
    }

    private static void searchPatient() throws PatientNotFoundException {
        System.out.println("--- Search for a Patient ---");
        String id = promptString("Enter Patient ID: ");
        Patient patient = system.searchPatient(id);
        System.out.println(patient.displayDetails());
    }

    private static void updatePatient() throws PatientNotFoundException {
        System.out.println("--- Update Patient Details ---");
        String id = promptString("Enter Patient ID to update: ");
        Patient existing = system.searchPatient(id);
        System.out.println("Current details:");
        System.out.println(existing.displayDetails());
        System.out.println("Enter the new details below:");

        String firstName = promptString("First Name [" + existing.getFirstName() + "]: ", existing.getFirstName());
        String lastName = promptString("Last Name [" + existing.getLastName() + "]: ", existing.getLastName());
        int age = promptIntWithDefault("Age [" + existing.getAge() + "]: ", existing.getAge());
        String gender = promptString("Gender [" + existing.getGender() + "]: ", existing.getGender());
        String condition = promptString("Medical Condition [" + existing.getMedicalCondition() + "]: ", existing.getMedicalCondition());

        system.updatePatientDetails(id, firstName, lastName, age, gender, condition);
        System.out.println(">> Patient " + id.toUpperCase() + " updated successfully.");
    }

    private static void deletePatient() throws PatientNotFoundException {
        System.out.println("--- Delete a Patient ---");
        String id = promptString("Enter Patient ID to delete: ");
        Patient existing = system.searchPatient(id);
        String confirm = promptString("Are you sure you want to delete " + existing.getFullName() + "? (Y/N): ");
        if (confirm.equalsIgnoreCase("Y")) {
            system.deletePatient(id);
            System.out.println(">> Patient " + id.toUpperCase() + " deleted successfully.");
        } else {
            System.out.println(">> Deletion cancelled.");
        }
    }

    private static void displayAllPatients() {
        displayPatientList(system.getAllPatients(), "ALL REGISTERED PATIENTS");
    }

    private static void displayPatientList(List<Patient> patientList, String title) {
        System.out.println("--- " + title + " ---");
        if (patientList.isEmpty()) {
            System.out.println("No patients are currently registered.");
            return;
        }
        for (Patient p : patientList) {
            System.out.println(p.displayDetails());
            System.out.println("-------------------------------------------------------");
        }
        System.out.println("Total: " + patientList.size());
    }

    private static void allocateBed() throws PatientNotFoundException, InvalidBedException, BedNotAvailableException {
        System.out.println("--- Allocate a Bed ---");
        String id = promptString("Enter Inpatient's Patient ID: ");
        String choice = promptString("Choose a specific bed number (e.g. B05), or press Enter to auto-assign the next available bed: ");
        if (choice.isEmpty()) {
            String bed = system.allocateNextAvailableBed(id);
            System.out.println(">> Bed " + bed + " allocated successfully.");
        } else {
            system.allocateBed(id, choice);
            System.out.println(">> Bed " + choice.toUpperCase() + " allocated successfully.");
        }
    }

    private static void releaseBed() throws PatientNotFoundException, InvalidBedException, BedNotAvailableException {
        System.out.println("--- Release a Bed ---");
        String id = promptString("Enter Patient ID: ");
        system.releaseBed(id);
        System.out.println(">> Bed released successfully.");
    }

    private static void displayBeds(List<Bed> beds, String title) {
        System.out.println("--- " + title + " ---");
        if (beds.isEmpty()) {
            System.out.println("None.");
            return;
        }
        for (Bed bed : beds) {
            System.out.println("  " + bed);
        }
        System.out.println("Total: " + beds.size());
    }

    // ------------------------------------------------------------
    // Input helpers
    // ------------------------------------------------------------

    private static String promptString(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private static String promptString(String label, String defaultValue) {
        String input = promptString(label);
        return input.isEmpty() ? defaultValue : input;
    }

    private static int promptInt(String label) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(">> Please enter a whole number.");
            }
        }
    }

    private static int promptIntWithDefault(String label, int defaultValue) {
        System.out.print(label);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(">> Not a valid number, keeping previous value (" + defaultValue + ").");
            return defaultValue;
        }
    }
}
