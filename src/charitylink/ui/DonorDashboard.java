package charitylink.ui;

import charitylink.model.*;
import charitylink.service.*;
import charitylink.util.AppConstants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DonorDashboard extends JFrame {

    private final Donor donor;
    private final AuthService authService;
    private final DonationService donationService;

    // Assigned in buildStatsBar() (called from buildMain(), called from buildUI()).
    // refreshStats() is called AFTER buildUI() completes, so these are never null when used.
    private JLabel totalLabel;
    private JLabel countLabel;
    private DefaultTableModel tableModel;

    public DonorDashboard(Donor donor, AuthService authService) {
        this.donor           = donor;
        this.authService     = authService;
        this.donationService = new DonationService(authService);

        setTitle("CharityLink - Donor Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 620);
        setLocationRelativeTo(null);

        buildUI();       // assigns totalLabel, countLabel, tableModel
        refreshStats();  // safe: called after buildUI()
        refreshTable();  // populate table with existing data

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
        side.setPreferredSize(new Dimension(200, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(30, 18, 30, 18));

        JLabel logo = UITheme.label("CharityLink", UITheme.FONT_HEADER, UITheme.ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(logo);
        side.add(Box.createVerticalStrut(6));

        String firstName = donor.getName().contains(" ")
                ? donor.getName().split(" ")[0] : donor.getName();
        JLabel welcome = UITheme.label("Hello, " + firstName + "!",
                UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(welcome);
        side.add(Box.createVerticalStrut(30));

        side.add(sideBtn("Make Donation", () -> openDonateDialog()));
        side.add(Box.createVerticalStrut(8));
        side.add(sideBtn("My History",   () -> refreshTable()));
        side.add(Box.createVerticalStrut(8));
        side.add(sideBtn("My Report",    () -> showMyReport()));
        side.add(Box.createVerticalGlue());

        JButton logout = UITheme.dangerButton("Logout");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logout.addActionListener(e -> { dispose(); new LoginFrame(authService); });
        side.add(logout);
        return side;
    }

    // ── Main area ─────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = UITheme.darkPanel();
        main.setLayout(new BorderLayout(0, 14));
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Order matters: buildStatsBar assigns labels, buildTable assigns tableModel
        main.add(buildStatsBar(),  BorderLayout.NORTH);
        main.add(buildTable(),     BorderLayout.CENTER);
        main.add(buildDonateBar(), BorderLayout.SOUTH);
        return main;
    }

    // ── Stats bar ─────────────────────────────────────────────────
    private JPanel buildStatsBar() {
        JPanel bar = UITheme.darkPanel();
        bar.setLayout(new GridLayout(1, 2, 16, 0));

        // Create labels first, pass them into the card builder — no getComponent() needed
        totalLabel = UITheme.label("Rs.0.00", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        countLabel = UITheme.label("0",       UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);

        bar.add(buildStatCard("Total Donated",  totalLabel, UITheme.ACCENT));
        bar.add(buildStatCard("Donations Made", countLabel, UITheme.ACCENT2));
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

    // ── History table ─────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {"Donation ID", "Amount (Rs.)", "Cause", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        return sp;
    }

    // ── Donate button bar ─────────────────────────────────────────
    private JPanel buildDonateBar() {
        JPanel bar = UITheme.darkPanel();
        bar.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton btn = UITheme.primaryButton("+ Make a Donation");
        btn.addActionListener(e -> openDonateDialog());
        bar.add(btn);
        return bar;
    }

    // ── Donate dialog ─────────────────────────────────────────────
    private void openDonateDialog() {
        JDialog dlg = new JDialog(this, "Make a Donation", true);
        dlg.setSize(380, 260);
        dlg.setLocationRelativeTo(this);

        JPanel p = UITheme.cardPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(7, 0, 7, 0);
        c.gridx = 0;
        c.weightx = 1;

        JTextField amtField = UITheme.styledField(15);
        JComboBox<String> causeBox = new JComboBox<>(AppConstants.CAUSES);
        causeBox.setBackground(UITheme.BG_INPUT);
        causeBox.setForeground(UITheme.TEXT_PRIMARY);
        causeBox.setFont(UITheme.FONT_BODY);

        c.gridy = 0; p.add(fieldRow("Amount (Rs.)", amtField), c);
        c.gridy = 1; p.add(fieldRow("Cause", causeBox), c);

        JButton confirm = UITheme.primaryButton("Donate Now");
        confirm.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amtField.getText().trim());
                String cause  = (String) causeBox.getSelectedItem();
                donationService.processDonation(donor, amount, cause);
                refreshStats();
                refreshTable();
                dlg.dispose();
                JOptionPane.showMessageDialog(this,
                    String.format("Thank you! Rs.%.2f donated to %s.", amount, cause),
                    "Donation Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg,
                    "Please enter a valid numeric amount.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        c.gridy = 2;
        c.insets = new Insets(18, 0, 0, 0);
        p.add(confirm, c);
        dlg.add(p);
        dlg.setVisible(true);
    }

    // ── My report dialog ──────────────────────────────────────────
    private void showMyReport() {
        JDialog dlg = new JDialog(this, "My Donation Report", false);
        dlg.setSize(520, 420);
        dlg.setLocationRelativeTo(this);

        JTextArea area = new JTextArea("Generating report...");
        area.setFont(UITheme.FONT_MONO);
        area.setBackground(UITheme.BG_DARK);
        area.setForeground(UITheme.TEXT_PRIMARY);
        area.setEditable(false);
        area.setMargin(new Insets(12, 14, 12, 14));
        dlg.add(new JScrollPane(area));
        dlg.setVisible(true);

        Thread t = new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("=== MY DONATION HISTORY ===\n\n");
            sb.append(String.format("Name  : %s%n", donor.getName()));
            sb.append(String.format("Email : %s%n", donor.getEmail()));
            sb.append(String.format("ID    : %s%n%n", donor.getId()));
            List<Donation> list = donationService.getDonationsForDonor(donor.getId());
            sb.append(String.format("Total donations : %d%n", list.size()));
            sb.append(String.format("Total amount    : Rs.%.2f%n%n", donor.getTotalDonated()));
            sb.append("---------------------------------------------\n");
            for (Donation d : list) {
                sb.append(String.format("  Rs.%.2f | %-18s | %s%n",
                        d.getAmount(), d.getCause(), d.getDate()));
            }
            if (list.isEmpty()) sb.append("  No donations yet.\n");
            String result = sb.toString();
            SwingUtilities.invokeLater(() -> area.setText(result));
        });
        t.setDaemon(true);
        t.setName("DonorReportThread");
        t.start();
    }

    // ── Refresh helpers ───────────────────────────────────────────
    private void refreshStats() {
        List<Donation> list = donationService.getDonationsForDonor(donor.getId());
        double total = 0;
        for (Donation d : list) total += d.getAmount();
        final double finalTotal = total;
        final int    finalCount = list.size();
        SwingUtilities.invokeLater(() -> {
            totalLabel.setText(String.format("Rs.%.2f", finalTotal));
            countLabel.setText(String.valueOf(finalCount));
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Donation d : donationService.getDonationsForDonor(donor.getId())) {
            tableModel.addRow(new Object[]{
                d.getDonationId(),
                String.format("Rs.%.2f", d.getAmount()),
                d.getCause(),
                d.getDate()
            });
        }
    }

    // ── UI helpers ────────────────────────────────────────────────
    private void styleTable(JTable table) {
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
    }

    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(UITheme.BG_CARD);
        row.add(UITheme.label(label, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
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
