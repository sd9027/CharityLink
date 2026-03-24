package charitylink.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Donation {
    private String donationId;
    private String donorId;
    private double amount;
    private String date;
    private String cause;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Donation(String donationId, String donorId, double amount, String cause) {
        this.donationId = donationId;
        this.donorId    = donorId;
        this.amount     = amount;
        this.cause      = cause;
        this.date       = LocalDateTime.now().format(FMT);
    }

   
    public Donation(String donationId, String donorId, double amount, String cause, String date) {
        this.donationId = donationId;
        this.donorId    = donorId;
        this.amount     = amount;
        this.cause      = cause;
        this.date       = date;
    }

    public String getDonationId() { return donationId; }
    public String getDonorId()    { return donorId; }
    public double getAmount()     { return amount; }
    public String getDate()       { return date; }
    public String getCause()      { return cause; }

 
    public String toCsv() {
        return donationId + "," + donorId + "," + amount + "," + cause + "," + date;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Donor: %s | Amount: ₹%.2f | Cause: %s | Date: %s",
                donationId, donorId, amount, cause, date);
    }
}
