import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Prijava - Poslovni Partnerji");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Prijava v sistem", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(2, 2, 10, 10));

        formPanel.add(new JLabel("Uporabniško ime:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Geslo:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Prijava");
        loginButton.addActionListener(e -> login());
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter key support
        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Prosim vnesite uporabniško ime in geslo.",
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, uporabnisko_ime, ime, priimek, email, vloga FROM uporabniki " +
                        "WHERE uporabnisko_ime = ? AND geslo = ? AND aktiven = true";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("uporabnisko_ime"),
                    rs.getString("ime"),
                    rs.getString("priimek"),
                    rs.getString("email"),
                    rs.getString("vloga")
                );

                // Update last login
                String updateSql = "UPDATE uporabniki SET zadnja_prijava = CURRENT_TIMESTAMP WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, user.getId());
                updateStmt.executeUpdate();

                // Open main window
                SwingUtilities.invokeLater(() -> {
                    new MainFrame(user).setVisible(true);
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(this,
                    "Napačno uporabniško ime ali geslo.",
                    "Napaka pri prijavi",
                    JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri povezavi z bazo: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
