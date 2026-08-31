package hospital;

/**
 * Feature 4 (OOP): Enum representing the three categories of patient that
 * MediCare Hospital treats. Using an enum instead of a plain String stops
 * invalid categories ("Inpatiant", "outpatient ", etc.) from ever being
 * stored, and lets us use a clean switch statement wherever the category
 * matters (e.g. deciding whether a bed is required).
 */
public enum PatientCategory {
    INPATIENT,
    OUTPATIENT,
    EMERGENCY;

    /**
     * Only Inpatients occupy a hospital bed (see assignment assumptions).
     */
    public boolean requiresBed() {
        return this == INPATIENT;
    }

    /**
     * Friendly display name (INPATIENT -> "Inpatient") instead of shouting
     * capitals in the console reports.
     */
    @Override
    public String toString() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    /**
     * Builds the "1) Inpatient  2) Outpatient  3) Emergency" menu line
     * shown to the user, generated from the enum itself using the built-in
     * values() and ordinal() methods rather than being hard-coded as a
     * String, so adding a fourth category later needs no menu-text change.
     */
    public static String menuOptions() {
        StringBuilder sb = new StringBuilder();
        for (PatientCategory category : values()) { // enum method: values()
            sb.append(category.ordinal() + 1).append(") ").append(category).append("   "); // enum method: ordinal()
        }
        return sb.toString().trim();
    }

    /**
     * Parses user console input ("1", "inpatient", "IN-PATIENT" ...) into a
     * PatientCategory. Throws IllegalArgumentException on invalid input so
     * the caller can catch it and re-prompt the user.
     *
     * Numeric input (1/2/3) is matched positionally against values() (the
     * built-in enum method that returns every constant in declaration
     * order). Text input is matched using the built-in valueOf() method,
     * which is the enum-native way to turn a name back into a constant.
     */
    public static PatientCategory fromInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient category cannot be empty.");
        }
        String cleaned = input.trim();

        try {
            int choice = Integer.parseInt(cleaned);
            PatientCategory[] all = values(); // enum method: values()
            if (choice >= 1 && choice <= all.length) {
                return all[choice - 1];
            }
            throw new IllegalArgumentException(
                    "Choose a number between 1 and " + all.length + " (see the menu options).");
        } catch (NumberFormatException notNumeric) {
            // Input wasn't a number - try matching it to a constant name instead.
            String normalised = cleaned.toUpperCase().replace("-", "").replace(" ", "");
            try {
                return PatientCategory.valueOf(normalised); // enum method: valueOf()
            } catch (IllegalArgumentException unknownName) {
                throw new IllegalArgumentException(
                        "Invalid patient category: '" + input + "'. Choose Inpatient, Outpatient or Emergency.");
            }
        }
    }
}
