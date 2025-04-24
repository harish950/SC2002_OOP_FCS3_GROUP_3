package main;

import boundary.MainUI;

/**
 * Main class to start the BTO Management System.
 */
public class Main {
    /**
     * Main method to start the application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting BTO Management System...");

        MainUI mainUI = new MainUI();
        mainUI.start();
    }
}