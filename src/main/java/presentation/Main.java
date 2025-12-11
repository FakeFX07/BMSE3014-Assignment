package presentation;

import presentation.General.Application;

/**
 * Entry point that delegates to the Application.
 */
public class Main {
    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }
}

