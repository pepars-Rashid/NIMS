package Main_GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Dialog extends JPanel {
    // Colors and style
    private final Color background = new Color(24, 24, 27);
    private final Color borderColor = new Color(47, 47, 50);
    private final Color overlayColor = new Color(0, 0, 0, 128); // Semi-transparent overlay
    private final int borderRadius = 0;
    private final int borderThickness = 1;

    // Content panel
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JPanel footerPanel;
    private JLabel titleLabel;

    // Container references
    private JDialog dialog;
    private JPanel overlayPanel;
    private boolean isModal = true;
    private boolean showOverlay = true;

    public Dialog() {
        initializeDialog();
    }

    public Dialog(String title) {
        initializeDialog();
        setTitle(title);
    }

    public Dialog(String title, boolean isModal) {
        initializeDialog();
        setTitle(title);
        this.isModal = isModal;
    }

    private void initializeDialog() {
        setOpaque(false); // Opacity
        setLayout(new BorderLayout()); // Border layout manager
        setBorder(new EmptyBorder(24, 24, 24, 24)); // Padding inside the dialog
        setPreferredSize(new Dimension(1200, 400)); // Dialog width & height

        // Create a frame as the dialog container
        dialog = new JDialog((Frame) null, "", true); // Default modal
        dialog.setUndecorated(true); // Remove the frames colors and functions
        dialog.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        dialog.setContentPane(createDialogContainer());
        dialog.pack();

        // Create overlay panel
        overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(overlayColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        overlayPanel.setLayout(new GridBagLayout());
        overlayPanel.setOpaque(false);

        // Header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Opacity
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0)); // Margin around the header panel

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Content panel
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout()); // Border layout manager

        // Footer panel
        footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(16, 0, 0, 0)); // Margin around the footer panel

        // Assemble dialog
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);

        innerPanel.add(headerPanel, BorderLayout.NORTH);
        innerPanel.add(contentPanel, BorderLayout.CENTER);
        innerPanel.add(footerPanel, BorderLayout.SOUTH);

        add(innerPanel, BorderLayout.CENTER);
    }

    // Method to draw the component using the attributes we provided
    private JPanel createDialogContainer() {
        JPanel container = new JPanel() {
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
        };
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(1, 1, 1, 1));
        container.add(this, BorderLayout.CENTER);
        return container;
    }

    // Show methods
    public void show(Component parent) {
        if (parent != null) {
            dialog.setLocationRelativeTo(parent);
        } else {
            dialog.setLocationRelativeTo(null);
        }

        if (showOverlay) {
            showWithOverlay(parent);
        } else {
            dialog.setVisible(true);
        }
    }

    public void show() {
        show(null);
    }

    private void showWithOverlay(Component parent) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(parent);
        if (topFrame != null) {
            JLayeredPane layeredPane = topFrame.getLayeredPane();

            // Set overlay size to match layered pane
            overlayPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());

            // Add overlay to highest layer
            layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

            // Center dialog on overlay
            dialog.setLocation(
                    topFrame.getX() + (topFrame.getWidth() - dialog.getWidth()) / 2,
                    topFrame.getY() + (topFrame.getHeight() - dialog.getHeight()) / 2
            );

            overlayPanel.setVisible(true);
            dialog.setVisible(true);
        } else {
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        }
    }

    // Hide methods
    public void hide() {
        dialog.setVisible(false);
        if (showOverlay && overlayPanel != null) {
            overlayPanel.setVisible(false);
            if (overlayPanel.getParent() != null) {
                overlayPanel.getParent().remove(overlayPanel);
            }
        }
    }

    // Setters and getters
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setContent(Component component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void addContent(Component component) {
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void addFooterButton(JButton button) {
        footerPanel.add(button);
    }

    public void setSize(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        dialog.pack();
    }

    // Custom paint for the dialog panel
    @Override
    protected void paintComponent(Graphics g) {
        // Background painting is handled by the container
        super.paintComponent(g);
    }
}