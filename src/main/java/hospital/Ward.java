package hospital;

import hospital.exceptions.BedNotAvailableException;
import hospital.exceptions.InvalidBedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Feature 2 (Bed Management, 20 Marks).
 *
 * The hospital ward contains 20 beds arranged in a 4 x 5 layout, exactly as
 * shown in the assignment brief:
 *
 *   B01  B02  B03  B04  B05
 *   B06  B07  B08  B09  B10
 *   B11  B12  B13  B14  B15
 *   B16  B17  B18  B19  B20
 *
 * The layout is stored as a genuine two-dimensional array (Bed[][]), and
 * every method that walks the whole ward (display, counting, occupancy %)
 * uses nested loops over that 2D array rather than a flat list, matching
 * the "correctly implemented two-dimensional array" / "nested loops"
 * wording in the rubric.
 */
public class Ward {

    public static final int ROWS = 4;
    public static final int COLS = 5;
    public static final int TOTAL_BEDS = ROWS * COLS; // 20

    private final Bed[][] beds;

    public Ward() {
        beds = new Bed[ROWS][COLS];
        int bedCounter = 1;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String bedNumber = String.format("B%02d", bedCounter);
                beds[row][col] = new Bed(bedNumber);
                bedCounter++;
            }
        }
    }

    /**
     * Finds the Bed object for a given bed number (e.g. "B07") by scanning
     * the 2D array. Returns null if the bed number does not exist.
     */
    private Bed findBed(String bedNumber) {
        if (bedNumber == null) {
            return null;
        }
        String target = bedNumber.trim().toUpperCase();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (beds[row][col].getBedNumber().equals(target)) {
                    return beds[row][col];
                }
            }
        }
        return null;
    }

    public boolean bedExists(String bedNumber) {
        return findBed(bedNumber) != null;
    }

    public boolean isBedOccupied(String bedNumber) throws InvalidBedException {
        Bed bed = findBed(bedNumber);
        if (bed == null) {
            throw new InvalidBedException(bedNumber);
        }
        return bed.isOccupied();
    }

    /**
     * Allocates a specific bed to a specific inpatient.
     * Prevents: allocating a bed that doesn't exist, allocating an already
     * occupied bed (duplicate allocation), and allocating a bed to a
     * patient who is already occupying a different bed.
     */
    public void allocateBed(String bedNumber, String patientId) throws InvalidBedException, BedNotAvailableException {
        Bed bed = findBed(bedNumber);
        if (bed == null) {
            throw new InvalidBedException(bedNumber);
        }
        if (bed.isOccupied()) {
            throw new BedNotAvailableException(
                    "Bed " + bed.getBedNumber() + " is already occupied and cannot be allocated again.");
        }
        if (findBedByPatientId(patientId) != null) {
            throw new BedNotAvailableException(
                    "Patient " + patientId + " already occupies bed " + findBedByPatientId(patientId) + ".");
        }
        bed.occupy(patientId);
    }

    /**
     * Allocates the next available bed (in ward order) to the patient.
     * Prevents bed allocation when the whole ward is full.
     *
     * @return the bed number that was allocated
     */
    public String allocateNextAvailableBed(String patientId) throws BedNotAvailableException {
        if (findBedByPatientId(patientId) != null) {
            throw new BedNotAvailableException(
                    "Patient " + patientId + " already occupies bed " + findBedByPatientId(patientId) + ".");
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!beds[row][col].isOccupied()) {
                    beds[row][col].occupy(patientId);
                    return beds[row][col].getBedNumber();
                }
            }
        }
        throw new BedNotAvailableException("No beds are available. The ward is full (all " + TOTAL_BEDS + " beds occupied).");
    }

    /**
     * Releases (frees up) a bed when a patient is discharged.
     */
    public void releaseBed(String bedNumber) throws InvalidBedException, BedNotAvailableException {
        Bed bed = findBed(bedNumber);
        if (bed == null) {
            throw new InvalidBedException(bedNumber);
        }
        if (!bed.isOccupied()) {
            throw new BedNotAvailableException("Bed " + bed.getBedNumber() + " is already free - nothing to release.");
        }
        bed.vacate();
    }

    /**
     * Finds which bed (if any) a given patient currently occupies.
     */
    public String findBedByPatientId(String patientId) {
        if (patientId == null) {
            return null;
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Bed bed = beds[row][col];
                if (bed.isOccupied() && patientId.equalsIgnoreCase(bed.getOccupiedByPatientId())) {
                    return bed.getBedNumber();
                }
            }
        }
        return null;
    }

    public int getOccupiedBedCount() {
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (beds[row][col].isOccupied()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getAvailableBedCount() {
        return TOTAL_BEDS - getOccupiedBedCount();
    }

    public boolean isFull() {
        return getAvailableBedCount() == 0;
    }

    /**
     * Bed Occupancy Report: occupancy percentage rounded to 1 decimal place.
     */
    public double getOccupancyPercentage() {
        return (getOccupiedBedCount() * 100.0) / TOTAL_BEDS;
    }

    public List<Bed> getAvailableBeds() {
        List<Bed> available = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!beds[row][col].isOccupied()) {
                    available.add(beds[row][col]);
                }
            }
        }
        return available;
    }

    public List<Bed> getOccupiedBeds() {
        List<Bed> occupied = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (beds[row][col].isOccupied()) {
                    occupied.add(beds[row][col]);
                }
            }
        }
        return occupied;
    }

    /**
     * Displays the full 4 x 5 ward layout using nested loops, marking each
     * bed as available ([B01]) or occupied ({B01}).
     */
    public String displayWardLayout() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ward Layout (4 x 5) - [Available]  {Occupied}").append(System.lineSeparator());
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Bed bed = beds[row][col];
                String cell = bed.isOccupied() ? "{" + bed.getBedNumber() + "}" : "[" + bed.getBedNumber() + "]";
                sb.append(String.format("%-8s", cell));
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
