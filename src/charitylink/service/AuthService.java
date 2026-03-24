package charitylink.service;

import charitylink.model.*;
import charitylink.util.*;

import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private List<User> users = new ArrayList<>();

    public AuthService() {
        loadUsers();
        ensureDefaultAdmin();
    }

    
    public Donor registerDonor(String name, String email, String password)
            throws Exception {
        if (name.isBlank() || email.isBlank() || password.isBlank())
            throw new Exception("All fields are required.");
        if (!email.contains("@"))
            throw new Exception("Invalid email address.");
        if (password.length() < 6)
            throw new Exception("Password must be at least 6 characters.");
        if (findByEmail(email) != null)
            throw new Exception("Email already registered.");

        String id = IDGenerator.nextDonorId();
        Donor donor = new Donor(id, name.trim(), email.trim().toLowerCase(), password);
        users.add(donor);
        FileHandler.appendLine(AppConstants.USERS_FILE, donor.toString());
        return donor;
    }

   
    public User login(String email, String password) throws Exception {
        if (email.isBlank() || password.isBlank())
            throw new Exception("Email and password cannot be empty.");
        User user = findByEmail(email.trim().toLowerCase());
        if (user == null || !user.getPassword().equals(password))
            throw new Exception("Invalid email or password.");
        return user;
    }

    private User findByEmail(String email) {
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }

    public User findById(String id) {
        for (User u : users)
            if (u.getId().equals(id)) return u;
        return null;
    }

    public List<User> getAllUsers() { return users; }

    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        for (User u : users)
            if (u instanceof Donor) donors.add((Donor) u);
        return donors;
    }

    private void loadUsers() {
        List<String> lines = FileHandler.readLines(AppConstants.USERS_FILE);
        int maxDonorId = 1000;
        for (String line : lines) {
            String[] p = line.split(",", 5);
            if (p.length < 5) continue;
            String id = p[0], name = p[1], email = p[2], pass = p[3], role = p[4];
            if ("DONOR".equals(role)) {
                users.add(new Donor(id, name, email, pass));
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n >= maxDonorId) maxDonorId = n + 1;
                } catch (NumberFormatException ignored) {}
            } else if ("ADMIN".equals(role)) {
                users.add(new Admin(id, name, email, pass, "SuperAdmin"));
            }
        }
        IDGenerator.setDonorSeed(maxDonorId);
    }

    private void ensureDefaultAdmin() {
        boolean hasAdmin = users.stream().anyMatch(u -> u instanceof Admin);
        if (!hasAdmin) {
            Admin admin = new Admin("A001", "Admin", "admin@charitylink.org", "admin123", "SuperAdmin");
            users.add(admin);
            FileHandler.appendLine(AppConstants.USERS_FILE, admin.toString());
        }
    }
}
