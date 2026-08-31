package hospital.exceptions;

/**
 * Thrown when a bed cannot be allocated: either the specific bed requested
 * is already occupied, or the ward is completely full
 * (Feature 2: Allocate and Release Beds must prevent invalid/duplicate
 * allocations and prevent allocation when no beds are available).
 */
public class BedNotAvailableException extends Exception {
    public BedNotAvailableException(String message) {
        super(message);
    }
}
