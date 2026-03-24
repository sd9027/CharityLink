package charitylink.service;

import charitylink.model.*;
import charitylink.util.*;
import java.util.ArrayList;
import java.util.List;

public class DonationService {

    private List<Donation> allDonations = new ArrayList<>();
    private AuthService authService;

    public DonationService(AuthService authService) {
        this.authService = authService;
        loadDonations();
    }

    public Donation processDonation(Donor donor, double amount, String cause)
            throws Exception {
        if (amount <= 0)
            throw new Exception("Donation amount must be greater than zero.");
        if (cause == null || cause.isBlank())
            throw new Exception("Please select a cause.");

        String id = IDGenerator.nextDonationId();
        Donation donation = new Donation(id, donor.getId(), amount, cause);
        allDonations.add(donation);
        donor.addDonation(donation);
        FileHandler.appendLine(AppConstants.DONATIONS_FILE, donation.toCsv());
        return donation;
    }

   
    public List<Donation> getDonationsForDonor(String donorId) {
        List<Donation> result = new ArrayList<>();
        for (Donation d : allDonations)
            if (d.getDonorId().equals(donorId)) result.add(d);
        return result;
    }

    public List<Donation> getAllDonations() { return allDonations; }

    public double getTotalDonations() {
        double total = 0;
        for (Donation d : allDonations) total += d.getAmount();
        return total;
    }

    public double getTotalForCause(String cause) {
        double total = 0;
        for (Donation d : allDonations)
            if (d.getCause().equalsIgnoreCase(cause)) total += d.getAmount();
        return total;
    }

    
    private void loadDonations() {
        List<String> lines = FileHandler.readLines(AppConstants.DONATIONS_FILE);
        int maxId = 5000;
        for (String line : lines) {
            String[] p = line.split(",", 5);
            if (p.length < 5) continue;
            try {
                String donId = p[0], donorId = p[1];
                double amount = Double.parseDouble(p[2]);
                String cause = p[3], date = p[4];
                Donation d = new Donation(donId, donorId, amount, cause, date);
                allDonations.add(d);
                // Link to donor object if found
                User u = authService.findById(donorId);
                if (u instanceof Donor) ((Donor) u).addDonation(d);
                try {
                    int n = Integer.parseInt(donId.replace("DON", ""));
                    if (n >= maxId) maxId = n + 1;
                } catch (NumberFormatException ignored) {}
            } catch (Exception ignored) {}
        }
        IDGenerator.setDonationSeed(maxId);
    }
}
