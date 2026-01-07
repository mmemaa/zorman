import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class DejavnostiDialog extends JDialog {
    private JTable dejavnostiTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterAkcijaCombo;
    private JComboBox<String> filterEntitetaCombo;
    private JComboBox<String> filterUporabnikCombo;
    private JButton refreshButton, closeButton;

    private final String[] columnNames = {
        "ID", "Datum/Čas", "Uporabnik", "Akcija", "Entiteta", "Opis"
    };

    public DejavnostiDialog(JFrame parent) {
        super(parent, "Dnevnik dejavnosti", true);

        setSize(1000, 600);
        setLocationRelativeTo(parent);

        initComponents();
        loadDejavnosti();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Dnevnik uporabniških dejavnosti", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtri"));

        filterPanel.add(new JLabel("Uporabnik:"));
        filterUporabnikCombo = new JComboBox<>();
        filterUporabnikCombo.addItem("Vsi");
        loadUporabniki();
        filterUporabnikCombo.addActionListener(e -> filterDejavnosti());
        filterPanel.add(filterUporabnikCombo);

        filterPanel.add(new JLabel("Akcija:"));
        filterAkcijaCombo = new JComboBox<>(new String[]{
            "Vse", "DODAJANJE", "UREJANJE", "BRISANJE", "PRIJAVA", "ODJAVA"
        });
        filterAkcijaCombo.addActionListener(e -> filterDejavnosti());
        filterPanel.add(filterAkcijaCombo);

        filterPanel.add(new JLabel("Entiteta:"));
        filterEntitetaCombo = new JComboBox<>(new String[]{
            "Vse", "POSLOVNI_PARTNER", "OBLIKA_PODJETJA", "UPORABNIK", "SISTEM"
        });
        filterEntitetaCombo.addActionListener(e -> filterDejavnosti());
        filterPanel.add(filterEntitetaCombo);

        add(filterPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dejavnostiTable = new JTable(tableModel);
        dejavnostiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dejavnostiTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        dejavnostiTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        dejavnostiTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        dejavnostiTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        dejavnostiTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        dejavnostiTable.getColumnModel().getColumn(5).setPreferredWidth(400);

        // Add double-click listener for details
        dejavnostiTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showDejavnostDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(dejavnostiTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        refreshButton = new JButton("Osveži");
        refreshButton.addActionListener(e -> loadDejavnosti());
        buttonPanel.add(refreshButton);

        JButton detailsButton = new JButton("Podrobnosti");
        detailsButton.addActionListener(e -> showDejavnostDetails());
        buttonPanel.add(detailsButton);

        closeButton = new JButton("Zapri");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUporabniki() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, uporabnisko_ime, ime, priimek FROM uporabniki ORDER BY uporabnisko_ime";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String display = rs.getString("uporabnisko_ime") + " (" +
                               rs.getString("ime") + " " + rs.getString("priimek") + ")";
                filterUporabnikCombo.addItem(display);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDejavnosti() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT d.id, d.created_at, u.uporabnisko_ime, u.ime, u.priimek, " +
                        "d.akcija, d.entiteta, d.opis " +
                        "FROM dejavnosti d " +
                        "JOIN uporabniki u ON d.uporabnik_id = u.id " +
                        "ORDER BY d.created_at DESC " +
                        "LIMIT 1000";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("created_at");
                String datum = timestamp != null ? sdf.format(timestamp) : "";

                String uporabnik = rs.getString("uporabnisko_ime") + " (" +
                                 rs.getString("ime") + " " + rs.getString("priimek") + ")";

                Object[] row = {
                    rs.getInt("id"),
                    datum,
                    uporabnik,
                    rs.getString("akcija"),
                    rs.getString("entiteta"),
                    rs.getString("opis")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri nalaganju dejavnosti: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterDejavnosti() {
        tableModel.setRowCount(0);

        String selectedUporabnik = (String) filterUporabnikCombo.getSelectedItem();
        String selectedAkcija = (String) filterAkcijaCombo.getSelectedItem();
        String selectedEntiteta = (String) filterEntitetaCombo.getSelectedItem();

        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder sql = new StringBuilder(
                "SELECT d.id, d.created_at, u.uporabnisko_ime, u.ime, u.priimek, " +
                "d.akcija, d.entiteta, d.opis " +
                "FROM dejavnosti d " +
                "JOIN uporabniki u ON d.uporabnik_id = u.id " +
                "WHERE 1=1"
            );

            if (selectedUporabnik != null && !selectedUporabnik.equals("Vsi")) {
                String uporabniskoIme = selectedUporabnik.substring(0, selectedUporabnik.indexOf(" ("));
                sql.append(" AND u.uporabnisko_ime = '").append(uporabniskoIme).append("'");
            }

            if (!selectedAkcija.equals("Vse")) {
                sql.append(" AND d.akcija = '").append(selectedAkcija).append("'");
            }

            if (!selectedEntiteta.equals("Vse")) {
                sql.append(" AND d.entiteta = '").append(selectedEntiteta).append("'");
            }

            sql.append(" ORDER BY d.created_at DESC LIMIT 1000");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("created_at");
                String datum = timestamp != null ? sdf.format(timestamp) : "";

                String uporabnik = rs.getString("uporabnisko_ime") + " (" +
                                 rs.getString("ime") + " " + rs.getString("priimek") + ")";

                Object[] row = {
                    rs.getInt("id"),
                    datum,
                    uporabnik,
                    rs.getString("akcija"),
                    rs.getString("entiteta"),
                    rs.getString("opis")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri filtriranju: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDejavnostDetails() {
        int selectedRow = dejavnostiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Prosim izberite dejavnost za podrobnosti.",
                "Opozorilo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int dejavnostId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT d.*, u.uporabnisko_ime, u.ime, u.priimek " +
                        "FROM dejavnosti d " +
                        "JOIN uporabniki u ON d.uporabnik_id = u.id " +
                        "WHERE d.id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, dejavnostId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

                StringBuilder details = new StringBuilder();
                details.append("ID: ").append(rs.getInt("id")).append("\n\n");
                details.append("Datum/Čas: ").append(sdf.format(rs.getTimestamp("created_at"))).append("\n\n");
                details.append("Uporabnik: ").append(rs.getString("uporabnisko_ime"))
                       .append(" (").append(rs.getString("ime")).append(" ")
                       .append(rs.getString("priimek")).append(")\n\n");
                details.append("Akcija: ").append(rs.getString("akcija")).append("\n\n");
                details.append("Entiteta: ").append(rs.getString("entiteta")).append("\n\n");

                int entitetaId = rs.getInt("entiteta_id");
                if (!rs.wasNull()) {
                    details.append("Entiteta ID: ").append(entitetaId).append("\n\n");
                }

                details.append("Opis: ").append(rs.getString("opis")).append("\n\n");

                String stareVrednosti = rs.getString("stare_vrednosti");
                if (stareVrednosti != null && !stareVrednosti.isEmpty()) {
                    details.append("Stare vrednosti:\n").append(stareVrednosti).append("\n\n");
                }

                String noveVrednosti = rs.getString("nove_vrednosti");
                if (noveVrednosti != null && !noveVrednosti.isEmpty()) {
                    details.append("Nove vrednosti:\n").append(noveVrednosti).append("\n\n");
                }

                String ipNaslov = rs.getString("ip_naslov");
                if (ipNaslov != null && !ipNaslov.isEmpty()) {
                    details.append("IP naslov: ").append(ipNaslov).append("\n");
                }

                JTextArea textArea = new JTextArea(details.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane,
                    "Podrobnosti dejavnosti", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri pridobivanju podrobnosti: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

