import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class GuestFrame extends JFrame {
    private JTable bikesTable;
    private DefaultTableModel bikesTableModel;

    public GuestFrame() {
        setTitle("Guest View - MotoVerse System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(135, 82, 55));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Available Bikes");
        titleLabel.setFont(new Font("Cambria", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Bikes Table
        String[] columns = {"Brand", "Model", "Year", "Price", "Condition", "Description"};
        bikesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        bikesTable = new JTable(bikesTableModel);
        bikesTable.getTableHeader().setReorderingAllowed(false);
        bikesTable.getTableHeader().setResizingAllowed(false);
        
        // Set custom column widths
        bikesTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Brand
        bikesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Model
        bikesTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // Year
        bikesTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Price
        bikesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Condition
        bikesTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Description

        JScrollPane scrollPane = new JScrollPane(bikesTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Back Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(135, 82, 55));

        JButton backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadBikes();
    }

    private void loadBikes() {
        bikesTableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("bikes_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                if (Integer.parseInt(bikeData[6]) > 0) { // Only show bikes with stock > 0
                    bikesTableModel.addRow(new Object[]{
                        bikeData[1], // Brand
                        bikeData[2], // Model
                        bikeData[3], // Year
                        "$" + bikeData[4], // Price
                        bikeData[5], // Condition
                        bikeData[7]  // Description
                    });
                }
            }
        } catch (IOException ex) {
            if (!ex.getMessage().contains("No such file")) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading bikes: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 