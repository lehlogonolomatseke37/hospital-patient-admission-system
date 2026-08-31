package hospital.exceptions;

/**
 * Thrown when a bed number supplied by the user does not exist in the
 * 4 x 5 ward layout (i.e. outside B01-B20).
 */
public class InvalidBedException extends Exception {
    public InvalidBedException(String bedNumber) {
        super("'" + bedNumber + "' is not a valid bed number. Valid beds are B01 to B20.");
    }
}
