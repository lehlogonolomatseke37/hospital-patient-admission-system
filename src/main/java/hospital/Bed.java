package hospital;

/**
 * Feature 2 (Bed Management): a single hospital bed.
 * Encapsulated - occupied/patientId can only change through occupy()/vacate().
 */
public class Bed {

    private final String bedNumber; // e.g. "B01"
    private boolean occupied;
    private String occupiedByPatientId;

    public Bed(String bedNumber) {
        this.bedNumber = bedNumber;
        this.occupied = false;
        this.occupiedByPatientId = null;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public String getOccupiedByPatientId() {
        return occupiedByPatientId;
    }

    void occupy(String patientId) {
        this.occupied = true;
        this.occupiedByPatientId = patientId;
    }

    void vacate() {
        this.occupied = false;
        this.occupiedByPatientId = null;
    }

    @Override
    public String toString() {
        return occupied ? bedNumber + "(Occupied - " + occupiedByPatientId + ")" : bedNumber + "(Available)";
    }
}
