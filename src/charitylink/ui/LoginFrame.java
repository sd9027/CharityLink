package charitylink.ui;

import charitylink.model.*;
import charitylink.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private AuthService authService;
    private JTabbedPane tabs;

    // Login fields
    private JTextField     loginEmail;
    private JPasswordField loginPassword;

    // Register fields
    private JTextField     regName;
    private JTextField     regEmail;
    private JPasswordField regPassword;
    private JPasswordField regConfirm;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        setTitle("CharityLink — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = UITheme.darkPanel();
        root.setLayout(new BorderLayout());

        // ─ Header ─
        JPanel header = UITheme.darkPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(36, 0, 20, 0));

        JLabel logo = UITheme.label("♥ CharityLink", UITheme.FONT_TITLE, UITheme.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel tagline = UITheme.label("Donate. Track. Make a difference.", UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(logo);
        header.add(Box.createVerticalStrut(6));
        header.add(tagline);

        // ─ Tabs ─
        tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_CARD);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("  Login  ", buildLoginPanel());
        tabs.addTab("  Register  ", buildRegisterPanel());

        root.add(header, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        add(root);
    }

    // ── Login Panel ───────────────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx = 0; c.weightx = 1;

        loginEmail    = UITheme.styledField(20);
        loginPassword = UITheme.styledPasswordField(20);

        c.gridy = 0; p.add(fieldGroup("Email", loginEmail), c);
        c.gridy = 1; p.add(fieldGroup("Password", loginPassword), c);

        JButton btn = UITheme.primaryButton("Login");
        btn.addActionListener(e -> doLogin());
        loginPassword.addActionListener(e -> doLogin());

        c.gridy = 2;
        c.insets = new Insets(18, 0, 6, 0);
        p.add(btn, c);

        JLabel hint = UITheme.label("Default admin: admin@charitylink.org / admin123",
                UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 3; c.insets = new Insets(10, 0, 0, 0);
        p.add(hint, c);
        return p;
    }

    // ── Register Panel ────────────────────────────────────────────
    private JPanel buildRegisterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx = 0; c.weightx = 1;

        regName     = UITheme.styledField(20);
        regEmail    = UITheme.styledField(20);
        regPassword = UITheme.styledPasswordField(20);
        regConfirm  = UITheme.styledPasswordField(20);

        c.gridy = 0; p.add(fieldGroup("Full Name", regName), c);
        c.gridy = 1; p.add(fieldGroup("Email",     regEmail), c);
        c.gridy = 2; p.add(fieldGroup("Password (min 6 chars)", regPassword), c);
        c.gridy = 3; p.add(fieldGroup("Confirm Password",       regConfirm),  c);

        JButton btn = UITheme.primaryButton("Create Account");
        btn.addActionListener(e -> doRegister());
        c.gridy = 4; c.insets = new Insets(18, 0, 6, 0);
        p.add(btn, c);
        return p;
    }

    // ── Actions ───────────────────────────────────────────────────
    private void doLogin() {
        try {
            String email = loginEmail.getText().trim();
            String pass  = new String(loginPassword.getPassword());
            User user = authService.login(email, pass);
            dispose();
            if (user instanceof charitylink.model.Admin) {
                new AdminDashboard((charitylink.model.Admin) user, authService);
            } else {
                new DonorDashboard((Donor) user, authService);
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void doRegister() {
        String name  = regName.getText().trim();
        String email = regEmail.getText().trim();
        String pass  = new String(regPassword.getPassword());
        String conf  = new String(regConfirm.getPassword());
        if (!pass.equals(conf)) { showError("Passwords do not match."); return; }
        try {
            authService.registerDonor(name, email, pass);
            JOptionPane.showMessageDialog(this,
                "Account created! You can now log in.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
            tabs.setSelectedIndex(0);
            loginEmail.setText(email);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────
    private JPanel fieldGroup(String label, JComponent field) {
        JPanel group = UITheme.darkPanel();
        group.setBackground(UITheme.BG_CARD);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        JLabel lbl = UITheme.label(label, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        group.add(lbl);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        group.add(field);
        return group;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
