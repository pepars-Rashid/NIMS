package Main_GUI.components;

import Main_GUI.lib.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class TextInputField extends JPanel {

    // Components
    private JLabel label;
    private JTextField textField;
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
    private final int borderRadius = 16;
    private final int borderThickness = 1;
    private final int focusBorderThickness = 1;
    private boolean hasError = false;
    private boolean isRequired = false;
    private String placeholder = "";

    // States
    private boolean isFocused = false;

    // Validation
    private InputValidator validator;

    public TextInputField() {
        initializeField();
    }

    public TextInputField(String labelText) {
        initializeField();
        setLabel(labelText);
    }

    public TextInputField(String labelText, String placeholder) {
        initializeField();
        setLabel(labelText);
        setPlaceholder(placeholder);
    }

    private void initializeField() {
        setOpaque(false); // opacity of parent panel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // layout manager of parent panel
        setBorder(new EmptyBorder(0, 0, 0, 0)); // margin around the text field component

        // Label
        label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.BOLD, 14)); // font face, font weight, font size
        label.setForeground(labelColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT); // align the label to the left of the parent panel
        label.setBorder(new EmptyBorder(0, 0, 4, 0)); // margin around label

        // Text field
        textField = new JTextField() {

            // Method to draw the component using the attributes we provided
            @Override
            protected void paintComponent(Graphics g) {
                // Paint the background
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint the input field background
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);

                // Paint the border
                Color currentBorderColor = borderColor;
                int currentBorderThickness = borderThickness;

                if (hasError) {
                    currentBorderColor = errorBorderColor;
                    currentBorderThickness = focusBorderThickness;
                } else if (isFocused) {
                    currentBorderColor = focusBorderColor;
                    currentBorderThickness = focusBorderThickness;
                }

                g2d.setColor(currentBorderColor);
                g2d.setStroke(new BasicStroke(currentBorderThickness));
                g2d.drawRoundRect(
                        currentBorderThickness / 2,
                        currentBorderThickness / 2,
                        getWidth() - currentBorderThickness,
                        getHeight() - currentBorderThickness,
                        borderRadius, borderRadius
                );

                g2d.dispose();

                // Paint the text
                super.paintComponent(g);

                // Paint placeholder if text is empty and not focused
                if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 150, 150));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // font face, font weight, font size
        textField.setForeground(textColor);
        textField.setBorder(BorderFactory.createEmptyBorder(16, 12, 12, 12)); // padding inside the text field
        textField.setOpaque(false);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));

        // Focus listeners
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                updateLabelColor();
                textField.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                updateLabelColor();
                textField.repaint();
                validateInput();
            }
        });

        // Text change listener
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearError();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearError();
            }

            private void clearError() {
                if (hasError) {
                    setError(false, "");
                }
            }
        });

        // Helper text
        helperText = new JLabel();
        helperText.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // font face, font weight, font size
        helperText.setForeground(helperTextColor);
        helperText.setAlignmentX(Component.LEFT_ALIGNMENT);
        helperText.setBorder(new EmptyBorder(8, 4, 0, 0)); // margin around helper text

        // Add components
        add(label);
        add(textField);
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

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder == null ? "" : placeholder;
        textField.repaint();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText().trim();
    }

    public void setRequired(boolean required) {
        this.isRequired = required;
    }

    public void setHelperText(String text) {
        if (text == null || text.isEmpty()) {
            helperText.setText("");
            helperText.setVisible(false);
        } else {
            helperText.setText(text);
            helperText.setVisible(true);
        }
    }

    public void setError(boolean error, String errorMessage) {
        this.hasError = error;
        if (error) {
            helperText.setForeground(errorHelperTextColor);
            helperText.setText(errorMessage);
            helperText.setVisible(true);
            updateLabelColor();
        } else {
            helperText.setForeground(helperTextColor);
            helperText.setText("");
            helperText.setVisible(false);
            updateLabelColor();
        }
        textField.repaint();
    }

    public boolean hasError() {
        return hasError;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        label.setEnabled(enabled);
        helperText.setEnabled(enabled);
        if (!enabled) {
            label.setForeground(Color.GRAY);
        } else {
            updateLabelColor();
        }
    }

    // Validation
    public void setValidator(InputValidator validator) {
        this.validator = validator;
    }

    public boolean validateInput() {
        if (validator != null) {
            String error = validator.validate(getText());
            if (error != null && !error.isEmpty()) {
                setError(true, error);
                return false;
            }
        }
        setError(false, "");
        return true;
    }
}

