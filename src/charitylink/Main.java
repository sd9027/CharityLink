package charitylink;

import charitylink.service.AuthService;
import charitylink.ui.LoginFrame;
import charitylink.util.AppConstants;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Ensure data directory exists
        new File(AppConstants.DATA_DIR).mkdirs();

        // Use system look and feel for native widgets where possible
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Launch on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();
            new LoginFrame(authService);
        });
    }
}
