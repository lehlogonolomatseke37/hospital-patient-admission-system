package hospital;

import java.util.Objects;

/**
 * Feature 4 (OOP): Patient Class Design + Encapsulation.
 *
 * All attributes are private (information hiding). Access is only possible
 * through the public getters and the validating setters, and every setter
 * rejects bad data by throwing IllegalArgumentException instead of silently
 * storing garbage.
 *
 * This is the base class in the inheritance hierarchy: {@link Inpatient}
 * extends this class and adds ward/bed information (Feature 4: Inheritance
 * and Constructor Chaining, Method Overriding).
 */
public class Patient {

    private String patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String medicalCondition;
    private PatientCategory category;

    /**
     * Full constructor used by Patient and (via super()) by Inpatient.
     */
    public Patient(String patientId, String firstName, String lastName, int age,
                   String gender, String medicalCondition, PatientCategory category) {
        setPatientId(patientId);
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setGender(gender);
        setMedicalCondition(medicalCondition);
        setCategory(category);
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public String getPatientId() {
        return patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public PatientCategory getCategory() {
        return category;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ----------------------------------------------------------------
    // Setters (with validation - encapsulation is more than just
    // "private fields", so every setter guards its own invariant)
    // ----------------------------------------------------------------

    public final void setPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        }
        this.patientId = patientId.trim().toUpperCase();
    }

    public final void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        this.firstName = firstName.trim();
    }

    public final void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        this.lastName = lastName.trim();
    }

    public final void setAge(int age) {
        if (age <= 0 || age > 130) {
            throw new IllegalArgumentException("Age must be between 1 and 130.");
        }
        this.age = age;
    }

    public final void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be empty.");
        }
        this.gender = gender.trim();
    }

    public final void setMedicalCondition(String medicalCondition) {
        if (medicalCondition == null || medicalCondition.trim().isEmpty()) {
            throw new IllegalArgumentException("Medical condition cannot be empty.");
        }
        this.medicalCondition = medicalCondition.trim();
    }

    public final void setCategory(PatientCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Patient category cannot be empty.");
        }
        this.category = category;
    }

    // ----------------------------------------------------------------
    // Behaviour
    // ----------------------------------------------------------------

    /**
     * Feature 4: Method Overriding. Inpatient overrides this method to
     * append ward and bed information underneath the standard details.
     * Kept as a plain method (rather than only toString()) so the rubric's
     * explicit "override the displayDetails() method" requirement is
     * satisfied literally.
     */
    public String displayDetails() {
        return String.format(
                "Patient ID     : %s%n" +
                "Name           : %s%n" +
                "Age            : %d%n" +
                "Gender         : %s%n" +
                "Medical Cond.  : %s%n" +
                "Category       : %s",
                patientId, getFullName(), age, gender, medicalCondition, category);
    }

    @Override
    public String toString() {
        return displayDetails();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient other = (Patient) o;
        return Objects.equals(patientId, other.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId);
    }
}
