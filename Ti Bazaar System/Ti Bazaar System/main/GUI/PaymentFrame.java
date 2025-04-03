import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentFrame extends JPanel {
    private String username;
    private JTable purchaseTable;
    private DefaultTableModel purchaseModel;
    private JLabel purchaseIdLabel, totalLabel;
    private JRadioButton cashRadio, cardRadio;
    private JTextField cashField, cardField;
    private JLabel changeLabel;
    private JButton nextButton, backButton, addItemButton, deleteItemButton;
    private double totalAmount = 0;
    private boolean validPayment = false;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public PaymentFrame(String username) {
        this.username = username;
        setLayout(null);

        // --- Back Button ---
        backButton = new JButton("BACK");
        backButton.setBounds(20, 20, 100, 30);
        add(backButton);
        backButton.addActionListener(e -> {
            // Navigate back to Home or your main panel.
            JOptionPane.showMessageDialog(PaymentFrame.this, "Back to Home.");
        });

        // --- Purchase ID ---
        JLabel pidLabel = new JLabel("Purchase ID:");
        pidLabel.setBounds(140, 20, 100, 30);
        add(pidLabel);
        purchaseIdLabel = new JLabel();
        purchaseIdLabel.setBounds(240, 20, 100, 30);
        add(purchaseIdLabel);
        loadPurchaseId();

        // --- Table Model ---
        // Columns: Product ID, Product Name, Price (Rs), Quantity (with minus/plus)
        purchaseModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing for the Quantity column (index 3)
                return column == 3;
            }
        };
        purchaseModel.addColumn("Product ID");
        purchaseModel.addColumn("Product Name");
        purchaseModel.addColumn("Price (Rs)");
        purchaseModel.addColumn("Quantity");

        purchaseTable = new JTable(purchaseModel);
        purchaseTable.setRowHeight(32);
        // Ensure only one row can be selected at a time
        purchaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(purchaseTable);
        scrollPane.setBounds(20, 70, 550, 300);
        add(scrollPane);

        // --- Custom Renderer and Editor for the Quantity column ---
        purchaseTable.getColumnModel().getColumn(3).setCellRenderer(new QuantityCellRenderer());
        purchaseTable.getColumnModel().getColumn(3).setCellEditor(new QuantityCellEditor());

        // --- Dummy Data (Replace with your actual cart data) ---
        purchaseModel.addRow(new Object[]{"P001", "Apple", "100", "2"});
        purchaseModel.addRow(new Object[]{"P002", "Banana", "50", "3"});
        updateTotal();

        // --- Add and Delete Buttons ---
        addItemButton = new JButton("Add Item");
        addItemButton.setBounds(20, 380, 100, 30);
        add(addItemButton);

        deleteItemButton = new JButton("Delete Item");
        deleteItemButton.setBounds(130, 380, 110, 30);
        add(deleteItemButton);

        addItemButton.addActionListener(e -> addItem());
        deleteItemButton.addActionListener(e -> deleteSelectedItem());

        // --- Total Label ---
        totalLabel = new JLabel("Total: Rs " + df.format(totalAmount));
        totalLabel.setBounds(260, 380, 200, 30);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(totalLabel);

        // --- Payment Section ---
        JPanel paymentPanel = new JPanel(null);
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        paymentPanel.setBounds(600, 70, 460, 300);
        add(paymentPanel);

        // Payment method selection
        cashRadio = new JRadioButton("Cash");
        cashRadio.setBounds(20, 30, 100, 30);
        cardRadio = new JRadioButton("Card");
        cardRadio.setBounds(140, 30, 100, 30);
        ButtonGroup group = new ButtonGroup();
        group.add(cashRadio);
        group.add(cardRadio);
        paymentPanel.add(cashRadio);
        paymentPanel.add(cardRadio);

        // Cash payment components
        JLabel cashLabel = new JLabel("Enter cash amount (Rs):");
        cashLabel.setBounds(20, 70, 200, 30);
        paymentPanel.add(cashLabel);
        cashField = new JTextField();
        cashField.setBounds(220, 70, 100, 30);
        paymentPanel.add(cashField);
        JLabel changeTextLabel = new JLabel("Change (Rs):");
        changeTextLabel.setBounds(20, 110, 200, 30);
        paymentPanel.add(changeTextLabel);
        changeLabel = new JLabel();
        changeLabel.setBounds(220, 110, 100, 30);
        paymentPanel.add(changeLabel);

        // Card payment components
        JLabel cardLabel = new JLabel("Enter 4-digit PIN:");
        cardLabel.setBounds(20, 150, 200, 30);
        paymentPanel.add(cardLabel);
        cardField = new JTextField();
        cardField.setBounds(220, 150, 100, 30);
        paymentPanel.add(cardField);

        // Next button to complete payment
        nextButton = new JButton("NEXT");
        nextButton.setBounds(200, 220, 100, 30);
        paymentPanel.add(nextButton);

        // --- Input Validation Listeners ---
        cashField.addActionListener(e -> {
            try {
                double cashInput = Double.parseDouble(cashField.getText());
                if (cashInput < totalAmount) {
                    JOptionPane.showMessageDialog(PaymentFrame.this, "Insufficient cash amount.");
                    cashField.setText("");
                    changeLabel.setText("");
                    validPayment = false;
                } else {
                    double change = cashInput - totalAmount;
                    changeLabel.setText(df.format(change));
                    validPayment = true;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(PaymentFrame.this, "Invalid cash amount.");
                validPayment = false;
            }
        });

        cardField.addActionListener(e -> {
            String pin = cardField.getText();
            if (pin.length() != 4 || !pin.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(PaymentFrame.this, "PIN must be a 4-digit number.");
                cardField.setText("");
                validPayment = false;
            } else {
                validPayment = true;
            }
        });

        nextButton.addActionListener(e -> {
            if (cashRadio.isSelected() || cardRadio.isSelected()) {
                if (validPayment) {
                    completeTransaction();
                } else {
                    JOptionPane.showMessageDialog(PaymentFrame.this, "Payment details are not valid.");
                }
            } else {
                JOptionPane.showMessageDialog(PaymentFrame.this, "Please select a payment method.");
            }
        });
    }

    // Recalculate the total amount based on each row's quantity and price.
    private void updateTotal() {
        totalAmount = 0;
        for (int i = 0; i < purchaseModel.getRowCount(); i++) {
            try {
                double price = Double.parseDouble(purchaseModel.getValueAt(i, 2).toString());
                int qty = Integer.parseInt(purchaseModel.getValueAt(i, 3).toString());
                totalAmount += price * qty;
            } catch (Exception e) {
                // Skip any row with invalid data.
            }
        }
        if (totalLabel != null) {
            totalLabel.setText("Total: Rs " + df.format(totalAmount));
        }
    }

    // Prompt the user for new item details and add a new row.
    private void addItem() {
        JTextField pidField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField = new JTextField();
        Object[] message = {
            "Product ID:", pidField,
            "Product Name:", nameField,
            "Price (Rs):", priceField,
            "Quantity:", qtyField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String pid = pidField.getText().trim();
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String qtyStr = qtyField.getText().trim();
            if (pid.isEmpty() || name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }
            try {
                double price = Double.parseDouble(priceStr);
                int qty = Integer.parseInt(qtyStr);
                purchaseModel.addRow(new Object[]{pid, name, String.valueOf(price), String.valueOf(qty)});
                updateTotal();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Price/Quantity must be numeric!");
            }
        }
    }

    // Delete the currently selected row. If no row is selected, prompt the user.
    private void deleteSelectedItem() {
        int selectedRow = purchaseTable.getSelectedRow();
        if (selectedRow >= 0) {
            purchaseModel.removeRow(selectedRow);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        }
    }

    // Load the next purchase ID from the database (for demo purposes).
    private void loadPurchaseId() {
        int maxId = 0;
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost/market?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=UTC", 
                "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(purchaseId) as maxId FROM purchase");
            if (rs.next()) {
                maxId = rs.getInt("maxId");
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        purchaseIdLabel.setText(String.valueOf(maxId + 1));
    }

    // Complete the transaction by inserting purchase records and updating stock.
    private void completeTransaction() {
        String purchaseId = purchaseIdLabel.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String now = sdf.format(new Date());
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost/market?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=UTC", 
                "root", "");
            Statement stmt = con.createStatement();
            // Insert each purchase record into the database.
            for (int i = 0; i < purchaseModel.getRowCount(); i++) {
                String productId = purchaseModel.getValueAt(i, 0).toString();
                String priceStr = purchaseModel.getValueAt(i, 2).toString();
                String qtyStr = purchaseModel.getValueAt(i, 3).toString();
                String query = "INSERT INTO purchase VALUES ('" + purchaseId + "', '"
                        + productId + "', '" + qtyStr + "', '" + priceStr + "', '" + now + "')";
                stmt.execute(query);
            }
            // Update product stock accordingly.
            for (int i = 0; i < purchaseModel.getRowCount(); i++) {
                String productId = purchaseModel.getValueAt(i, 0).toString();
                String qtyStr = purchaseModel.getValueAt(i, 3).toString();
                String updateQuery = "UPDATE product SET stock = stock - " + qtyStr
                        + " WHERE productId = '" + productId + "'";
                stmt.execute(updateQuery);
            }
            con.close();
            JOptionPane.showMessageDialog(PaymentFrame.this, "Payment successful! Transaction completed.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(PaymentFrame.this, "Database error during transaction.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // Custom Renderer for the Quantity column (displays minus and plus icons with a label)
    // -------------------------------------------------------------------------
    private class QuantityCellRenderer extends JPanel implements TableCellRenderer {
        private JLabel quantityLabel;
        private JLabel minusIconLabel, plusIconLabel;
        public QuantityCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(true);
            // Replace these with your actual icon file paths or resource references.
            ImageIcon minusIcon = new ImageIcon("minus.png");
            ImageIcon plusIcon = new ImageIcon("plus.png");
            minusIconLabel = new JLabel(minusIcon);
            plusIconLabel = new JLabel(plusIcon);
            quantityLabel = new JLabel("0");
            add(minusIconLabel);
            add(quantityLabel);
            add(plusIconLabel);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(UIManager.getColor("Table.background"));
            }
            quantityLabel.setText((value == null) ? "0" : value.toString());
            return this;
        }
    }

    // -------------------------------------------------------------------------
    // Custom Editor for the Quantity column (allows interactive minus/plus)
    // -------------------------------------------------------------------------
    private class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JLabel quantityLabel;
        private JButton minusButton, plusButton;
        private int currentQuantity = 0;
        private int editingRow = -1;
        public QuantityCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            // Replace these with your actual icon file paths or resource references.
            ImageIcon minusIcon = new ImageIcon("minus.png");
            ImageIcon plusIcon = new ImageIcon("plus.png");
            minusButton = new JButton(minusIcon);
            plusButton = new JButton(plusIcon);
            minusButton.setFocusable(false);
            plusButton.setFocusable(false);
            quantityLabel = new JLabel("0");
            // Decrease quantity (not below 0)
            minusButton.addActionListener(e -> {
                if (currentQuantity > 0) {
                    currentQuantity--;
                    quantityLabel.setText(String.valueOf(currentQuantity));
                }
            });
            // Increase quantity
            plusButton.addActionListener(e -> {
                currentQuantity++;
                quantityLabel.setText(String.valueOf(currentQuantity));
            });
            panel.add(minusButton);
            panel.add(quantityLabel);
            panel.add(plusButton);
        }
        @Override
        public Object getCellEditorValue() {
            return String.valueOf(currentQuantity);
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            editingRow = row;
            try {
                currentQuantity = Integer.parseInt(value.toString());
            } catch (Exception e) {
                currentQuantity = 0;
            }
            quantityLabel.setText(String.valueOf(currentQuantity));
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(UIManager.getColor("Table.background"));
            }
            return panel;
        }
        @Override
        public boolean stopCellEditing() {
            int modelRow = editingRow;
            if (modelRow >= 0 && modelRow < purchaseModel.getRowCount()) {
                purchaseModel.setValueAt(String.valueOf(currentQuantity), modelRow, 3);
            }
            updateTotal();
            return super.stopCellEditing();
        }
    }

    // -------------------------------------------------------------------------
    // Test Main Method
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        JFrame testFrame = new JFrame("PaymentFrame with +/- Icons");
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(1100, 700);
        testFrame.setLocationRelativeTo(null);
        testFrame.add(new PaymentFrame("TestUser"));
        testFrame.setVisible(true);
    }
}