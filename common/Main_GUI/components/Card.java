package Main_GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Card extends JPanel {
    // Colors and style
    private final Color background = new Color(24, 24, 27);
    private final Color borderColor = new Color(47, 47, 50);
    private final int borderRadius = 16;
    private final int borderThickness = 1;

    // Content panel
    JPanel contentPanel = new JPanel();
    JLabel title = new JLabel();
    JLabel description = new JLabel();

    public Card() {
        initializeCard();
    }

    public Card(String title, String description) {
        initializeCard();
        this.title.setText(title);
        this.description.setText(description);
    }

    private void initializeCard() {
        setOpaque(false); // Opacity
        setLayout(new BorderLayout()); // layout manager for parent panel
        setBorder(new EmptyBorder(16, 16, 16, 16)); // Padding

        contentPanel.add(Box.createRigidArea(new Dimension(0, 16))); // gap between elements

        // Title panel
        title.setFont(new Font("Segoe UI", Font.BOLD, 16)); // font face, font weight, font size
        title.setForeground(Color.WHITE);

        // Description panel
        description.setFont(new Font("Segoe UI", Font.BOLD, 14)); // font face, font weight, font size
        description.setForeground(new Color(150, 150, 150));

        // Content panel
        contentPanel.setOpaque(false); // Opacity
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // layout manager for content panel
        contentPanel.add(title);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8))); // gap between elements
        contentPanel.add(description);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 32))); // gap between elements

        add(contentPanel, BorderLayout.CENTER); // place the content panel in the center of the parent panel
    }

    // To add components to the content panel
    public void addContent(Component component) {
        contentPanel.add(component);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16))); // gap between elements
    }

    // Method to draw the component using the attributes we provided
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(background);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

        // Draw border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(borderThickness));
        g2d.drawRoundRect(
                borderThickness / 2,
                borderThickness / 2,
                getWidth() - borderThickness,
                getHeight() - borderThickness,
                borderRadius, borderRadius
        );

        g2d.dispose();
    }
}
