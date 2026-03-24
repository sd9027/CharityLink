package charitylink.model;

import java.util.ArrayList;
import java.util.List;

public class Donor extends User {
    private List<Donation> donations;

    public Donor(String id, String name, String email, String password) {
        super(id, name, email, password);
        this.donations = new ArrayList<>();
    }

    @Override
    public String getRole() { return "DONOR"; }

    public void addDonation(Donation d)       { donations.add(d); }
    public List<Donation> getDonations()      { return donations; }

    public double getTotalDonated() {
        double total = 0;
        for (Donation d : donations) total += d.getAmount();
        return total;
    }
}
