
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.File;
import javax.imageio.ImageIO;

public class RegistrationGui extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField firstname, lastname, email, username, mob;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton btnRegister, btnLogin;
    private Image bgImage;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                RegistrationGui frame = new RegistrationGui();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public RegistrationGui() {
        setTitle("Ti Bazaar - Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(250, 100, 890, 530); 
        setResizable(false);

        // Load background image
        try {
            bgImage = ImageIO.read(new File("../img/register.jpg")); 
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
                }
            }
        };
        
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblRegister = new JLabel("Ti Bazaar - Registration");
        lblRegister.setFont(new Font("Arial", Font.BOLD, 24));
        lblRegister.setBounds(300, 40, 300, 30); // Moved down
        contentPane.add(lblRegister);

        // Column 1 - Left side
        JLabel lblFirstName = new JLabel("First Name:");
        lblFirstName.setFont(new Font("Arial", Font.BOLD, 15));
        lblFirstName.setBounds(50, 110, 120, 25);
        contentPane.add(lblFirstName);

        JLabel lblLastName = new JLabel("Last Name:");
        lblLastName.setFont(new Font("Arial", Font.BOLD, 15));
        lblLastName.setBounds(50, 160, 120, 25);
        contentPane.add(lblLastName);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 15));
        lblEmail.setBounds(50, 210, 120, 25);
        contentPane.add(lblEmail);
        
        JLabel lblMobile = new JLabel("Mobile:");
        lblMobile.setFont(new Font("Arial", Font.BOLD, 15));
        lblMobile.setBounds(50, 260, 120, 25);
        contentPane.add(lblMobile);

        firstname = new JTextField();
        firstname.setBounds(160, 110, 200, 30); 
        contentPane.add(firstname);

        lastname = new JTextField();
        lastname.setBounds(160, 160, 200, 30);
        contentPane.add(lastname);
        
        email = new JTextField();
        email.setBounds(160, 210, 200, 30);
        contentPane.add(email);
        
        mob = new JTextField();
        mob.setBounds(160, 260, 200, 30);
        contentPane.add(mob);

        // Column 2 - Right side
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 15));
        lblUsername.setBounds(470, 110, 120, 25);
        contentPane.add(lblUsername);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 15));
        lblPassword.setBounds(470, 160, 120, 25);
        contentPane.add(lblPassword);

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setFont(new Font("Arial", Font.BOLD, 15));
        lblConfirmPassword.setBounds(470, 210, 150, 25);
        contentPane.add(lblConfirmPassword);

        username = new JTextField();
        username.setBounds(630, 110, 200, 30);
        contentPane.add(username);

        passwordField = new JPasswordField();
        passwordField.setBounds(630, 160, 200, 30);
        contentPane.add(passwordField);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(630, 210, 200, 30);
        contentPane.add(confirmPasswordField);

        // Register Button
        btnRegister = new JButton("Register");
        btnRegister.setBounds (450, 340, 150, 40); 
        contentPane.add(btnRegister);

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser(); // Call function registerUser(), the one below
            }
        });

        // Login Button
        btnLogin = new JButton("Go to Login");
        btnLogin.setBounds(250, 340, 150, 40);
        contentPane.add(btnLogin);

        btnLogin.addActionListener(e -> {
            new LoginGui().setVisible(true); //Open LoginGui.java
            dispose(); // Closes current window
        });

    }

    private void registerUser() {
        String firstName = firstname.getText();
        String lastName = lastname.getText();
        String emailText = email.getText();
        String userName = username.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String mobileNumber = mob.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || emailText.isEmpty() || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || mobileNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mobileNumber.length() != 8) {
            JOptionPane.showMessageDialog(this, "Enter a valid 8-digit mobile number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tibazaar", "root", "");

            String query = "INSERT INTO users (first_name, last_name, username, password, email, mobile) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, userName);
            pst.setString(4, password);
            pst.setString(5, emailText);
            pst.setString(6, mobileNumber);

            int inserted = pst.executeUpdate(); //Execute Insert

            if (inserted > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful!\n\nWelcome, " + firstName + " " + lastName + 
               "!\nClick 'Go to Login' to proceed.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
