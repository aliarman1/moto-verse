import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserFrame extends JFrame {
    private String currentUsername;
    private JPanel mainPanel, bikesPanel, cartPanel, ordersPanel;
    private JTable bikesTable, cartTable, ordersTable;
    private DefaultTableModel bikesTableModel, cartTableModel, ordersTableModel;
    private Map<String, Integer> cart = new HashMap<>(); // bikeId -> quantity
    private JLabel totalLabel;
    private double total = 0.0;

    public UserFrame(String username) {
        this.currentUsername = username;
        setTitle("User Dashboard - MotoVerse System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with CardLayout
        mainPanel = new JPanel(new CardLayout());

        // Create panels
        createBikesPanel();
        createCartPanel();
        createOrdersPanel();

        // Navigation Panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(new Color(135, 82, 55));

        JButton bikesBtn = new JButton("Available Bikes");
        JButton cartBtn = new JButton("My Cart");
        JButton ordersBtn = new JButton("My Orders");
        JButton backBtn = new JButton("Back to Login");

        bikesBtn.addActionListener(e -> {
            ((CardLayout)mainPanel.getLayout()).show(mainPanel, "Bikes");
            loadBikes();
        });
        cartBtn.addActionListener(e -> ((CardLayout)mainPanel.getLayout()).show(mainPanel, "Cart"));
        ordersBtn.addActionListener(e -> {
            ((CardLayout)mainPanel.getLayout()).show(mainPanel, "Orders");
            loadOrders();
        });
        backBtn.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        navPanel.add(bikesBtn);
        navPanel.add(cartBtn);
        navPanel.add(ordersBtn);
        navPanel.add(backBtn);

        // Add panels to main panel
        mainPanel.add(bikesPanel, "Bikes");
        mainPanel.add(cartPanel, "Cart");
        mainPanel.add(ordersPanel, "Orders");

        // Add components to frame
        add(navPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        loadBikes();
        loadOrders();
    }

    private void createBikesPanel() {
        bikesPanel = new JPanel(new BorderLayout(10, 10));
        bikesPanel.setBackground(new Color(135, 82, 55));
        bikesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Available Bikes");
        titleLabel.setFont(new Font("Cambria", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(135, 82, 55));
        
        JLabel filterLabel = new JLabel("Show:");
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JComboBox<String> stockFilter = new JComboBox<>(new String[]{"All Bikes", "In Stock", "Out of Stock"});
        stockFilter.addActionListener(e -> {
            String selected = (String) stockFilter.getSelectedItem();
            loadBikes(selected);
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(stockFilter);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(135, 82, 55));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.EAST);
        
        bikesPanel.add(topPanel, BorderLayout.NORTH);

        // Bikes Table
        String[] columns = {"ID", "Brand", "Model", "Year", "Price", "Condition", "Availability", "Description"};
        bikesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bikesTable = new JTable(bikesTableModel);
        JScrollPane scrollPane = new JScrollPane(bikesTable);
        bikesPanel.add(scrollPane, BorderLayout.CENTER);

        // Add to Cart Panel
        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addToCartPanel.setBackground(new Color(135, 82, 55));
        
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton addToCartBtn = new JButton("Add to Cart");
        
        addToCartBtn.addActionListener(e -> {
            int selectedRow = bikesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a bike", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bikeId = bikesTable.getValueAt(selectedRow, 0).toString();
            String availability = bikesTable.getValueAt(selectedRow, 6).toString();
            
            if (availability.equals("Stock Out")) {
                JOptionPane.showMessageDialog(this, "This bike is currently out of stock", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get actual stock from file
            try {
                int availableStock = getAvailableStock(bikeId);
                int quantity = (Integer) quantitySpinner.getValue();

                if (quantity > availableStock) {
                    JOptionPane.showMessageDialog(this, "Not enough stock available", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                cart.put(bikeId, cart.getOrDefault(bikeId, 0) + quantity);
                updateCartTable();
                JOptionPane.showMessageDialog(this, "Added to cart successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error checking stock: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        addToCartPanel.add(new JLabel("Quantity:"));
        addToCartPanel.add(quantitySpinner);
        addToCartPanel.add(addToCartBtn);
        
        bikesPanel.add(addToCartPanel, BorderLayout.SOUTH);
    }

    private void createCartPanel() {
        cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBackground(new Color(135, 82, 55));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Shopping Cart");
        titleLabel.setFont(new Font("Cambria", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        cartPanel.add(titleLabel, BorderLayout.NORTH);

        // Cart Table
        String[] columns = {"ID", "Brand", "Model", "Price", "Quantity", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(135, 82, 55));

        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(new Color(135, 82, 55));
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(Color.WHITE);
        totalPanel.add(totalLabel);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(new Color(135, 82, 55));

        JButton removeBtn = new JButton("Remove Selected");
        JButton clearBtn = new JButton("Clear Cart");
        JButton checkoutBtn = new JButton("Checkout");

        removeBtn.addActionListener(e -> removeFromCart());
        clearBtn.addActionListener(e -> clearCart());
        checkoutBtn.addActionListener(e -> checkout());

        buttonsPanel.add(removeBtn);
        buttonsPanel.add(clearBtn);
        buttonsPanel.add(checkoutBtn);

        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createOrdersPanel() {
        ordersPanel = new JPanel(new BorderLayout(10, 10));
        ordersPanel.setBackground(new Color(135, 82, 55));
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My Orders");
        titleLabel.setFont(new Font("Cambria", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        ordersPanel.add(titleLabel, BorderLayout.NORTH);

        // Orders Table
        String[] columns = {"Order ID", "Bike", "Price", "Order Date", "Status"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        ordersPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(135, 82, 55));
        
        JButton refreshBtn = new JButton("Refresh Orders");
        JButton cancelBtn = new JButton("Cancel Order");
        
        refreshBtn.addActionListener(e -> loadOrders());
        cancelBtn.addActionListener(e -> cancelOrder());
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(cancelBtn);
        ordersPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBikes() {
        loadBikes("All Bikes");
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        total = 0.0;

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String bikeId = entry.getKey();
            int quantity = entry.getValue();

            try (BufferedReader reader = new BufferedReader(new FileReader("bikes_data.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] bikeData = line.split(",");
                    if (bikeData[0].equals(bikeId)) {
                        double price = Double.parseDouble(bikeData[4]);
                        double subtotal = price * quantity;
                        total += subtotal;
                        cartTableModel.addRow(new Object[]{
                            bikeId,
                            bikeData[1], // Brand
                            bikeData[2], // Model
                            price,
                            quantity,
                            subtotal
                        });
                        break;
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating cart: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bikeId = cartTable.getValueAt(selectedRow, 0).toString();
        cart.remove(bikeId);
        updateCartTable();
    }

    private void clearCart() {
        cart.clear();
        updateCartTable();
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Total amount: $" + String.format("%.2f", total) + "\nProceed with checkout?",
            "Confirm Order",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Create orders and update stock
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String orderDate = dateFormat.format(new Date());

            try {
                // Update bike stock
                updateBikeStock();

                // Save orders
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders_data.txt", true))) {
                    for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                        String bikeId = entry.getKey();
                        String bikeName = getBikeName(bikeId);
                        double price = getBikePrice(bikeId) * entry.getValue();
                        
                        String orderId = System.currentTimeMillis() + "-" + bikeId;
                        String orderData = String.format("%s,%s,%s,%s,%.2f,%s,%s",
                            orderId,
                            currentUsername,
                            bikeId,
                            bikeName,
                            price,
                            orderDate,
                            "Pending");
                        
                        writer.write(orderData);
                        writer.newLine();
                    }
                }

                JOptionPane.showMessageDialog(this, "Order placed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearCart();
                loadOrders();
                loadBikes();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error processing order: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateBikeStock() throws IOException {
        File inputFile = new File("bikes_data.txt");
        File tempFile = new File("temp_bikes_data.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                String bikeId = bikeData[0];
                
                if (cart.containsKey(bikeId)) {
                    int currentStock = Integer.parseInt(bikeData[6]);
                    int orderedQuantity = cart.get(bikeId);
                    int newStock = currentStock - orderedQuantity;
                    bikeData[6] = String.valueOf(newStock);
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

    private String getBikeName(String bikeId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("bikes_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                if (bikeData[0].equals(bikeId)) {
                    return bikeData[1] + " " + bikeData[2]; // Brand + Model
                }
            }
        }
        return "";
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

    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("orders_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                if (orderData[1].equals(currentUsername)) {
                    ordersTableModel.addRow(new Object[]{
                        orderData[0], // Order ID
                        orderData[3], // Bike Name
                        orderData[4], // Price
                        orderData[5], // Order Date
                        orderData[6]  // Status
                    });
                }
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

    private void cancelOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = ordersTable.getValueAt(selectedRow, 4).toString();
        if (status.equals("Completed") || status.equals("Cancelled")) {
            JOptionPane.showMessageDialog(this, 
                "Cannot cancel order. Status is already " + status, 
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
                String orderId = ordersTable.getValueAt(selectedRow, 0).toString();
                
                // First restore the bike stock
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
                loadBikes("All Bikes");

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

    // Update loadBikes to handle filtering
    private void loadBikes(String filter) {
        bikesTableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("bikes_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                int stock = Integer.parseInt(bikeData[6]);
                String availability = stock > 0 ? "Available" : "Stock Out";
                
                boolean shouldShow = filter.equals("All Bikes") ||
                    (filter.equals("In Stock") && stock > 0) ||
                    (filter.equals("Out of Stock") && stock == 0);
                
                if (shouldShow) {
                    Object[] displayData = {
                        bikeData[0],  // ID
                        bikeData[1],  // Brand
                        bikeData[2],  // Model
                        bikeData[3],  // Year
                        "$" + bikeData[4],  // Price
                        bikeData[5],  // Condition
                        availability, // Instead of stock number
                        bikeData[7]   // Description
                    };
                    bikesTableModel.addRow(displayData);
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

    // Add new helper method to get actual stock
    private int getAvailableStock(String bikeId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("bikes_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bikeData = line.split(",");
                if (bikeData[0].equals(bikeId)) {
                    return Integer.parseInt(bikeData[6]);
                }
            }
        }
        return 0;
    }
} 