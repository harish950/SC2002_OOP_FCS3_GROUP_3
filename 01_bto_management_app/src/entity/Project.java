package entity;

import entity.enums.FlatType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a BTO project in the system.
 */
public class Project implements Serializable {
    private String projectName;
    private String neighborhood;
    private Map<FlatType, Integer> flatTypeUnits;
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;
    private String managerInChargeNric;
    private int availableOfficerSlots;
    private boolean isVisible;
    private List<String> officerNrics;

    /**
     * Creates a new BTO project with the specified details.
     * 
     * @param projectName            The name of the project
     * @param neighborhood           The neighborhood of the project
     * @param flatTypeUnits          Map containing number of units for each flat
     *                               type
     * @param applicationOpeningDate The opening date for applications
     * @param applicationClosingDate The closing date for applications
     * @param managerInChargeNric    The NRIC of the manager in charge
     * @param availableOfficerSlots  The number of available HDB officer slots
     */
    public Project(String projectName, String neighborhood, Map<FlatType, Integer> flatTypeUnits,
            LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
            String managerInChargeNric, int availableOfficerSlots) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypeUnits = flatTypeUnits;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.managerInChargeNric = managerInChargeNric;
        this.availableOfficerSlots = availableOfficerSlots;
        this.isVisible = false; // Default is not visible
        this.officerNrics = new ArrayList<>();
    }

    /**
     * Constructs a Project with default empty values for flatTypeUnits and
     * officerNrics.
     */
    public Project(String projectName, String neighborhood,
            LocalDate applicationOpeningDate, LocalDate applicationClosingDate,
            String managerInChargeNric, int availableOfficerSlots) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypeUnits = new HashMap<>();
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.managerInChargeNric = managerInChargeNric;
        this.availableOfficerSlots = availableOfficerSlots;
        this.isVisible = false;
        this.officerNrics = new ArrayList<>();
    }

    /**
     * Gets the name of this project.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the name of this project.
     * 
     * @param projectName The new project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the neighborhood of this project.
     * 
     * @return The neighborhood
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Sets the neighborhood of this project.
     * 
     * @param neighborhood The new neighborhood
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * Gets the map of flat types and their available units.
     * 
     * @return Map of flat types to number of units
     */
    public Map<FlatType, Integer> getFlatTypeUnits() {
        return flatTypeUnits;
    }

    /**
     * Sets the number of units for a specific flat type.
     * 
     * @param flatType The flat type
     * @param units    The number of units
     */
    public void setFlatTypeUnits(FlatType flatType, int units) {
        flatTypeUnits.put(flatType, units);
    }

    /**
     * Gets the application opening date.
     * 
     * @return The application opening date
     */
    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    /**
     * Sets the application opening date.
     * 
     * @param applicationOpeningDate The new application opening date
     */
    public void setApplicationOpeningDate(LocalDate applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    /**
     * Gets the application closing date.
     * 
     * @return The application closing date
     */
    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }

    /**
     * Sets the application closing date.
     * 
     * @param applicationClosingDate The new application closing date
     */
    public void setApplicationClosingDate(LocalDate applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }

    /**
     * Gets the NRIC of the manager in charge.
     * 
     * @return The manager's NRIC
     */
    public String getManagerInChargeNric() {
        return managerInChargeNric;
    }

    /**
     * Sets the NRIC of the manager in charge.
     * 
     * @param managerInChargeNric The new manager's NRIC
     */
    public void setManagerInChargeNric(String managerInChargeNric) {
        this.managerInChargeNric = managerInChargeNric;
    }

    /**
     * Gets the number of available slots for HDB officers.
     * 
     * @return The number of available slots
     */
    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }

    /**
     * Sets the number of available slots for HDB officers.
     * 
     * @param availableOfficerSlots The new number of available slots
     */
    public void setAvailableOfficerSlots(int availableOfficerSlots) {
        this.availableOfficerSlots = availableOfficerSlots;
    }

    /**
     * Decrements the available officer slots by one.
     */
    public void decrementAvailableOfficerSlots() {
        if (availableOfficerSlots > 0) {
            availableOfficerSlots--;
        }
    }

    /**
     * Increments the available officer slots by one.
     */
    public void incrementAvailableOfficerSlots() {
        if (availableOfficerSlots < 10) {
            availableOfficerSlots++;
        }
    }

    /**
     * Checks if this project is visible to applicants.
     * 
     * @return true if the project is visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Sets the visibility of this project.
     * 
     * @param visible true to make the project visible, false otherwise
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Gets the list of officer NRICs assigned to this project.
     * 
     * @return The list of officer NRICs
     */
    public List<String> getOfficerNrics() {
        return officerNrics;
    }

    /**
     * Adds an officer NRIC to this project.
     * 
     * @param officerNric The NRIC of the officer to add
     * @return true if the officer was added, false if already present
     */
    public boolean addOfficerNric(String officerNric) {
        if (!officerNrics.contains(officerNric)) {
            officerNrics.add(officerNric);
            return true;
        }
        return false;
    }

    /**
     * Removes an officer NRIC from this project.
     * 
     * @param officerNric The NRIC of the officer to remove
     * @return true if the officer was removed, false if not found
     */
    public boolean removeOfficerNric(String officerNric) {
        return officerNrics.remove(officerNric);
    }

    /**
     * Decreases the number of units for a specific flat type by one.
     * 
     * @param flatType The flat type to decrement
     * @return true if successful, false if no more units available
     */
    public boolean decrementFlatTypeUnits(FlatType flatType) {
        Integer currentUnits = flatTypeUnits.get(flatType);
        if (currentUnits != null && currentUnits > 0) {
            flatTypeUnits.put(flatType, currentUnits - 1);
            return true;
        }
        return false;
    }

    /**
     * Increases the number of units for a specific flat type by one.
     * 
     * @param flatType The flat type to increment
     */
    public void incrementFlatTypeUnits(FlatType flatType) {
        Integer currentUnits = flatTypeUnits.getOrDefault(flatType, 0);
        flatTypeUnits.put(flatType, currentUnits + 1);
    }

    /**
     * Checks if the project is currently in the application period.
     * 
     * @return true if the current date is within the application period, false
     *         otherwise
     */
    public boolean isInApplicationPeriod() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(applicationOpeningDate) && !today.isAfter(applicationClosingDate);
    }
}