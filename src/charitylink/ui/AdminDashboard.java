package charitylink.ui;

import charitylink.model.*;
import charitylink.service.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final Admin admin;
    private final AuthService authService;
    private final DonationService donationService;
    private final ReportService reportService;

    // Assigned in buildStatsBar(); refreshStats() is only called after buildUI() completes.
    private JLabel totalAmtLabel;
    private JLabel totalDonLabel;
    private JLabel totalDonorsLabel;

    // Assigned in their respective tab-builders.
    private DefaultTableModel donationsModel;
    private DefaultTableModel donorsModel;

    public AdminDashboard(Admin admin, AuthService authService) {
        this.admin           = admin;
        this.authService     = authService;
        this.donationService = new DonationService(authService);
        this.reportService   = new ReportService(authService, donationService);

        setTitle("CharityLink - Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 660);
        setLocationRelativeTo(null);

        buildUI();       // assigns all labels and table models
        refreshAll();    // safe: called after buildUI()

        setVisible(true);
    }

    // ── Top-level layout ──────────────────────────────────────────
    private void buildUI() {
        JPanel root = UITheme.darkPanel();
        root.setLayout(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
        add(root);
    }

    // ── Sidebar ───────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(UITheme.BG_CARD);
        side.setPreferredSize(new Dimension(210, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(30, 18, 30, 18));

        JLabel logo = UITheme.label("CharityLink", UITheme.FONT_HEADER, UITheme.ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel role = UITheme.label("Administrator", UITheme.FONT_SMALL, UITheme.AMBER);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(logo);
        side.add(Box.createVerticalStrut(4));
        side.add(role);
        side.add(Box.createVerticalStrut(30));

        side.add(sideBtn("Overview",        () -> refreshAll()));
        side.add(Box.createVerticalStrut(8));
        side.add(sideBtn("All Donations",   () -> refreshDonationsTable()));
        side.add(Box.createVerticalStrut(8));
        side.add(sideBtn("Donors",          () -> refreshDonorsTable()));
        side.add(Box.createVerticalStrut(8));
        side.add(sideBtn("Generate Report", () -> generateReport()));
        side.add(Box.createVerticalGlue());

        JButton logout = UITheme.dangerButton("Logout");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logout.addActionListener(e -> { dispose(); new LoginFrame(authService); });
        side.add(logout);
        return side;
    }

    // ── Main content ──────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = UITheme.darkPanel();
        main.setLayout(new BorderLayout(0, 16));
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // buildStatsBar() assigns the stat labels
        main.add(buildStatsBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        // buildDonationsTab() and buildDonorsTab() assign the table models
        tabs.addTab("  All Donations  ", buildDonationsTab());
        tabs.addTab("  Donors         ", buildDonorsTab());
        main.add(tabs, BorderLayout.CENTER);
        return main;
    }

    // ── Stats bar ─────────────────────────────────────────────────
    private JPanel buildStatsBar() {
        JPanel bar = UITheme.darkPanel();
        bar.setLayout(new GridLayout(1, 3, 14, 0));

        // Create labels first, then build cards around them — no getComponent() needed
        totalAmtLabel    = UITheme.label("Rs.0.00", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        totalDonLabel    = UITheme.label("0",       UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        totalDonorsLabel = UITheme.label("0",       UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);

        bar.add(buildStatCard("Total Collected",   totalAmtLabel,    UITheme.ACCENT));
        bar.add(buildStatCard("Total Donations",   totalDonLabel,    UITheme.ACCENT2));
        bar.add(buildStatCard("Registered Donors", totalDonorsLabel, UITheme.AMBER));
        return bar;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JLabel titleLbl = UITheme.label(title, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        JPanel vp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        vp.setBackground(UITheme.BG_CARD);
        vp.add(valueLabel);
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(vp,       BorderLayout.CENTER);
        return card;
    }

    // ── Donations tab ─────────────────────────────────────────────
    private JScrollPane buildDonationsTab() {
        String[] cols = {"Donation ID", "Donor ID", "Donor Name", "Amount (Rs.)", "Cause", "Date"};
        donationsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(donationsModel);
        return styledScrollPane(table);
    }

    // ── Donors tab ────────────────────────────────────────────────
    private JScrollPane buildDonorsTab() {
        String[] cols = {"ID", "Name", "Email", "Donations", "Total Donated (Rs.)"};
        donorsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(donorsModel);
        return styledScrollPane(table);
    }

    // ── Report generation ─────────────────────────────────────────
    private void generateReport() {
        JDialog dlg = new JDialog(this, "Generating Report...", false);
        dlg.setSize(640, 520);
        dlg.setLocationRelativeTo(this);

        JTextArea area = new JTextArea("Generating report in background thread...\n");
        area.setFont(UITheme.FONT_MONO);
        area.setBackground(UITheme.BG_DARK);
        area.setForeground(UITheme.TEXT_PRIMARY);
        area.setEditable(false);
        area.setMargin(new Insets(12, 14, 12, 14));

        JPanel btnPanel = UITheme.darkPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UITheme.primaryButton("Save to File");
        saveBtn.setEnabled(false);
        btnPanel.add(saveBtn);

        dlg.setLayout(new BorderLayout());
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);

        reportService.generateReportAsync(
            report -> SwingUtilities.invokeLater(() -> {
                area.setText(report);
                saveBtn.setEnabled(true);
                dlg.setTitle("Donation Report");
                // Use an array to allow effective-final capture inside lambda
                saveBtn.addActionListener(e -> saveReportToFile(report));
            }),
            error -> SwingUtilities.invokeLater(() -> area.setText("Error: " + error))
        );
    }

    private void saveReportToFile(String report) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("CharityLink_Report.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter fw = new java.io.FileWriter(fc.getSelectedFile())) {
                fw.write(report);
                JOptionPane.showMessageDialog(this,
                    "Report saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Refresh helpers ───────────────────────────────────────────
    private void refreshAll() {
        List<Donation> all    = donationService.getAllDonations();
        List<Donor>    donors = authService.getAllDonors();
        double total = donationService.getTotalDonations();
        SwingUtilities.invokeLater(() -> {
            totalAmtLabel.setText(String.format("Rs.%.2f", total));
            totalDonLabel.setText(String.valueOf(all.size()));
            totalDonorsLabel.setText(String.valueOf(donors.size()));
        });
        refreshDonationsTable();
        refreshDonorsTable();
    }

    private void refreshDonationsTable() {
        if (donationsModel == null) return;
        donationsModel.setRowCount(0);
        for (Donation d : donationService.getAllDonations()) {
            User u = authService.findById(d.getDonorId());
            String name = (u != null) ? u.getName() : "Unknown";
            donationsModel.addRow(new Object[]{
                d.getDonationId(), d.getDonorId(), name,
                String.format("Rs.%.2f", d.getAmount()),
                d.getCause(), d.getDate()
            });
        }
    }

    private void refreshDonorsTable() {
        if (donorsModel == null) return;
        donorsModel.setRowCount(0);
        for (Donor d : authService.getAllDonors()) {
            List<Donation> ds = donationService.getDonationsForDonor(d.getId());
            donorsModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getEmail(),
                ds.size(),
                String.format("Rs.%.2f", d.getTotalDonated())
            });
        }
    }

    // ── UI helpers ────────────────────────────────────────────────
    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(UITheme.BORDER);
        table.setSelectionBackground(UITheme.ACCENT_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.BG_INPUT);
        header.setForeground(UITheme.TEXT_SECONDARY);
        header.setFont(UITheme.FONT_SMALL);
        return table;
    }

    private JScrollPane styledScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        return sp;
    }

    private JButton sideBtn(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setBackground(UITheme.BG_DARK);
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setFont(UITheme.FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(e -> action.run());
        return btn;
    }
}
