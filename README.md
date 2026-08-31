# Hospital Patient Admission System - PROG6112w Practical Assignment 1

Console-based Java application for MediCare Hospital, built to a Maven
project layout so NetBeans opens and runs it with no extra setup.

## How to open in Apache NetBeans

1. NetBeans -> File -> Open Project.
2. Select the `hospital-patient-admission-system` folder (the one with
   `pom.xml` in it). NetBeans detects it as a Maven project automatically.
3. Right-click the project -> Clean and Build. NetBeans/Maven will download
   JUnit 5 the first time (needs internet access on your machine).
4. Right-click `Main.java` (under `Source Packages > hospital`) -> Run File.
   Or right-click the project -> Run.
5. To run the unit tests: right-click the project -> Test, or right-click
   `PatientManagementSystemTest.java` -> Test File. NetBeans' built-in JUnit
   runner will show all 16 tests passing (green).

## Project structure

```
src/main/java/hospital/
    PatientCategory.java      - enum: INPATIENT, OUTPATIENT, EMERGENCY
    Patient.java               - base class (encapsulation, validation)
    Inpatient.java              - extends Patient (inheritance, super(), override)
    Bed.java / Ward.java        - 4x5 bed grid (2D array), allocate/release
    PatientManagementSystem.java - CRUD, reports, array-based sorting
    Main.java                    - console menu (entry point)
    exceptions/                  - custom checked exceptions
src/test/java/hospital/
    PatientManagementSystemTest.java - 16 JUnit 5 tests
```

## Where each rubric feature lives

| Rubric Feature | Marks | Where |
|---|---|---|
| 1. Patient Management | 20 | `PatientManagementSystem` (register/search/update/delete) + `Main` menu |
| 2. Bed Management | 20 | `Ward` (4x5 `Bed[][]`), `Bed` |
| 3. Reports | 15 | `PatientManagementSystem.generatePatientReport()`, `generateBedOccupancyReport()`, `sortPatientsBySurname()/ByPatientId()` |
| 4. Object-Oriented Programming | 30 | `Patient` (encapsulation), `Inpatient` (inheritance, `super()`, overridden `displayDetails()`) |
| 5. Unit Testing | 15 | `PatientManagementSystemTest` (16 tests) |

## Learning Unit objectives covered

- Sorting Arrays -> manual bubble sort in `PatientManagementSystem` (operates on `Patient[]`, not a library sort)
- Two-dimensional arrays -> `Ward`'s `Bed[4][5]`
- Enum methods -> `PatientCategory` uses `values()`, `ordinal()`, `valueOf()`, `name()`
- Passing an array to a method / `.length` -> `bubbleSortBySurname(Patient[] array)` etc.
- Nested loops -> all of `Ward`'s bed-grid methods
- ArrayList class -> `PatientManagementSystem`'s patient list
- Inheritance / constructor chaining / method overriding -> `Inpatient extends Patient`
- Information hiding -> every field in every class is `private`
- Exception handling -> `hospital.exceptions` package + try/catch in `Main`
- Unit testing -> JUnit 5 test class

## Notes

- Verified in development with `javac`/JUnit console and with a full
  `mvn clean install` run (16/16 tests passing, jar built successfully).
  Opening the Maven project in NetBeans will fetch JUnit 5 from Maven
  Central automatically the first time you build.
