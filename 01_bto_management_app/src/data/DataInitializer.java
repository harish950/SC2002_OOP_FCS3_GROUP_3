package data;

import entity.*;
import entity.enums.MaritalStatus;
import entity.enums.UserRole;
import entity.enums.FlatType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Initializes the system with sample data.
 */
public class DataInitializer {
    private static final String USER_DATA_CSV = "usersInit.csv";
    private static final String PROJECT_DATA_CSV = "projectsInit.csv";
    private static final String USER_DATA_DAT = "users.dat";
    private static final String PROJECT_DATA_DAT = "projects.dat";

    private UserDB userDB;
    private ProjectDB projectDB;
    private ApplicationDB applicationDB;
    private EnquiryDB enquiryDB;

    /**
     * Constructs a new DataInitializer with references to all databases.
     * 
     * @param userDB        The user database
     * @param projectDB     The project database
     * @param applicationDB The application database
     * @param enquiryDB     The enquiry database
     */
    public DataInitializer(UserDB userDB, ProjectDB projectDB, ApplicationDB applicationDB, EnquiryDB enquiryDB) {
        this.userDB = userDB;
        this.projectDB = projectDB;
        this.applicationDB = applicationDB;
        this.enquiryDB = enquiryDB;
    }

    /**
     * Initializes the system with sample data.
     */
    public void initialize() {
        boolean datFilesExist = checkDatFilesExist();

        if (!datFilesExist) {
            System.out.println("No .dat files found. Loading data from CSV files...");
            loadUsersFromCSV();
            loadProjectsFromCSV();
        } else {
            System.out.println(".dat files found. CSV files will not be loaded.");
            // .dat files exist and will be loaded by their respective database classes
        }
    }

    /**
     * Checks if .dat files exist.
     * 
     * @return true if all required .dat files exist, false otherwise
     */
    private boolean checkDatFilesExist() {
        File userDat = new File(USER_DATA_DAT);
        File projectDat = new File(PROJECT_DATA_DAT);

        // Return true only if all .dat files exist
        return userDat.exists() && projectDat.exists();
    }

    /**
     * Loads user data from a CSV file.
     */
    private void loadUsersFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_DATA_CSV))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 4) {
                    String nric = data[0].trim();
                    String name = data[1].trim();
                    String password = "password"; // Default password as per requirements
                    int age = Integer.parseInt(data[2].trim());
                    MaritalStatus maritalStatus = data[3].trim().equalsIgnoreCase("Single") ? MaritalStatus.SINGLE
                            : MaritalStatus.MARRIED;
                    UserRole role = parseRole(data[4].trim());

                    User user;
                    switch (role) {
                        case APPLICANT:
                            user = new Applicant(nric, name, password, age, maritalStatus);
                            break;
                        case HDB_OFFICER:
                            user = new HDBOfficer(nric, name, password, age, maritalStatus);
                            break;
                        case HDB_MANAGER:
                            user = new HDBManager(nric, name, password, age, maritalStatus);
                            break;
                        case ADMIN:
                            user = new Admin(nric, name, password, age, maritalStatus);
                            break;
                        default:
                            continue;
                    }

                    userDB.addUser(user);
                }
            }
            System.out.println("Users loaded successfully from CSV.");
        } catch (IOException e) {
            System.out.println("Error loading users from CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing user data: " + e.getMessage());
        }
    }

    /**
     * Parses a role string into a UserRole enum value.
     * 
     * @param roleStr The role string
     * @return The UserRole enum value
     */
    private UserRole parseRole(String roleStr) {
        switch (roleStr.toLowerCase()) {
            case "applicant":
                return UserRole.APPLICANT;
            case "hdb officer":
            case "hdbofficer":
                return UserRole.HDB_OFFICER;
            case "hdb manager":
            case "hdbmanager":
                return UserRole.HDB_MANAGER;
            case "admin":
                return UserRole.ADMIN;
            default:
                return UserRole.APPLICANT; // Default to applicant
        }
    }

    /**
     * Loads project data from a CSV file.
     */
    private void loadProjectsFromCSV() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(PROJECT_DATA_CSV))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 7) {
                    String name = data[0].trim();
                    String neighborhood = data[1].trim();
                    LocalDate openingDate = LocalDate.parse(data[2].trim(), dateFormatter);
                    LocalDate closingDate = LocalDate.parse(data[3].trim(), dateFormatter);
                    String managerNric = data[4].trim();
                    int twoRoomUnits = Integer.parseInt(data[5].trim());
                    int threeRoomUnits = Integer.parseInt(data[6].trim());

                    // Check if manager exists
                    if (userDB.getUser(managerNric) != null) {
                        Project project = new Project(name, neighborhood, openingDate, closingDate, managerNric, 10);

                        Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
                        flatTypeUnits.put(FlatType.TWO_ROOM, twoRoomUnits);
                        flatTypeUnits.put(FlatType.THREE_ROOM, threeRoomUnits);

                        for (Map.Entry<FlatType, Integer> entry : flatTypeUnits.entrySet()) {
                            project.setFlatTypeUnits(entry.getKey(), entry.getValue());
                        }

                        // Set project to visible by default
                        project.setVisible(true);
                        projectDB.addProject(project);

                        // Update manager's created projects list
                        User user = userDB.getUser(managerNric);
                        if (user instanceof HDBManager) {
                            ((HDBManager) user).addCreatedProject(name);
                            userDB.updateUser(user);
                        }

                    }
                }
            }
            System.out.println("Projects loaded successfully from CSV.");
        } catch (IOException e) {
            System.out.println("Error loading projects from CSV: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error parsing project data: " + e.getMessage());
        }
    }
}