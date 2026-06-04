package Main_GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class DataTable extends JPanel {
    // Colors and style
    private final Color background = new Color(24, 24, 27);
    private final Color borderColor = new Color(47, 47, 50);
    private final Color headerBackground = new Color(24, 24, 27);
    private final Color headerTextColor = Color.WHITE;
    private final Color cellTextColor = new Color(114, 114, 120);
    private final Color gridColor = new Color(55, 55, 58);
    private final int borderRadius = 16;
    private final int borderThickness = 1;

    // Table components
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    // With initial data
    public DataTable(String[] columnNames, Object[][] data) {
        initializeTable();
        setData(columnNames, data);
    }

    // Without initial data
    public DataTable(String[] columnNames) {
        initializeTable();
        setData(columnNames);
    }

    private void initializeTable() {
        setOpaque(false); // Opacity
        setLayout(new BorderLayout()); // Layout manager for parent panel
        setBorder(new EmptyBorder(16, 1, 16, 1)); // Padding around the table

        // Create table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        // Create table
        table = new JTable(tableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                // Fill entire background with the same color (no alternating rows)
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(background);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        // Configure table appearance
        configureTableAppearance();

        // Create scroll pane
        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder()); // Remove viewport border

        // Customize scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 65);
                this.trackColor = background;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    private void configureTableAppearance() {
        // Table settings
        table.setOpaque(false); // Opacity
        table.setForeground(cellTextColor);
        table.setBackground(background);
        table.setSelectionBackground(new Color(49, 49, 51));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(gridColor);
        table.setShowGrid(true);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(BorderFactory.createEmptyBorder()); // Remove table border

        // Configure header
        JTableHeader header = table.getTableHeader();
        header.setBackground(headerBackground);
        header.setForeground(headerTextColor);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setBorder(BorderFactory.createEmptyBorder()); // Remove header border

        // Disable header reordering
        header.setReorderingAllowed(false);

        // Custom header renderer to remove borders
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Remove all borders from header cells
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBackground(headerBackground);
                label.setForeground(headerTextColor);

                return label;
            }
        });

        // Custom cell renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JComponent c = (JComponent) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setOpaque(false);
                c.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Padding on both sides

                // Center align the first column (usually IDs or serial numbers)
                if (column == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                // Set text color
                if (isSelected) {
                    setForeground(table.getSelectionForeground());
                    c.setBackground(table.getSelectionBackground());
                    c.setOpaque(true); // Make selected row opaque to show selection color
                } else {
                    setForeground(cellTextColor);
                }

                return c;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    // Set data for the table
    public void setData(String[] columnNames, Object[][] data) {
        tableModel.setDataVector(data, columnNames);
        configureTableAppearance();
    }

    public void setData(String[] columnNames) {
        Object[][] data = {};
        tableModel.setDataVector(data, columnNames);
        configureTableAppearance();
    }

    // Add a single row
    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    // Clear all data
    public void clearData() {
        tableModel.setRowCount(0);
    }

    // Get selected row data
    public Object[] getSelectedRowData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int columnCount = table.getColumnCount();
            Object[] rowData = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                rowData[i] = table.getValueAt(selectedRow, i);
            }
            return rowData;
        }
        return null;
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