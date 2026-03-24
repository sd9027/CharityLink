package charitylink.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UITheme {
    public static final Color BG_DARK     = new Color(18, 18, 24);
    public static final Color BG_CARD     = new Color(28, 28, 38);
    public static final Color BG_INPUT    = new Color(38, 38, 52);
    public static final Color ACCENT      = new Color(29, 158, 117);   // teal
    public static final Color ACCENT_DARK = new Color(15, 110, 86);
    public static final Color ACCENT2     = new Color(83, 74, 183);    // purple
    public static final Color TEXT_PRIMARY   = new Color(240, 240, 245);
    public static final Color TEXT_SECONDARY = new Color(150, 150, 165);
    public static final Color DANGER      = new Color(220, 60, 60);
    public static final Color AMBER       = new Color(186, 117, 23);
    public static final Color BORDER      = new Color(55, 55, 72);

    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 12);

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        );
    }

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_HEADER);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(BG_INPUT);
        btn.setForeground(TEXT_PRIMARY);
        return btn;
    }

    public static JTextField styledField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setFont(FONT_BODY);
        return tf;
    }

    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pf.setFont(FONT_BODY);
        return pf;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_DARK);
        return p;
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(cardBorder());
        return p;
    }
}
