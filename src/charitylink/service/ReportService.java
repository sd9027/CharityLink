package charitylink.service;

import charitylink.model.*;
import charitylink.util.AppConstants;
import java.util.List;
import java.util.function.Consumer;

public class ReportService {

    private DonationService donationService;
    private AuthService authService;

    public ReportService(AuthService authService, DonationService donationService) {
        this.authService     = authService;
        this.donationService = donationService;
    }

   
    public void generateReportAsync(Consumer<String> onComplete, Consumer<String> onError) {
        Thread reportThread = new Thread(() -> {
            try {
                Thread.sleep(600); 
                String report = buildReport();
                onComplete.accept(report);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                onError.accept("Report generation was interrupted.");
            } catch (Exception e) {
                onError.accept("Error generating report: " + e.getMessage());
            }
        });
        reportThread.setDaemon(true);
        reportThread.setName("ReportThread");
        reportThread.start();
    }

    public String buildReport() {
        StringBuilder sb = new StringBuilder();
        List<Donation> all = donationService.getAllDonations();
        List<Donor>  donors = authService.getAllDonors();

        sb.append("╔══════════════════════════════════════════════════╗\n");
        sb.append("║           CHARITYLINK — DONATION REPORT          ║\n");
        sb.append("╚══════════════════════════════════════════════════╝\n\n");

        sb.append(String.format("  Total donations recorded : %d\n", all.size()));
        sb.append(String.format("  Total amount collected   : ₹%.2f\n", donationService.getTotalDonations()));
        sb.append(String.format("  Registered donors        : %d\n\n", donors.size()));

        sb.append("──────────────────── BY CAUSE ───────────────────\n");
        for (String cause : AppConstants.CAUSES) {
            double t = donationService.getTotalForCause(cause);
            sb.append(String.format("  %-18s : ₹%.2f\n", cause, t));
        }

        sb.append("\n─────────────────── BY DONOR ────────────────────\n");
        for (Donor d : donors) {
            List<Donation> ds = donationService.getDonationsForDonor(d.getId());
            sb.append(String.format("\n  %s (%s) — %d donation(s), Total: ₹%.2f\n",
                    d.getName(), d.getId(), ds.size(), d.getTotalDonated()));
            for (Donation don : ds) {
                sb.append(String.format("    • ₹%.2f | %s | %s\n",
                        don.getAmount(), don.getCause(), don.getDate()));
            }
        }

        sb.append("\n══════════════════════════════════════════════════\n");
        return sb.toString();
    }
}
