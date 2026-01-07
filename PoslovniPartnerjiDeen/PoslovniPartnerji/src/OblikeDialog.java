import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OblikeDialog extends JDialog {
    private JTable oblikeTabele;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, closeButton;
    private User currentUser;

    private final String[] columnNames = {"ID", "Kratica", "Polno ime"};

    public OblikeDialog(JFrame parent, User currentUser) {
        super(parent, "Oblike podjetij", true);
        this.currentUser = currentUser;

        setSize(600, 400);
        setLocationRelativeTo(parent);

        initComponents();
        loadOblike();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Šifrant pravnih oblik podjetij", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        oblikeTabele = new JTable(tableModel);
        oblikeTabele.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(oblikeTabele);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton = new JButton("Dodaj");
        addButton.addActionListener(e -> addOblika());
        buttonPanel.add(addButton);

        editButton = new JButton("Uredi");
        editButton.addActionListener(e -> editOblika());
        buttonPanel.add(editButton);

        deleteButton = new JButton("Izbriši");
        deleteButton.addActionListener(e -> deleteOblika());
        buttonPanel.add(deleteButton);

        closeButton = new JButton("Zapri");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadOblike() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, kratica, polno_ime FROM oblike_podjetij ORDER BY kratica";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("kratica"),
                    rs.getString("polno_ime")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri nalaganju podatkov: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOblika() {
        JTextField kraticaField = new JTextField();
        JTextField polnoImeField = new JTextField();

        Object[] message = {
            "Kratica:", kraticaField,
            "Polno ime:", polnoImeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Dodaj obliko podjetja",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String kratica = kraticaField.getText().trim();
            String polnoIme = polnoImeField.getText().trim();

            if (kratica.isEmpty() || polnoIme.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vsa polja so obvezna.",
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO oblike_podjetij (kratica, polno_ime) VALUES (?, ?) RETURNING id";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, kratica);
                stmt.setString(2, polnoIme);
                ResultSet rs = stmt.executeQuery();

                int newOblikaId = 0;
                if (rs.next()) {
                    newOblikaId = rs.getInt(1);
                }

                JOptionPane.showMessageDialog(this,
                    "Oblika podjetja uspešno dodana.",
                    "Uspeh",
                    JOptionPane.INFORMATION_MESSAGE);
                loadOblike();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Napaka pri dodajanju: " + e.getMessage(),
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editOblika() {
        int selectedRow = oblikeTabele.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Prosim izberite obliko za urejanje.",
                "Opozorilo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String currentKratica = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPolnoIme = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField kraticaField = new JTextField(currentKratica);
        JTextField polnoImeField = new JTextField(currentPolnoIme);

        Object[] message = {
            "Kratica:", kraticaField,
            "Polno ime:", polnoImeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Uredi obliko podjetja",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String kratica = kraticaField.getText().trim();
            String polnoIme = polnoImeField.getText().trim();

            if (kratica.isEmpty() || polnoIme.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vsa polja so obvezna.",
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE oblike_podjetij SET kratica = ?, polno_ime = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, kratica);
                stmt.setString(2, polnoIme);
                stmt.setInt(3, id);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Oblika podjetja uspešno posodobljena.",
                    "Uspeh",
                    JOptionPane.INFORMATION_MESSAGE);
                loadOblike();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Napaka pri posodabljanju: " + e.getMessage(),
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteOblika() {
        int selectedRow = oblikeTabele.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Prosim izberite obliko za brisanje.",
                "Opozorilo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String kratica = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Ali ste prepričani, da želite izbrisati obliko:\n" + kratica + "?",
            "Potrditev brisanja",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM oblike_podjetij WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Oblika podjetja uspešno izbrisana.",
                    "Uspeh",
                    JOptionPane.INFORMATION_MESSAGE);
                loadOblike();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Napaka pri brisanju: " + e.getMessage() +
                    "\nMorda je ta oblika že v uporabi pri partnerjih.",
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
