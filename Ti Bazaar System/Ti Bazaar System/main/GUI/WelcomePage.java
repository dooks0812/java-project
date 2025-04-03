import java.awt.*;
import javax.swing.*;

public class WelcomePage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JLabel titleLabel;
    private JButton userLoginButton;
    private JButton userRegisterButton;
    private JLabel backgroundLabel;
    private JPanel titlePanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                WelcomePage frame = new WelcomePage();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public WelcomePage() {
        setTitle("Ti Bazaar - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        setLayout(null);
        setLocationRelativeTo(null);

        // Background Image
        ImageIcon background = new ImageIcon(new ImageIcon("../img/register1.jpg").getImage()
                .getScaledInstance(800, 500, Image.SCALE_SMOOTH));
        backgroundLabel = new JLabel(background);
        backgroundLabel.setBounds(0, 0, 800, 500);
        setContentPane(backgroundLabel);
        backgroundLabel.setLayout(null);

        // Title Label
        titleLabel = new JLabel("Bonzour, Bienvenue to Ti Bazaar !");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(150, 70, 500, 40);
        backgroundLabel.add(titleLabel);

        // Blurred Round Panel for Title with more rounded corners
        titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Increased corner radius from 25 to 40 for more rounded look
                g2.setColor(new Color(156, 146, 146,100)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 55, 55);
                
                g2.dispose();
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setBounds(140, 60, 515, 60);
        backgroundLabel.add(titlePanel);

        // User Login Button
        userLoginButton = createRoundedButton("Login", 310, 200);
        // User Register Button
        userRegisterButton = createRoundedButton("Register", 310, 280);

        // Button Actions
        userLoginButton.addActionListener(e -> {
            dispose();
            new LoginGui().setVisible(true);
        });

        userRegisterButton.addActionListener(e -> {
            dispose();
            new RegistrationGui().setVisible(true);
        });
    }

    private JButton createRoundedButton(String text, int x, int y) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        styleButton(button, new Color(76, 76, 76));
        button.setBounds(x, y, 180, 50);
        backgroundLabel.add(button);
        return button;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Tahoma", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 100, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
}