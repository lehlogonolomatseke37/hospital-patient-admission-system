package hospital.exceptions;

/**
 * Thrown when a search, update or delete is attempted for a Patient ID
 * that does not exist in the system.
 */
public class PatientNotFoundException extends Exception {
    public PatientNotFoundException(String patientId) {
        super("No patient found with ID '" + patientId + "'.");
    }
}
