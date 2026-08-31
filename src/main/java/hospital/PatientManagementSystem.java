package hospital;

import hospital.exceptions.BedNotAvailableException;
import hospital.exceptions.DuplicatePatientIdException;
import hospital.exceptions.InvalidBedException;
import hospital.exceptions.PatientNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Core business logic for the MediCare Hospital Patient Admission System.
 * Combines:
 *   - Feature 1: Patient Management (register / search / update / delete)
 *   - Feature 2: Bed Management (delegated to {@link Ward})
 *   - Feature 3: Reports (patient report, bed occupancy report, sorting)
 *
 * Kept deliberately free of any Scanner/System.out console code so it can
 * be unit tested directly (Feature 5) without needing keyboard input.
 */
public class PatientManagementSystem {

    private final List<Patient> patients = new ArrayList<>();
    private final Ward ward = new Ward();

    public Ward getWard() {
        return ward;
    }

    // ------------------------------------------------------------
    // Feature 1: Patient Management
    // ------------------------------------------------------------

    /**
     * Registers a new patient. Prevents duplicate Patient IDs.
     */
    public void registerPatient(Patient patient) throws DuplicatePatientIdException {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        }
        if (isPatientIdTaken(patient.getPatientId())) {
            throw new DuplicatePatientIdException(patient.getPatientId());
        }
        patients.add(patient);
    }

    public boolean isPatientIdTaken(String patientId) {
        return findPatientIndex(patientId) != -1;
    }

    private int findPatientIndex(String patientId) {
        if (patientId == null) {
            return -1;
        }
        String target = patientId.trim().toUpperCase();
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientId().equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for and returns a patient by Patient ID.
     */
    public Patient searchPatient(String patientId) throws PatientNotFoundException {
        int index = findPatientIndex(patientId);
        if (index == -1) {
            throw new PatientNotFoundException(patientId);
        }
        return patients.get(index);
    }

    /**
     * Updates an existing patient's editable details (name, age, gender,
     * medical condition). Patient ID and category are not changed here,
     * since changing category would affect bed assignment rules.
     */
    public void updatePatientDetails(String patientId, String firstName, String lastName,
                                      int age, String gender, String medicalCondition)
            throws PatientNotFoundException {
        Patient patient = searchPatient(patientId);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setMedicalCondition(medicalCondition);
    }

    /**
     * Deletes a patient from the system. If the patient is an Inpatient
     * currently occupying a bed, that bed is automatically released first
     * so the ward never ends up with a bed "occupied" by a patient who no
     * longer exists.
     */
    public void deletePatient(String patientId) throws PatientNotFoundException {
        Patient patient = searchPatient(patientId);
        String occupiedBed = ward.findBedByPatientId(patient.getPatientId());
        if (occupiedBed != null) {
            try {
                ward.releaseBed(occupiedBed);
            } catch (InvalidBedException e) {
                // Should not happen - we just looked the bed up - but never leave
                // the system in an inconsistent state silently.
                throw new IllegalStateException("Failed to release bed while deleting patient.", e);
            } catch (BedNotAvailableException e) {
                throw new IllegalStateException("Failed to release bed while deleting patient.", e);
            }
        }
        patients.remove(findPatientIndex(patientId));
    }

    /**
     * All registered patients, in registration order.
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public int getPatientCount() {
        return patients.size();
    }

    // ------------------------------------------------------------
    // Feature 2: Bed Management (delegates to Ward)
    // ------------------------------------------------------------

    /**
     * Allocates a specific bed to an Inpatient. Only Inpatients may occupy
     * a bed (Outpatients/Emergency patients do not require one).
     */
    public void allocateBed(String patientId, String bedNumber)
            throws PatientNotFoundException, InvalidBedException, BedNotAvailableException {
        Patient patient = searchPatient(patientId);
        if (!(patient instanceof Inpatient)) {
            throw new BedNotAvailableException(
                    "Only Inpatients require a hospital bed. " + patient.getFullName() +
                            " is registered as " + patient.getCategory() + ".");
        }
        Inpatient inpatient = (Inpatient) patient;
        ward.allocateBed(bedNumber, inpatient.getPatientId());
        inpatient.setBedNumber(bedNumber);
    }

    /**
     * Allocates the next available bed automatically.
     * @return the bed number allocated
     */
    public String allocateNextAvailableBed(String patientId)
            throws PatientNotFoundException, BedNotAvailableException {
        Patient patient = searchPatient(patientId);
        if (!(patient instanceof Inpatient)) {
            throw new BedNotAvailableException(
                    "Only Inpatients require a hospital bed. " + patient.getFullName() +
                            " is registered as " + patient.getCategory() + ".");
        }
        Inpatient inpatient = (Inpatient) patient;
        String bedNumber = ward.allocateNextAvailableBed(inpatient.getPatientId());
        inpatient.setBedNumber(bedNumber);
        return bedNumber;
    }

    /**
     * Releases the bed occupied by a patient (patient is discharged from the bed).
     */
    public void releaseBed(String patientId)
            throws PatientNotFoundException, InvalidBedException, BedNotAvailableException {
        Patient patient = searchPatient(patientId);
        String bedNumber = ward.findBedByPatientId(patient.getPatientId());
        if (bedNumber == null) {
            throw new BedNotAvailableException(patient.getFullName() + " does not currently occupy a bed.");
        }
        ward.releaseBed(bedNumber);
        if (patient instanceof Inpatient) {
            ((Inpatient) patient).setBedNumber(null);
        }
    }

    // ------------------------------------------------------------
    // Feature 3: Reports & Sorting
    // ------------------------------------------------------------

    /**
     * Sorting and Data Processing (LU: "Sorting Arrays" / "Passing an array
     * to a method and using the length field").
     *
     * Patients are copied out of the ArrayList into a plain Patient[] array
     * and sorted using a manually written bubble sort, rather than relying
     * on a library sort method - this is the array-sorting technique the
     * module explicitly teaches, so it is demonstrated directly instead of
     * being hidden behind List.sort().
     */
    public List<Patient> sortPatientsBySurname() {
        Patient[] array = patients.toArray(new Patient[0]);
        bubbleSortBySurname(array);
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * Sorting and Data Processing: returns patients sorted by Patient ID,
     * again using a manual array bubble sort.
     */
    public List<Patient> sortPatientsByPatientId() {
        Patient[] array = patients.toArray(new Patient[0]);
        bubbleSortByPatientId(array);
        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * Bubble sort on an array of patients by surname (then first name as a
     * tie-breaker), both case-insensitive. Demonstrates: passing an array
     * to a method, using array.length, and manual element swapping.
     */
    private static void bubbleSortBySurname(Patient[] array) {
        for (int pass = 0; pass < array.length - 1; pass++) {
            for (int i = 0; i < array.length - 1 - pass; i++) {
                int comparison = array[i].getLastName().compareToIgnoreCase(array[i + 1].getLastName());
                if (comparison == 0) {
                    comparison = array[i].getFirstName().compareToIgnoreCase(array[i + 1].getFirstName());
                }
                if (comparison > 0) {
                    swap(array, i, i + 1);
                }
            }
        }
    }

    /**
     * Bubble sort on an array of patients by Patient ID (case-insensitive).
     */
    private static void bubbleSortByPatientId(Patient[] array) {
        for (int pass = 0; pass < array.length - 1; pass++) {
            for (int i = 0; i < array.length - 1 - pass; i++) {
                if (array[i].getPatientId().compareToIgnoreCase(array[i + 1].getPatientId()) > 0) {
                    swap(array, i, i + 1);
                }
            }
        }
    }

    private static void swap(Patient[] array, int i, int j) {
        Patient temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Patient Report: a complete, well-formatted report of every
     * registered patient (sorted by Patient ID for a predictable order).
     */
    public String generatePatientReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================").append(System.lineSeparator());
        sb.append("                     PATIENT REPORT                     ").append(System.lineSeparator());
        sb.append("=======================================================").append(System.lineSeparator());
        List<Patient> sorted = sortPatientsByPatientId();
        if (sorted.isEmpty()) {
            sb.append("No patients are currently registered.").append(System.lineSeparator());
        } else {
            for (Patient p : sorted) {
                sb.append(p.displayDetails()).append(System.lineSeparator());
                sb.append("-------------------------------------------------------").append(System.lineSeparator());
            }
        }
        sb.append("Total registered patients: ").append(patients.size()).append(System.lineSeparator());
        sb.append("=======================================================");
        return sb.toString();
    }

    /**
     * Bed Occupancy Report: ward layout, available beds, occupied beds and
     * the ward occupancy percentage.
     */
    public String generateBedOccupancyReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================================").append(System.lineSeparator());
        sb.append("                 BED OCCUPANCY REPORT                   ").append(System.lineSeparator());
        sb.append("=======================================================").append(System.lineSeparator());
        sb.append(ward.displayWardLayout());
        sb.append("-------------------------------------------------------").append(System.lineSeparator());
        sb.append("Total beds        : ").append(Ward.TOTAL_BEDS).append(System.lineSeparator());
        sb.append("Occupied beds     : ").append(ward.getOccupiedBedCount()).append(System.lineSeparator());
        sb.append("Available beds    : ").append(ward.getAvailableBedCount()).append(System.lineSeparator());
        sb.append(String.format("Ward occupancy    : %.1f%%%n", ward.getOccupancyPercentage()));
        sb.append("=======================================================");
        return sb.toString();
    }
}
