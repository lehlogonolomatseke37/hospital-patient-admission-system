package hospital.exceptions;

/**
 * Thrown when an attempt is made to register a patient using a Patient ID
 * that already exists in the system (Feature 1: Register Patient must
 * prevent duplicate Patient IDs).
 */
public class DuplicatePatientIdException extends Exception {
    public DuplicatePatientIdException(String patientId) {
        super("A patient with ID '" + patientId + "' is already registered.");
    }
}
