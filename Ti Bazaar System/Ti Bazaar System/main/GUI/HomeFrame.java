
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class HomeFrame extends JFrame implements MenuListener {

    private String username;
    private JPanel mainPanel;
    private JPanel productPanel;
    private JPanel categoryPanel;
    private JMenu homeMenu, productMenu, categoryMenu;
    private JTable productTable;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    HomeFrame frame = new HomeFrame("Guest");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public HomeFrame(String username) {
        this.username = username;
        setTitle("Ti Bazar - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 0, 1100, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setLayout(new BorderLayout());

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        homeMenu = new JMenu("Home");
        productMenu = new JMenu("Products");
        categoryMenu = new JMenu("Categories");

        homeMenu.addMenuListener(this);
        productMenu.addMenuListener(this);
        categoryMenu.addMenuListener(this);

        menuBar.add(homeMenu);
        menuBar.add(productMenu);
        menuBar.add(categoryMenu);
        setJMenuBar(menuBar);

        // Main Panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        add(mainPanel, BorderLayout.CENTER);

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to Ti Bazaar, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Product Panel
        productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout());
        mainPanel.add(productPanel, BorderLayout.CENTER);

        // Product Table
        String[] columnNames = {"Product ID", "Product Name", "Category", "Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(productTable);
        productPanel.add(scrollPane, BorderLayout.CENTER);

        // Fetch Products from Database
        loadProducts();
    }

    public HomeFrame() {
        //TODO Auto-generated constructor stub
    }

    /**
     * Load products from the database into the table
     */
    private void loadProducts() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tibazaar", "root", "");
            String query = "SELECT * FROM products";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price")
                });
            }
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while loading products!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Handle menu navigation
     */
    @Override
    public void menuSelected(MenuEvent e) {
        if (e.getSource() == homeMenu) {
            JOptionPane.showMessageDialog(this, "You are already on the Home Page.");
        } else if (e.getSource() == productMenu) {
            JOptionPane.showMessageDialog(this, "Navigating to Product Page...");
            // Open Product Page
        } else if (e.getSource() == categoryMenu) {
            JOptionPane.showMessageDialog(this, "Navigating to Categories...");
            // Open Categories Page
        }
    }

    @Override
    public void menuDeselected(MenuEvent e) {}

    @Override
    public void menuCanceled(MenuEvent e) {}
}
