import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;


public class LoginGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel titleLabel;
    private JLabel backgroundLabel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginGui frame = new LoginGui();
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
    public LoginGui() {
        setTitle("Ti Bazaar - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 0, 1100, 700);
        setLayout(null);
    
        // Set Background Image (using fixed dimensions matching setBounds)
        ImageIcon background = new ImageIcon(new ImageIcon("../img/login.jpg").getImage().getScaledInstance(1100, 700, Image.SCALE_SMOOTH));
        backgroundLabel = new JLabel(background);
        backgroundLabel.setBounds(0, 0, 1100, 700);
        setContentPane(backgroundLabel);
        backgroundLabel.setLayout(null);
    

        // Title
        titleLabel = new JLabel("Ti Bazaar - Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setBounds(450, 30, 273, 93);
        backgroundLabel.add(titleLabel);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 18));
        lblUsername.setBounds(350, 170, 100, 40);
        backgroundLabel.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 30));
        usernameField.setBounds(481, 170, 281, 40);
        backgroundLabel.add(usernameField);
        usernameField.setColumns(10);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 18));
        lblPassword.setBounds(350, 240, 100, 40);
        backgroundLabel.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 30));
        passwordField.setBounds(481, 240, 281, 40);
        backgroundLabel.add(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Tahoma", Font.PLAIN, 26));
        loginButton.setBounds(560, 310, 100, 40);
        backgroundLabel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
    }

    /**
     * Method to handle login logic
     */
    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tibazaar", "root", "");
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose(); // Close login window
                new HomeFrame().setVisible(true); // Open main dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
