import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private User currentUser;
    private JTable partnerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;
    private JButton addButton, editButton, deleteButton, refreshButton, oblikeButton;

    private final String[] columnNames = {
        "ID", "Krajši naziv", "Polni naziv", "Davčna št.", "Matična št.",
        "Oblika", "Naslov", "Kraj", "Email", "Telefon", "Status"
    };

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Poslovni Partnerji - " + user.getIme() + " " + user.getPriimek() + " (" + user.getVloga() + ")");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadPartners();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with search and user info
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Iskanje:"));
        searchField = new JTextField(30);
        searchField.addActionListener(e -> filterTable());
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Išči");
        searchButton.addActionListener(e -> filterTable());
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Prijavljen: " + currentUser.getIme() + " " + currentUser.getPriimek());
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(userLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        partnerTable = new JTable(tableModel);
        partnerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        partnerTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(partnerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addButton = new JButton("Dodaj");
        addButton.addActionListener(e -> addPartner());
        buttonPanel.add(addButton);

        editButton = new JButton("Uredi");
        editButton.addActionListener(e -> editPartner());
        buttonPanel.add(editButton);

        deleteButton = new JButton("Izbriši");
        deleteButton.addActionListener(e -> deletePartner());
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("Osveži");
        refreshButton.addActionListener(e -> loadPartners());
        buttonPanel.add(refreshButton);

        // Admin only
        if (currentUser.isAdmin()) {
            oblikeButton = new JButton("Oblike podjetij");
            oblikeButton.addActionListener(e -> openOblikeDialog());
            buttonPanel.add(oblikeButton);

            JButton dejavnostiButton = new JButton("Dnevnik dejavnosti");
            dejavnostiButton.addActionListener(e -> openDejavnostiDialog());
            buttonPanel.add(dejavnostiButton);
        }

        JButton logoutButton = new JButton("Odjava");
        logoutButton.addActionListener(e -> logout());
        buttonPanel.add(logoutButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);

        statusLabel = new JLabel("Pripravljeno");
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPartners() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, o.kratica FROM poslovni_partnerji p " +
                        "LEFT JOIN oblike_podjetij o ON p.oblika_id = o.id " +
                        "ORDER BY p.id";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int count = 0;
            while (rs.next()) {
                String naslov = (rs.getString("ulica") != null ? rs.getString("ulica") : "") +
                               (rs.getString("hisna_stevilka") != null ? " " + rs.getString("hisna_stevilka") : "");

                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("krajsi_naziv"),
                    rs.getString("polni_naziv"),
                    rs.getString("davcna_stevilka"),
                    rs.getString("maticna_stevilka"),
                    rs.getString("kratica"),
                    naslov.trim(),
                    (rs.getString("postna_stevilka") != null ? rs.getString("postna_stevilka") + " " : "") +
                    (rs.getString("kraj") != null ? rs.getString("kraj") : ""),
                    rs.getString("email"),
                    rs.getString("telefon"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
                count++;
            }

            statusLabel.setText("Število zapisov: " + count);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri nalaganju podatkov: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            loadPartners();
            return;
        }

        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, o.kratica FROM poslovni_partnerji p " +
                        "LEFT JOIN oblike_podjetij o ON p.oblika_id = o.id " +
                        "WHERE LOWER(p.krajsi_naziv) LIKE ? OR LOWER(p.polni_naziv) LIKE ? " +
                        "OR LOWER(p.davcna_stevilka) LIKE ? OR LOWER(p.maticna_stevilka) LIKE ? " +
                        "OR LOWER(p.email) LIKE ? OR LOWER(p.telefon) LIKE ? OR LOWER(p.kraj) LIKE ? " +
                        "ORDER BY p.id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            String pattern = "%" + searchText + "%";
            for (int i = 1; i <= 7; i++) {
                stmt.setString(i, pattern);
            }

            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                String naslov = (rs.getString("ulica") != null ? rs.getString("ulica") : "") +
                               (rs.getString("hisna_stevilka") != null ? " " + rs.getString("hisna_stevilka") : "");

                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("krajsi_naziv"),
                    rs.getString("polni_naziv"),
                    rs.getString("davcna_stevilka"),
                    rs.getString("maticna_stevilka"),
                    rs.getString("kratica"),
                    naslov.trim(),
                    (rs.getString("postna_stevilka") != null ? rs.getString("postna_stevilka") + " " : "") +
                    (rs.getString("kraj") != null ? rs.getString("kraj") : ""),
                    rs.getString("email"),
                    rs.getString("telefon"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
                count++;
            }

            statusLabel.setText("Najdenih: " + count);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri iskanju: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPartner() {
        PartnerDialog dialog = new PartnerDialog(this, null, currentUser);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadPartners();
        }
    }

    private void editPartner() {
        int selectedRow = partnerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Prosim izberite partnerja za urejanje.",
                "Opozorilo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int partnerId = (int) tableModel.getValueAt(selectedRow, 0);
        PoslovniPartner partner = getPartnerById(partnerId);

        if (partner != null) {
            PartnerDialog dialog = new PartnerDialog(this, partner, currentUser);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadPartners();
            }
        }
    }

    private void deletePartner() {
        int selectedRow = partnerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Prosim izberite partnerja za brisanje.",
                "Opozorilo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int partnerId = (int) tableModel.getValueAt(selectedRow, 0);
        String naziv = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Ali ste prepričani, da želite izbrisati partnerja:\n" + naziv + "?",
            "Potrditev brisanja",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM poslovni_partnerji WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, partnerId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Partner uspešno izbrisan.",
                    "Uspeh",
                    JOptionPane.INFORMATION_MESSAGE);
                loadPartners();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Napaka pri brisanju: " + e.getMessage(),
                    "Napaka",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private PoslovniPartner getPartnerById(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT p.*, o.kratica FROM poslovni_partnerji p " +
                        "LEFT JOIN oblike_podjetij o ON p.oblika_id = o.id " +
                        "WHERE p.id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PoslovniPartner partner = new PoslovniPartner();
                partner.setId(rs.getInt("id"));
                partner.setKrajsiNaziv(rs.getString("krajsi_naziv"));
                partner.setPolniNaziv(rs.getString("polni_naziv"));
                partner.setDavcnaStevilka(rs.getString("davcna_stevilka"));
                partner.setMaticnaStevilka(rs.getString("maticna_stevilka"));
                partner.setOblikaId(rs.getInt("oblika_id"));
                partner.setOblikaKratica(rs.getString("kratica"));
                partner.setUlica(rs.getString("ulica"));
                partner.setHisnaStevilka(rs.getString("hisna_stevilka"));
                partner.setPostnaStevilka(rs.getString("postna_stevilka"));
                partner.setKraj(rs.getString("kraj"));
                partner.setEmail(rs.getString("email"));
                partner.setTelefon(rs.getString("telefon"));
                partner.setSpletnaStran(rs.getString("spletna_stran"));
                partner.setStatus(rs.getString("status"));
                partner.setOpombe(rs.getString("opombe"));
                return partner;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri pridobivanju podatkov: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void openOblikeDialog() {
        OblikeDialog dialog = new OblikeDialog(this, currentUser);
        dialog.setVisible(true);
    }

    private void openDejavnostiDialog() {
        DejavnostiDialog dialog = new DejavnostiDialog(this);
        dialog.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Ali se želite odjaviti?",
            "Odjava",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
                dispose();
            });
        }
    }
}
