package hospital;

import hospital.exceptions.BedNotAvailableException;
import hospital.exceptions.DuplicatePatientIdException;
import hospital.exceptions.InvalidBedException;
import hospital.exceptions.PatientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature 5: Unit Testing (15 Marks).
 *
 * Covers every item explicitly listed in the assignment brief:
 *   Register a patient / Search for a patient / Update patient details /
 *   Delete a patient / Allocate a bed / Release a bed /
 *   Prevent duplicate Patient IDs / Prevent allocating an occupied bed /
 *   Prevent bed allocation when all beds are occupied /
 *   Sort patients by surname or Patient ID.
 *
 * Grouped under three headings matching the rubric's three sub-criteria:
 * CRUD Operation Tests, Bed Management Tests, Validation and Boundary Tests.
 */
class PatientManagementSystemTest {

    private PatientManagementSystem system;

    @BeforeEach
    void setUp() {
        system = new PatientManagementSystem();
    }

    // ==============================================================
    // CRUD Operation Tests
    // ==============================================================

    @Test
    @DisplayName("Register a patient")
    void testRegisterPatient() throws Exception {
        Patient patient = new Patient("P001", "Thabo", "Mokoena", 34, "Male", "Fractured arm", PatientCategory.OUTPATIENT);

        system.registerPatient(patient);

        assertEquals(1, system.getPatientCount());
        assertTrue(system.isPatientIdTaken("P001"));
        assertEquals("P001", system.searchPatient("P001").getPatientId());
    }

    @Test
    @DisplayName("Search for a patient")
    void testSearchPatient() throws Exception {
        Patient patient = new Patient("P002", "Amahle", "Dlamini", 28, "Female", "Migraine", PatientCategory.OUTPATIENT);
        system.registerPatient(patient);

        Patient found = system.searchPatient("p002"); // case-insensitive lookup

        assertNotNull(found);
        assertEquals("Amahle", found.getFirstName());
        assertEquals("Dlamini", found.getLastName());
    }

    @Test
    @DisplayName("Searching for a Patient ID that does not exist throws PatientNotFoundException")
    void testSearchPatientNotFound() {
        assertThrows(PatientNotFoundException.class, () -> system.searchPatient("DOES-NOT-EXIST"));
    }

    @Test
    @DisplayName("Update patient details")
    void testUpdatePatientDetails() throws Exception {
        Patient patient = new Patient("P003", "Sipho", "Nkosi", 40, "Male", "Flu", PatientCategory.OUTPATIENT);
        system.registerPatient(patient);

        system.updatePatientDetails("P003", "Sipho", "Nkosi", 41, "Male", "Recovered - Flu");

        Patient updated = system.searchPatient("P003");
        assertEquals(41, updated.getAge());
        assertEquals("Recovered - Flu", updated.getMedicalCondition());
    }

    @Test
    @DisplayName("Delete a patient")
    void testDeletePatient() throws Exception {
        Patient patient = new Patient("P004", "Zanele", "Khumalo", 55, "Female", "High blood pressure", PatientCategory.OUTPATIENT);
        system.registerPatient(patient);
        assertEquals(1, system.getPatientCount());

        system.deletePatient("P004");

        assertEquals(0, system.getPatientCount());
        assertThrows(PatientNotFoundException.class, () -> system.searchPatient("P004"));
    }

    @Test
    @DisplayName("Deleting an Inpatient automatically frees their bed")
    void testDeletePatientReleasesBed() throws Exception {
        Inpatient inpatient = new Inpatient("P005", "Lerato", "Molefe", 60, "Female", "Pneumonia");
        system.registerPatient(inpatient);
        String bed = system.allocateNextAvailableBed("P005");
        assertTrue(system.getWard().isBedOccupied(bed));

        system.deletePatient("P005");

        assertFalse(system.getWard().isBedOccupied(bed));
    }

    // ==============================================================
    // Bed Management Tests
    // ==============================================================

    @Test
    @DisplayName("Allocate a bed to an inpatient")
    void testAllocateBed() throws Exception {
        Inpatient inpatient = new Inpatient("P010", "Kabelo", "Mahlangu", 45, "Male", "Broken leg");
        system.registerPatient(inpatient);

        system.allocateBed("P010", "B01");

        assertTrue(system.getWard().isBedOccupied("B01"));
        assertEquals("B01", ((Inpatient) system.searchPatient("P010")).getBedNumber());
        assertEquals(19, system.getWard().getAvailableBedCount());
    }

    @Test
    @DisplayName("Release a bed")
    void testReleaseBed() throws Exception {
        Inpatient inpatient = new Inpatient("P011", "Naledi", "Sithole", 38, "Female", "Appendicitis");
        system.registerPatient(inpatient);
        system.allocateBed("P011", "B02");

        system.releaseBed("P011");

        assertFalse(system.getWard().isBedOccupied("B02"));
        assertEquals(Ward.TOTAL_BEDS, system.getWard().getAvailableBedCount());
        assertFalse(((Inpatient) system.searchPatient("P011")).hasBed());
    }

    @Test
    @DisplayName("Only Inpatients may be allocated a bed")
    void testOnlyInpatientsCanBeAllocatedABed() throws Exception {
        Patient outpatient = new Patient("P012", "Bongani", "Zulu", 22, "Male", "Sprained ankle", PatientCategory.OUTPATIENT);
        system.registerPatient(outpatient);

        assertThrows(BedNotAvailableException.class, () -> system.allocateBed("P012", "B03"));
    }

    // ==============================================================
    // Validation and Boundary Tests
    // ==============================================================

    @Test
    @DisplayName("Prevent duplicate Patient IDs")
    void testPreventDuplicatePatientIds() throws Exception {
        Patient patient1 = new Patient("P020", "Given", "Ngwenya", 30, "Male", "Diabetes", PatientCategory.OUTPATIENT);
        system.registerPatient(patient1);

        Patient duplicate = new Patient("p020", "Someone", "Else", 50, "Female", "Cold", PatientCategory.OUTPATIENT);

        assertThrows(DuplicatePatientIdException.class, () -> system.registerPatient(duplicate));
        assertEquals(1, system.getPatientCount()); // duplicate was rejected, not added
    }

    @Test
    @DisplayName("Prevent allocating an occupied bed")
    void testPreventAllocatingOccupiedBed() throws Exception {
        Inpatient inpatient1 = new Inpatient("P021", "Palesa", "Mofokeng", 29, "Female", "Malaria");
        Inpatient inpatient2 = new Inpatient("P022", "Tumelo", "Radebe", 33, "Male", "Kidney stones");
        system.registerPatient(inpatient1);
        system.registerPatient(inpatient2);
        system.allocateBed("P021", "B04");

        assertThrows(BedNotAvailableException.class, () -> system.allocateBed("P022", "B04"));
    }

    @Test
    @DisplayName("Allocating a bed number that does not exist throws InvalidBedException")
    void testAllocatingInvalidBedNumber() throws Exception {
        Inpatient inpatient = new Inpatient("P023", "Refilwe", "Mahlangu", 27, "Female", "Asthma");
        system.registerPatient(inpatient);

        assertThrows(InvalidBedException.class, () -> system.allocateBed("P023", "B99"));
    }

    @Test
    @DisplayName("Prevent bed allocation when all beds are occupied")
    void testPreventBedAllocationWhenWardIsFull() throws Exception {
        // Fill all 20 beds with 20 different inpatients.
        for (int i = 1; i <= Ward.TOTAL_BEDS; i++) {
            Inpatient inpatient = new Inpatient("F" + i, "First" + i, "Last" + i, 30, "Male", "Observation");
            system.registerPatient(inpatient);
            system.allocateNextAvailableBed("F" + i);
        }
        assertTrue(system.getWard().isFull());
        assertEquals(0, system.getWard().getAvailableBedCount());
        assertEquals(100.0, system.getWard().getOccupancyPercentage());

        // The 21st inpatient cannot be given a bed - the ward is full.
        Inpatient oneTooMany = new Inpatient("F21", "Extra", "Patient", 30, "Male", "Observation");
        system.registerPatient(oneTooMany);

        assertThrows(BedNotAvailableException.class, () -> system.allocateNextAvailableBed("F21"));
    }

    @Test
    @DisplayName("Sort patients by surname")
    void testSortPatientsBySurname() throws Exception {
        system.registerPatient(new Patient("P030", "A", "Zondo", 20, "Male", "Cold", PatientCategory.OUTPATIENT));
        system.registerPatient(new Patient("P031", "B", "Adams", 21, "Female", "Cold", PatientCategory.OUTPATIENT));
        system.registerPatient(new Patient("P032", "C", "Mokoena", 22, "Male", "Cold", PatientCategory.OUTPATIENT));

        List<Patient> sorted = system.sortPatientsBySurname();

        assertEquals("Adams", sorted.get(0).getLastName());
        assertEquals("Mokoena", sorted.get(1).getLastName());
        assertEquals("Zondo", sorted.get(2).getLastName());
    }

    @Test
    @DisplayName("Sort patients by Patient ID")
    void testSortPatientsByPatientId() throws Exception {
        system.registerPatient(new Patient("P100", "A", "One", 20, "Male", "Cold", PatientCategory.OUTPATIENT));
        system.registerPatient(new Patient("P020", "B", "Two", 21, "Female", "Cold", PatientCategory.OUTPATIENT));
        system.registerPatient(new Patient("P055", "C", "Three", 22, "Male", "Cold", PatientCategory.OUTPATIENT));

        List<Patient> sorted = system.sortPatientsByPatientId();

        assertEquals("P020", sorted.get(0).getPatientId());
        assertEquals("P055", sorted.get(1).getPatientId());
        assertEquals("P100", sorted.get(2).getPatientId());
    }

    @Test
    @DisplayName("Patient setters reject invalid data (boundary validation)")
    void testPatientValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                new Patient("", "First", "Last", 30, "Male", "Cold", PatientCategory.OUTPATIENT));
        assertThrows(IllegalArgumentException.class, () ->
                new Patient("P200", "First", "Last", 0, "Male", "Cold", PatientCategory.OUTPATIENT));
        assertThrows(IllegalArgumentException.class, () ->
                new Patient("P201", "First", "Last", 200, "Male", "Cold", PatientCategory.OUTPATIENT));
    }
}
