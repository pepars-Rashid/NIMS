package Main_GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PrimaryButton extends JButton {
    // Colors and style
    private final Color background = new Color(127, 34, 254);
    private final Color hoverBackground = new Color(109, 29, 219);
    private final Color pressedBackground = new Color(91, 24, 184);
    private final Color textColor = Color.WHITE;
    private final int borderRadius = 16;
    private int paddingX = 32;
    private int paddingY = 20;


    public PrimaryButton(String text, ActionListener onClick) {
        super(text);
        initializeButton(onClick);
    }

    public PrimaryButton(String text) {
        super(text);
        initializeButton(e -> {});
    }

    public PrimaryButton(String text, int px, int py, ActionListener onClick) {
        super(text);
        paddingX = px;
        paddingY = py;
        initializeButton(onClick);
    }

    private void initializeButton(ActionListener onClick) {
        // Set the initial background
        super.setBackground(background);

        // Set the basic styling
        setFont(new Font("Segoe UI", Font.BOLD, 16)); // font face, font weight, font size
        setForeground(textColor); // text color
        setFocusPainted(false); // remove focus state
        setContentAreaFilled(false);
        setOpaque(false); // opacity
        setBorder(new EmptyBorder(paddingY, paddingX, paddingY - 8, paddingX)); // padding around the text

        // Add on-click listener
        addActionListener(onClick);

        // Add hover and press effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBackground);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(background);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(pressedBackground);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (getModel().isRollover()) {
                    setBackground(hoverBackground);
                } else {
                    setBackground(background);
                }
                repaint();
            }
        });
    }

    // Method to draw the component using the attributes we provided
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rounded background
        if (isEnabled()) {
            g2.setColor(getBackground());
        } else {
            g2.setColor(Color.GRAY); // Disabled state
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

        // Draw the button text
        super.paintComponent(g2);
        g2.dispose();
    }
}