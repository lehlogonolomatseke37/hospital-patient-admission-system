package hospital;

/**
 * Feature 4 (OOP): Inheritance and Constructor Chaining + Method Overriding.
 *
 * Inpatient IS-A Patient (extends Patient) and adds the two extra
 * attributes an inpatient needs that an Outpatient/Emergency patient does
 * not: Ward Number and Bed Number (see assignment "The Inpatient class
 * must: Extend the Patient class... Use super() to initialise inherited
 * attributes... Override the displayDetails() method to include the ward
 * and bed information.").
 *
 * Only one ward exists in this system (assignment assumption), so
 * wardNumber defaults to 1, but it is still stored as its own field as the
 * brief requires.
 */
public class Inpatient extends Patient {

    public static final int DEFAULT_WARD_NUMBER = 1;
    public static final String NOT_ALLOCATED = "Not allocated";

    private int wardNumber;
    private String bedNumber; // e.g. "B01", or NOT_ALLOCATED until a bed is assigned

    /**
     * Constructor chaining: this constructor calls super(...) to let the
     * Patient class initialise the attributes it owns, then this
     * constructor only has to deal with the two attributes Inpatient adds.
     */
    public Inpatient(String patientId, String firstName, String lastName, int age,
                      String gender, String medicalCondition) {
        super(patientId, firstName, lastName, age, gender, medicalCondition, PatientCategory.INPATIENT);
        this.wardNumber = DEFAULT_WARD_NUMBER;
        this.bedNumber = NOT_ALLOCATED;
    }

    public int getWardNumber() {
        return wardNumber;
    }

    public void setWardNumber(int wardNumber) {
        if (wardNumber <= 0) {
            throw new IllegalArgumentException("Ward number must be a positive number.");
        }
        this.wardNumber = wardNumber;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    /**
     * Package-visible on purpose: only the Ward class (Feature 2: Bed
     * Management) should be updating which bed an inpatient occupies, via
     * allocateBed()/releaseBed(), rather than arbitrary calling code.
     */
    void setBedNumber(String bedNumber) {
        this.bedNumber = (bedNumber == null || bedNumber.trim().isEmpty()) ? NOT_ALLOCATED : bedNumber.trim();
    }

    public boolean hasBed() {
        return !NOT_ALLOCATED.equals(bedNumber);
    }

    /**
     * Method Overriding: extends (does not replace) the superclass
     * behaviour - it calls super.displayDetails() first and then appends
     * the additional inpatient-only information, as the rubric's
     * "Exceeds the Required Standard" column requires.
     */
    @Override
    public String displayDetails() {
        return super.displayDetails() + String.format(
                "%nWard Number    : %d%n" +
                "Bed Number     : %s",
                wardNumber, bedNumber);
    }
}
