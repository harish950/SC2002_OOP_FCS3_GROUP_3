package boundary;

import controller.ProjectController;
import entity.Project;
import entity.enums.FlatType;

import java.util.Scanner;

/**
 * User interface for project-related functionality.
 */
public class ProjectUI {
    // private Scanner scanner;
    private ProjectController projectController;

    /**
     * Constructs a new ProjectUI with references to necessary components.
     * 
     * @param scanner           The scanner for user input
     * @param projectController The project controller
     */
    public ProjectUI(Scanner scanner, ProjectController projectController) {
        // this.scanner = scanner;
        this.projectController = projectController;
    }

    /**
     * Displays detailed information about a specific project.
     * 
     * @param projectName The name of the project to view
     * @param isManager   Whether the current user is a manager
     */
    public void viewProjectDetails(String projectName, boolean isManager) {
        Project project = projectController.getProject(projectName);

        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        System.out.println("\n=== PROJECT DETAILS ===");
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Period: " + project.getApplicationOpeningDate() +
                " to " + project.getApplicationClosingDate());
        System.out.println("Manager In Charge: " + project.getManagerInChargeNric());
        System.out.println("Available Officer Slots: " + project.getAvailableOfficerSlots());
        System.out.println("Visibility: " + (project.isVisible() ? "ON" : "OFF"));

        System.out.println("\nFlat Types and Units:");
        for (FlatType flatType : project.getFlatTypeUnits().keySet()) {
            System.out.println(flatType.getDescription() + ": " +
                    project.getFlatTypeUnits().get(flatType) + " units");
        }

        System.out.println("\nAssigned Officers: " + project.getOfficerNrics().size());
        if (isManager && !project.getOfficerNrics().isEmpty()) {
            System.out.println("Officer NRICs:");
            for (String officerNric : project.getOfficerNrics()) {
                System.out.println("- " + officerNric);
            }
        }
    }
}