import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class AdminFrame extends JFrame {
    private JPanel mainPanel, userPanel, bikePanel, orderPanel;
    private JTable userTable, bikeTable, orderTable;
    private JScrollPane userScrollPane, bikeScrollPane, orderScrollPane;
    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private JButton backButton, deleteUserButton, refreshButton;
    private DefaultTableModel bikeTableModel, orderTableModel;
    private JTextField brandField, modelField, yearField, priceField, stockField;
    private JTextArea descriptionArea;
    private JComboBox<String> conditionCombo;

    public AdminFrame() {
        setTitle("Admin Dashboard - MotoVerse System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with CardLayout
        mainPanel = new JPanel(new CardLayout());

        // Create User Management Panel
        createUserManagementPanel();

        // Create Bike Management Panel
        createBikeManagementPanel();

        // Create Order Management Panel
        createOrderManagementPanel();

        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(new Color(135, 82, 55));

        JButton userManagementBtn = new JButton("User Management");
        JButton bikeManagementBtn = new JButton("Bike Management");
        JButton orderManagementBtn = new JButton("Order Management");
        backButton = new JButton("Back to Login");

        userManagementBtn.addActionListener(e -> ((CardLayout)mainPanel.getLayout()).show(mainPanel, "Users"));
        bikeManagementBtn.addActionListener(e -> ((CardLayout)mainPanel.getLayout()).show(mainPanel, "Bikes"));
        orderManagementBtn.addActionListener(e -> {
            ((CardLayout)mainPanel.getLayout()).show(mainPanel, "Orders");
            loadOrders();
        });
        backButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        navPanel.add(userManagementBtn);
        navPanel.add(bikeManagementBtn);
        navPanel.add(orderManagementBtn);
        navPanel.add(backButton);

        // Add panels to main panel
        mainPanel.add(userPanel, "Users");
        mainPanel.add(bikePanel, "Bikes");
        mainPanel.add(orderPanel, "Orders");

        // Add components to frame
        add(navPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        loadUsers();
        loadBikes();
    }

    private void createUserManagementPanel() {
        userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setBackground(new Color(135, 82, 55));
        userPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Cambria", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        userPanel.add(titleLabel, BorderLayout.NORTH);

        // User List
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setBackground(new Color(245, 245, 245));
        userList.setFont(new Font("Arial", Font.PLAIN, 14));
        userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(600, 400));
        userPanel.add(userScrollPane, BorderLayout.CENTER);

        // User Buttons Panel
        JPanel userButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        userButtonPanel.setBackground(new Color(135, 82, 55));

        deleteUserButton = new JButton("Delete User");
        refreshButton = new JButton("Refresh List");

        deleteUserButton.addActionListener(e -> deleteSelectedUser());
        refreshButton.addActionListener(e -> loadUsers());

        userButtonPanel.add(deleteUserButton);
        userButtonPanel.add(refreshButton);
        userPanel.add(userButtonPanel, BorderLayout.SOUTH);
    }

    private void createBikeManagementPanel() {
        bikePanel = new JPanel(new BorderLayout(10, 10));
        bikePanel.setBackground(new Color(135, 82, 55));
        bikePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Bike Management");
        titleLabel.setFont(new Font("Cambria", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        bikePanel.add(titleLabel, BorderLayout.NORTH);

        // Bike Table
        String[] columns = {"ID", "Brand", "Model", "Year", "Price", "Condition", "Stock", "Description"};
        bikeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bikeTable = new JTable(bikeTableModel);
        bikeScrollPane = new JScrollPane(bikeTable);
        bikePanel.add(bikeScrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(135, 82, 55));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form Fields
        brandField = new JTextField(15);
        modelField = new JTextField(15);
        yearField = new JTextField(15);
        priceField = new JTextField(15);
        stockField = new JTextField(15);
        descriptionArea = new JTextArea(3, 15);
        conditionCombo = new JComboBox<>(new String[]{"New", "Used", "Refurbished"});

        // Add form components
        addFormComponent(formPanel, "Brand:", brandField, gbc, 0);
        addFormComponent(formPanel, "Model:", modelField, gbc, 1);
        addFormComponent(formPanel, "Year:", yearField, gbc, 2);
        addFormComponent(formPanel, "Price:", priceField, gbc, 3);
        addFormComponent(formPanel, "Stock:", stockField, gbc, 4);
        addFormComponent(formPanel, "Condition:", conditionCombo, gbc, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(135, 82, 55));

        JButton addButton = new JButton("Add Bike");
        JButton updateButton = new JButton("Update Bike");
        JButton deleteButton = new JButton("Delete Bike");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh List");

        addButton.addActionListener(e -> addBike());
        updateButton.addActionListener(e -> updateBike());
        deleteButton.addActionListener(e -> deleteBike());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadBikes());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        // Add form and buttons to a container panel
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(135, 82, 55));
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        bikePanel.add(southPanel, BorderLayout.SOUTH);

        // Table Selection Listener
        bikeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bikeTable.getSelectedRow() != -1) {
                int row = bikeTable.getSelectedRow();
                brandField.setText(bikeTable.getValueAt(row, 1).toString());
                modelField.setText(bikeTable.getValueAt(row, 2).toString());
                yearField.setText(bikeTable.getValueAt(row, 3).toString());
                priceField.setText(bikeTable.getValueAt(row, 4).toString());
                conditionCombo.setSelectedItem(bikeTable.getValueAt(row, 5).toString());
                stockField.setText(bikeTable.getValueAt(row, 6).toString());
                descriptionArea.setText(bikeTable.getValueAt(row, 7).toString());
            }
        });
    }

    private void createOrderManagementPanel() {
        orderPanel = new JPanel(new BorderLayout(10, 10));
        orderPanel.setBackground(new Color(135, 82, 55));
        orderPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Order Management");
        titleLabel.setFont(new Font("Cambria", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        orderPanel.add(titleLabel, BorderLayout.NORTH);

        // Orders Table
        String[] columns = {"Order ID", "Username", "Bike", "Price", "Order Date", "Status"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        orderScrollPane = new JScrollPane(orderTable);
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(135, 82, 55));

        JButton completeOrderBtn = new JButton("Mark as Completed");
        JButton cancelOrderBtn = new JButton("Cancel Order");
        JButton refreshOrdersBtn = new JButton("Refresh Orders");

        completeOrderBtn.addActionListener(e -> completeOrder());
        cancelOrderBtn.addActionListener(e -> cancelOrder());
        refreshOrdersBtn.addActionListener(e -> loadOrders());

        buttonPanel.add(completeOrderBtn);
        buttonPanel.add(cancelOrderBtn);
        buttonPanel.add(refreshOrdersBtn);
        orderPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormComponent(JPanel panel, String label, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void addBike() {
        if (!validateBikeFields()) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bikes_data.txt", true))) {
            String bikeData = String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                System.currentTimeMillis(), // Using timestamp as ID
                brandField.getText(),
                modelField.getText(),
                yearField.getText(),
                priceField.getText(),
                conditionCombo.getSelectedItem(),
                stockField.getText(),
                descriptionArea.getText().replace(",", ";"));
            
            writer.write(bikeData);
            writer.newLine();
            
            JOptionPane.showMessageDialog(this, "Bike added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadBikes();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error adding bike: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBike() {
        int selectedRow = bikeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bike to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateBikeFields()) return;

        String bikeId = bikeTable.getValueAt(selectedRow, 0).toString();
        try {
            File inputFile = new File("bikes_data.txt");
            File tempFile = new File("temp_bikes_data.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                if (bikeData[0].equals(bikeId)) {
                    // Write updated bike data
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                        bikeId,
                        brandField.getText(),
                        modelField.getText(),
                        yearField.getText(),
                        priceField.getText(),
                        conditionCombo.getSelectedItem(),
                        stockField.getText(),
                        descriptionArea.getText().replace(",", ";")));
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            writer.close();
            reader.close();

            if (!inputFile.delete()) {
                throw new IOException("Could not delete the original file");
            }
            if (!tempFile.renameTo(inputFile)) {
                throw new IOException("Could not rename temp file");
            }

            JOptionPane.showMessageDialog(this, "Bike updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBikes();
            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error updating bike: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBike() {
        int selectedRow = bikeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bike to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this bike?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String bikeId = bikeTable.getValueAt(selectedRow, 0).toString();
            try {
                File inputFile = new File("bikes_data.txt");
                File tempFile = new File("temp_bikes_data.txt");

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] bikeData = line.split(",");
                    if (!bikeData[0].equals(bikeId)) {
                        writer.write(line);
                        writer.newLine();
                    }
                }

                writer.close();
                reader.close();

                if (!inputFile.delete()) {
                    throw new IOException("Could not delete the original file");
                }
                if (!tempFile.renameTo(inputFile)) {
                    throw new IOException("Could not rename temp file");
                }

                JOptionPane.showMessageDialog(this, "Bike deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBikes();
                clearFields();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting bike: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadBikes() {
        bikeTableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("bikes_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                bikeTableModel.addRow(bikeData);
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

    private boolean validateBikeFields() {
        if (brandField.getText().isEmpty() || modelField.getText().isEmpty() || 
            yearField.getText().isEmpty() || priceField.getText().isEmpty() || 
            stockField.getText().isEmpty() || descriptionArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int year = Integer.parseInt(yearField.getText());
            if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR) + 1) {
                JOptionPane.showMessageDialog(this, "Invalid year", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            int stock = Integer.parseInt(stockField.getText());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Stock cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearFields() {
        brandField.setText("");
        modelField.setText("");
        yearField.setText("");
        priceField.setText("");
        stockField.setText("");
        descriptionArea.setText("");
        conditionCombo.setSelectedIndex(0);
        bikeTable.clearSelection();
    }

    // Existing user management methods
    private void loadUsers() {
        userListModel.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("registration_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                userListModel.addElement(String.format("Username: %s | Name: %s | Email: %s | Phone: %s", 
                    userData[0], userData[1], userData[2], userData[3]));
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading users: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a user to delete", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedUser = userList.getSelectedValue();
        String username = selectedUser.split("\\|")[0].split(":")[1].trim();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + username + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            deleteUserFromFile(username);
        }
    }

    private void deleteUserFromFile(String username) {
        try {
            File inputFile = new File("registration_data.txt");
            File tempFile = new File("temp_registration_data.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean userDeleted = false;

            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (!userData[0].equals(username)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    userDeleted = true;
                }
            }

            writer.close();
            reader.close();

            if (!inputFile.delete()) {
                throw new IOException("Could not delete the original file");
            }

            if (!tempFile.renameTo(inputFile)) {
                throw new IOException("Could not rename temp file");
            }

            if (userDeleted) {
                JOptionPane.showMessageDialog(this,
                    "User deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error deleting user: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrders() {
        orderTableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("orders_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                orderTableModel.addRow(new Object[]{
                    orderData[0], // Order ID
                    orderData[1], // Username
                    orderData[3], // Bike Name
                    orderData[4], // Price
                    orderData[5], // Order Date
                    orderData[6]  // Status
                });
            }
        } catch (IOException ex) {
            if (!ex.getMessage().contains("No such file")) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading orders: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void completeOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to complete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = orderTable.getValueAt(selectedRow, 0).toString();
        String currentStatus = orderTable.getValueAt(selectedRow, 5).toString();

        if (currentStatus.equals("Completed")) {
            JOptionPane.showMessageDialog(this, "Order is already completed", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File inputFile = new File("orders_data.txt");
            File tempFile = new File("temp_orders_data.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                if (orderData[0].equals(orderId)) {
                    // Update status to Completed
                    orderData[6] = "Completed";
                    line = String.join(",", orderData);
                }
                writer.write(line);
                writer.newLine();
            }

            writer.close();
            reader.close();

            if (!inputFile.delete()) {
                throw new IOException("Could not delete the original file");
            }
            if (!tempFile.renameTo(inputFile)) {
                throw new IOException("Could not rename temp file");
            }

            JOptionPane.showMessageDialog(this, "Order marked as completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadOrders();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating order: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = orderTable.getValueAt(selectedRow, 0).toString();
        String currentStatus = orderTable.getValueAt(selectedRow, 5).toString();

        if (currentStatus.equals("Completed") || currentStatus.equals("Cancelled")) {
            JOptionPane.showMessageDialog(this, 
                "Cannot cancel order. Status is already " + currentStatus, 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this order?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First, restore the bike stock
                restoreOrderStock(orderId);

                // Then update the order status
                File inputFile = new File("orders_data.txt");
                File tempFile = new File("temp_orders_data.txt");

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] orderData = line.split(",");
                    if (orderData[0].equals(orderId)) {
                        // Update status to Cancelled
                        orderData[6] = "Cancelled";
                        line = String.join(",", orderData);
                    }
                    writer.write(line);
                    writer.newLine();
                }

                writer.close();
                reader.close();

                if (!inputFile.delete()) {
                    throw new IOException("Could not delete the original file");
                }
                if (!tempFile.renameTo(inputFile)) {
                    throw new IOException("Could not rename temp file");
                }

                JOptionPane.showMessageDialog(this, "Order cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error cancelling order: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreOrderStock(String orderId) throws IOException {
        // First get the order details
        String bikeId = "";
        int quantity = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("orders_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                if (orderData[0].equals(orderId)) {
                    bikeId = orderData[2];
                    quantity = (int)(Double.parseDouble(orderData[4]) / getBikePrice(bikeId));
                    break;
                }
            }
        }

        // Now update the bike stock
        File inputFile = new File("bikes_data.txt");
        File tempFile = new File("temp_bikes_data.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                if (bikeData[0].equals(bikeId)) {
                    int currentStock = Integer.parseInt(bikeData[6]);
                    bikeData[6] = String.valueOf(currentStock + quantity);
                    line = String.join(",", bikeData);
                }
                writer.write(line);
                writer.newLine();
            }
        }

        if (!inputFile.delete()) {
            throw new IOException("Could not delete the original file");
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Could not rename temp file");
        }
    }

    private double getBikePrice(String bikeId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("bikes_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                if (bikeData[0].equals(bikeId)) {
                    return Double.parseDouble(bikeData[4]);
                }
            }
        }
        return 0.0;
    }
} 