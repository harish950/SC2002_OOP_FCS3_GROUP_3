package controller;

import data.ApplicationDB;
import data.ProjectDB;
import data.UserDB;
import entity.Application;
import entity.FlatBooking;
import entity.Project;
import entity.User;
import entity.enums.ApplicationStatus;
import entity.enums.FlatType;
import entity.enums.MaritalStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for generating various reports for the BTO Management System.
 */
public class ReportController {
    private ApplicationDB applicationDB;
    private ProjectDB projectDB;
    private UserDB userDB;

    /**
     * Constructs a new ReportController with references to all necessary databases.
     * 
     * @param applicationDB The application database
     * @param projectDB     The project database
     * @param userDB        The user database
     */
    public ReportController(ApplicationDB applicationDB, ProjectDB projectDB, UserDB userDB) {
        this.applicationDB = applicationDB;
        this.projectDB = projectDB;
        this.userDB = userDB;
    }

    /**
     * Generates a report of flat bookings filtered by various criteria.
     * 
     * @param projectName   The name of the project (optional)
     * @param flatType      The type of flat (optional)
     * @param maritalStatus The marital status to filter by (optional)
     * @param minAge        The minimum age to filter by (optional)
     * @param maxAge        The maximum age to filter by (optional)
     * @return A list of bookings matching the criteria
     */
    public List<FlatBooking> generateBookingReport(String projectName, FlatType flatType,
            MaritalStatus maritalStatus, Integer minAge, Integer maxAge) {
        return applicationDB.generateBookingReport(projectName, flatType, maritalStatus, minAge, maxAge);
    }

    /**
     * Generates a summary report of applications for all projects.
     * 
     * @return A map of project names to application counts
     */
    public Map<String, Integer> generateApplicationSummaryReport() {
        Map<String, Integer> summary = new HashMap<>();
        List<Project> projects = projectDB.getAllProjects();

        for (Project project : projects) {
            List<Application> applications = applicationDB.getApplicationsByProject(project.getProjectName());
            summary.put(project.getProjectName(), applications.size());
        }

        return summary;
    }

    /**
     * Generates a detailed report of applications for a specific project.
     * 
     * @param projectName The name of the project
     * @return A map of application statuses to counts
     */
    public Map<ApplicationStatus, Integer> generateApplicationDetailReport(String projectName) {
        Map<ApplicationStatus, Integer> report = new HashMap<>();
        List<Application> applications = applicationDB.getApplicationsByProject(projectName);

        // Initialize counts for all statuses
        for (ApplicationStatus status : ApplicationStatus.values()) {
            report.put(status, 0);
        }

        // Count applications by status
        for (Application application : applications) {
            ApplicationStatus status = application.getStatus();
            report.put(status, report.get(status) + 1);
        }

        return report;
    }

    /**
     * Generates a report of flat type preferences by marital status.
     * 
     * @return A nested map of marital statuses to flat types to counts
     */
    public Map<MaritalStatus, Map<FlatType, Integer>> generateFlatTypePreferenceReport() {
        Map<MaritalStatus, Map<FlatType, Integer>> report = new HashMap<>();

        // Initialize report structure
        for (MaritalStatus status : MaritalStatus.values()) {
            Map<FlatType, Integer> flatTypeCounts = new HashMap<>();
            for (FlatType type : FlatType.values()) {
                flatTypeCounts.put(type, 0);
            }
            report.put(status, flatTypeCounts);
        }

        // Count applications by marital status and flat type
        List<Application> applications = new ArrayList<>();
        for (Project project : projectDB.getAllProjects()) {
            applications.addAll(applicationDB.getApplicationsByProject(project.getProjectName()));
        }

        for (Application application : applications) {
            User user = userDB.getUser(application.getApplicantNric());
            if (user != null) {
                MaritalStatus status = user.getMaritalStatus();
                FlatType type = application.getFlatType();

                Map<FlatType, Integer> flatTypeCounts = report.get(status);
                flatTypeCounts.put(type, flatTypeCounts.get(type) + 1);
            }
        }

        return report;
    }

    /**
     * Generates a report of application success rates by age group.
     * 
     * @param ageGroups An array of age boundaries for grouping
     * @return A map of age group strings to success rates
     */
    public Map<String, Double> generateAgeGroupSuccessReport(int[] ageGroups) {
        Map<String, Integer> totalByAgeGroup = new HashMap<>();
        Map<String, Integer> successfulByAgeGroup = new HashMap<>();
        Map<String, Double> successRates = new HashMap<>();

        // Initialize age group maps
        for (int i = 0; i < ageGroups.length; i++) {
            String groupLabel;
            if (i == 0) {
                groupLabel = "Below " + ageGroups[i];
            } else if (i == ageGroups.length - 1) {
                groupLabel = ageGroups[i] + " and above";
            } else {
                groupLabel = ageGroups[i - 1] + " to " + (ageGroups[i] - 1);
            }

            totalByAgeGroup.put(groupLabel, 0);
            successfulByAgeGroup.put(groupLabel, 0);
        }

        // Count applications by age group and success status
        List<Application> applications = new ArrayList<>();
        for (Project project : projectDB.getAllProjects()) {
            applications.addAll(applicationDB.getApplicationsByProject(project.getProjectName()));
        }

        for (Application application : applications) {
            User user = userDB.getUser(application.getApplicantNric());
            if (user != null) {
                String ageGroup = getAgeGroup(user.getAge(), ageGroups);

                // Increment total count for this age group
                totalByAgeGroup.put(ageGroup, totalByAgeGroup.get(ageGroup) + 1);

                // If application is successful or booked, increment successful count
                if (application.getStatus() == ApplicationStatus.SUCCESSFUL ||
                        application.getStatus() == ApplicationStatus.BOOKED) {
                    successfulByAgeGroup.put(ageGroup, successfulByAgeGroup.get(ageGroup) + 1);
                }
            }
        }

        // Calculate success rates
        for (String ageGroup : totalByAgeGroup.keySet()) {
            int total = totalByAgeGroup.get(ageGroup);
            int successful = successfulByAgeGroup.get(ageGroup);

            double rate = total > 0 ? (double) successful / total : 0.0;
            successRates.put(ageGroup, rate);
        }

        return successRates;
    }

    /**
     * Determines the age group label for a given age.
     * 
     * @param age       The age to categorize
     * @param ageGroups The boundaries of age groups
     * @return The age group label
     */
    private String getAgeGroup(int age, int[] ageGroups) {
        for (int i = 0; i < ageGroups.length; i++) {
            if (age < ageGroups[i]) {
                if (i == 0) {
                    return "Below " + ageGroups[i];
                } else {
                    return ageGroups[i - 1] + " to " + (ageGroups[i] - 1);
                }
            }
        }
        return ageGroups[ageGroups.length - 1] + " and above";
    }

    /**
     * Generates a report of remaining units by project and flat type.
     * 
     * @return A nested map of project names to flat types to remaining units
     */
    public Map<String, Map<FlatType, Integer>> generateRemainingUnitsReport() {
        Map<String, Map<FlatType, Integer>> report = new HashMap<>();
        List<Project> projects = projectDB.getAllProjects();

        for (Project project : projects) {
            Map<FlatType, Integer> flatTypeUnits = new HashMap<>(project.getFlatTypeUnits());
            report.put(project.getProjectName(), flatTypeUnits);
        }

        return report;
    }

    /**
     * Generates a report of officer performance by number of bookings processed.
     * 
     * @return A map of officer NRICs to booking counts
     */
    public Map<String, Integer> generateOfficerPerformanceReport() {
        Map<String, Integer> report = new HashMap<>();

        // Get all officers
        List<entity.HDBOfficer> officers = userDB.getAllOfficers();
        for (entity.HDBOfficer officer : officers) {
            report.put(officer.getNric(), 0);
        }

        // Count bookings by officer
        List<FlatBooking> allBookings = new ArrayList<>();
        for (Project project : projectDB.getAllProjects()) {
            allBookings.addAll(applicationDB.getBookingsByProject(project.getProjectName()));
        }

        for (FlatBooking booking : allBookings) {
            String officerNric = booking.getOfficerNric();
            if (report.containsKey(officerNric)) {
                report.put(officerNric, report.get(officerNric) + 1);
            } else {
                // Handle case where officer might have been removed
                report.put(officerNric, 1);
            }
        }

        return report;
    }

    /**
     * Generates a text report of booking details for a specific project.
     * 
     * @param projectName The name of the project
     * @return A formatted string report
     */
    public String generateBookingDetailsTextReport(String projectName) {
        Project project = projectDB.getProject(projectName);
        if (project == null) {
            return "Project not found.";
        }

        List<FlatBooking> bookings = applicationDB.getBookingsByProject(projectName);
        if (bookings.isEmpty()) {
            return "No bookings found for project: " + projectName;
        }

        StringBuilder report = new StringBuilder();
        report.append("=================================================\n");
        report.append("BOOKING DETAILS REPORT FOR PROJECT: ").append(projectName).append("\n");
        report.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        report.append("Application Period: ").append(project.getApplicationOpeningDate())
                .append(" to ").append(project.getApplicationClosingDate()).append("\n");
        report.append("=================================================\n\n");

        report.append(String.format("%-15s %-10s %-15s %-15s %-20s\n",
                "Applicant", "Age", "Marital Status", "Flat Type", "Booking Date"));
        report.append("-------------------------------------------------------------------------\n");

        for (FlatBooking booking : bookings) {
            User applicant = userDB.getUser(booking.getApplicantNric());
            if (applicant != null) {
                report.append(String.format("%-15s %-10d %-15s %-15s %-20s\n",
                        applicant.getNric(),
                        applicant.getAge(),
                        applicant.getMaritalStatus().getStatus(),
                        booking.getFlatType().getDescription(),
                        booking.getBookingDate().toLocalDate().toString()));
            }
        }

        report.append("\nTotal Bookings: ").append(bookings.size()).append("\n");
        report.append("=================================================\n");

        return report.toString();
    }

    /**
     * Generates a summary text report of applications and bookings for all
     * projects.
     * 
     * @return A formatted string report
     */
    public String generateSystemSummaryReport() {
        List<Project> projects = projectDB.getAllProjects();
        int totalApplications = 0;
        int totalBookings = 0;
        Map<FlatType, Integer> totalUnitsByType = new HashMap<>();

        for (FlatType type : FlatType.values()) {
            totalUnitsByType.put(type, 0);
        }

        StringBuilder report = new StringBuilder();
        report.append("=================================================\n");
        report.append("BTO MANAGEMENT SYSTEM SUMMARY REPORT\n");
        report.append("=================================================\n\n");

        report.append("PROJECT SUMMARY:\n");
        report.append(String.format("%-25s %-20s %-15s %-15s %-10s %-10s\n",
                "Project Name", "Neighborhood", "Applications", "Bookings", "2-Room", "3-Room"));
        report.append("-------------------------------------------------------------------------------------------\n");

        for (Project project : projects) {
            List<Application> applications = applicationDB.getApplicationsByProject(project.getProjectName());
            List<FlatBooking> bookings = applicationDB.getBookingsByProject(project.getProjectName());

            totalApplications += applications.size();
            totalBookings += bookings.size();

            // Count remaining units by flat type
            Map<FlatType, Integer> remainingUnits = project.getFlatTypeUnits();
            for (FlatType type : FlatType.values()) {
                if (remainingUnits.containsKey(type)) {
                    totalUnitsByType.put(type, totalUnitsByType.get(type) + remainingUnits.get(type));
                }
            }

            report.append(String.format("%-25s %-20s %-15d %-15d %-10d %-10d\n",
                    project.getProjectName(),
                    project.getNeighborhood(),
                    applications.size(),
                    bookings.size(),
                    remainingUnits.getOrDefault(FlatType.TWO_ROOM, 0),
                    remainingUnits.getOrDefault(FlatType.THREE_ROOM, 0)));
        }

        report.append("\nSYSTEM TOTALS:\n");
        report.append("Total Projects: ").append(projects.size()).append("\n");
        report.append("Total Applications: ").append(totalApplications).append("\n");
        report.append("Total Bookings: ").append(totalBookings).append("\n");
        report.append("Total Remaining 2-Room Units: ").append(totalUnitsByType.get(FlatType.TWO_ROOM)).append("\n");
        report.append("Total Remaining 3-Room Units: ").append(totalUnitsByType.get(FlatType.THREE_ROOM)).append("\n");
        report.append("\n=================================================\n");

        return report.toString();
    }
}