package Main_GUI.components;

import Main_GUI.lib.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RadioInputField extends JPanel {
    // Components
    private JLabel label;
    private JPanel radioPanel;
    private ButtonGroup buttonGroup;
    private JLabel helperText;

    // Colors
    private final Color backgroundColor = new Color(33, 33, 36);
    private final Color borderColor = new Color(47, 47, 50);
    private final Color focusBorderColor = new Color(127, 34, 254);
    private final Color errorBorderColor = new Color(217, 48, 37);
    private final Color labelColor = Color.WHITE;
    private final Color focusLabelColor = Color.WHITE;
    private final Color textColor = Color.WHITE;
    private final Color helperTextColor = new Color(95, 99, 104);
    private final Color errorHelperTextColor = new Color(217, 48, 37);

    // Styling
    private boolean hasError = false;

    // States
    private boolean isFocused = false;

    // Validation
    private InputValidator validator;

    public RadioInputField() {
        initializeField();
    }

    public RadioInputField(String labelText) {
        initializeField();
        setLabel(labelText);
    }

    private void initializeField() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        // Label
        label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(labelColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));

        // Radio panel
        radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setOpaque(false);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioPanel.setBorder(new EmptyBorder(0, 4, 0, 0));

        // Button group
        buttonGroup = new ButtonGroup();

        // Helper text
        helperText = new JLabel();
        helperText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        helperText.setForeground(helperTextColor);
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);
        helperText.setBorder(new EmptyBorder(8, 4, 0, 0));

        // Add components
        add(label);
        add(radioPanel);
        add(helperText);
    }

    private void updateLabelColor() {
        if (hasError) {
            label.setForeground(errorBorderColor);
        } else if (isFocused) {
            label.setForeground(focusLabelColor);
        } else {
            label.setForeground(labelColor);
        }
    }

    // Public methods
    public void setLabel(String text) {
        if (text == null) {
            label.setText("");
            label.setVisible(false);
        } else {
            label.setText(text);
            label.setVisible(true);
        }
    }

    public void setOptions(String[] options) {
        // Clear existing radio buttons
        radioPanel.removeAll();
        buttonGroup = new ButtonGroup();

        for (String option : options) {
            JRadioButton radioButton = createRadioButton(option);
            radioPanel.add(radioButton);
            buttonGroup.add(radioButton);
            radioPanel.add(Box.createRigidArea(new Dimension(0, 4))); // Spacing
        }

        radioPanel.revalidate();
        radioPanel.repaint();
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioButton.setForeground(textColor);
        radioButton.setOpaque(false);
        radioButton.setFocusPainted(true);

        // Custom focus listener
        radioButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                updateLabelColor();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                updateLabelColor();
            }
        });

        return radioButton;
    }

    public void setSelectedValue(String value) {
        if (value == null) return;

        for (Component comp : radioPanel.getComponents()) {
            if (comp instanceof JRadioButton radio) {
                if (radio.getText().equals(value)) {
                    radio.setSelected(true);
                    break;
                }
            }
        }
    }

    public String getSelectedValue() {
        for (Component comp : radioPanel.getComponents()) {
            if (comp instanceof JRadioButton radio) {
                if (radio.isSelected()) {
                    return radio.getText();
                }
            }
        }
        return "";
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        // Enable/disable all radio buttons
        for (Component comp : radioPanel.getComponents()) {
            if (comp instanceof JRadioButton) {
                comp.setEnabled(enabled);
            }
        }

        label.setEnabled(enabled);
        helperText.setEnabled(enabled);

        if (!enabled) {
            label.setForeground(Color.GRAY);
        } else {
            updateLabelColor();
        }
    }
}