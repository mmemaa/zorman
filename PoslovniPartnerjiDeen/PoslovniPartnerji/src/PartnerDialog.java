import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartnerDialog extends JDialog {
    private PoslovniPartner partner;
    private boolean confirmed = false;

    private JTextField krajsiNazivField;
    private JTextField polniNazivField;
    private JTextField davcnaStevilkaField;
    private JTextField maticnaStevilkaField;
    private JComboBox<OblikaPodjetja> oblikaCombo;
    private JTextField ulicaField;
    private JTextField hisnaStevilkaField;
    private JTextField postnaStevilkaField;
    private JTextField krajField;
    private JTextField emailField;
    private JTextField telefonField;
    private JTextField spletnaStranField;
    private JComboBox<String> statusCombo;
    private JTextArea opombeArea;
    private User currentUser;

    public PartnerDialog(JFrame parent, PoslovniPartner partner, User currentUser) {
        super(parent, partner == null ? "Dodaj partnerja" : "Uredi partnerja", true);
        this.partner = partner;
        this.currentUser = currentUser;

        setSize(600, 700);
        setLocationRelativeTo(parent);

        initComponents();

        if (partner != null) {
            fillFields();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Krajši naziv
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Krajši naziv:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        krajsiNazivField = new JTextField();
        formPanel.add(krajsiNazivField, gbc);
        row++;

        // Polni naziv
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Polni naziv:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        polniNazivField = new JTextField();
        formPanel.add(polniNazivField, gbc);
        row++;

        // Davčna številka
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Davčna številka:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        davcnaStevilkaField = new JTextField();
        formPanel.add(davcnaStevilkaField, gbc);
        row++;

        // Matična številka
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Matična številka:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        maticnaStevilkaField = new JTextField();
        formPanel.add(maticnaStevilkaField, gbc);
        row++;

        // Oblika podjetja
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Oblika podjetja:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        oblikaCombo = new JComboBox<>();
        loadOblike();
        formPanel.add(oblikaCombo, gbc);
        row++;

        // Ulica
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Ulica:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        ulicaField = new JTextField();
        formPanel.add(ulicaField, gbc);
        row++;

        // Hišna številka
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Hišna številka:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        hisnaStevilkaField = new JTextField();
        formPanel.add(hisnaStevilkaField, gbc);
        row++;

        // Poštna številka
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Poštna številka:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        postnaStevilkaField = new JTextField();
        formPanel.add(postnaStevilkaField, gbc);
        row++;

        // Kraj
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Kraj:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        krajField = new JTextField();
        formPanel.add(krajField, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        emailField = new JTextField();
        formPanel.add(emailField, gbc);
        row++;

        // Telefon
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Telefon:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        telefonField = new JTextField();
        formPanel.add(telefonField, gbc);
        row++;

        // Spletna stran
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Spletna stran:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        spletnaStranField = new JTextField();
        formPanel.add(spletnaStranField, gbc);
        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        statusCombo = new JComboBox<>(new String[]{"aktiven", "neaktiven", "blokiran"});
        formPanel.add(statusCombo, gbc);
        row++;

        // Opombe
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Opombe:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        opombeArea = new JTextArea(4, 20);
        opombeArea.setLineWrap(true);
        opombeArea.setWrapStyleWord(true);
        JScrollPane opombeScroll = new JScrollPane(opombeArea);
        formPanel.add(opombeScroll, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Shrani");
        saveButton.addActionListener(e -> save());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Prekliči");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        getRootPane().setDefaultButton(saveButton);
    }

    private void loadOblike() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, kratica, polno_ime FROM oblike_podjetij ORDER BY kratica";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            oblikaCombo.addItem(null); // Empty option

            while (rs.next()) {
                OblikaPodjetja oblika = new OblikaPodjetja(
                    rs.getInt("id"),
                    rs.getString("kratica"),
                    rs.getString("polno_ime")
                );
                oblikaCombo.addItem(oblika);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri nalaganju oblik podjetij: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillFields() {
        krajsiNazivField.setText(partner.getKrajsiNaziv());
        polniNazivField.setText(partner.getPolniNaziv());
        davcnaStevilkaField.setText(partner.getDavcnaStevilka());
        maticnaStevilkaField.setText(partner.getMaticnaStevilka());
        ulicaField.setText(partner.getUlica());
        hisnaStevilkaField.setText(partner.getHisnaStevilka());
        postnaStevilkaField.setText(partner.getPostnaStevilka());
        krajField.setText(partner.getKraj());
        emailField.setText(partner.getEmail());
        telefonField.setText(partner.getTelefon());
        spletnaStranField.setText(partner.getSpletnaStran());
        statusCombo.setSelectedItem(partner.getStatus());
        opombeArea.setText(partner.getOpombe());

        // Select oblika
        for (int i = 0; i < oblikaCombo.getItemCount(); i++) {
            OblikaPodjetja oblika = oblikaCombo.getItemAt(i);
            if (oblika != null && oblika.getId() == partner.getOblikaId()) {
                oblikaCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void save() {
        // Validation
        if (krajsiNazivField.getText().trim().isEmpty() || polniNazivField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Krajši in polni naziv sta obvezna.",
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (partner == null) {
                // Insert
                sql = "INSERT INTO poslovni_partnerji (krajsi_naziv, polni_naziv, davcna_stevilka, " +
                      "maticna_stevilka, oblika_id, ulica, hisna_stevilka, postna_stevilka, kraj, " +
                      "email, telefon, spletna_stran, status, opombe) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
            } else {
                // Update
                sql = "UPDATE poslovni_partnerji SET krajsi_naziv = ?, polni_naziv = ?, " +
                      "davcna_stevilka = ?, maticna_stevilka = ?, oblika_id = ?, ulica = ?, " +
                      "hisna_stevilka = ?, postna_stevilka = ?, kraj = ?, email = ?, telefon = ?, " +
                      "spletna_stran = ?, status = ?, opombe = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(15, partner.getId());
            }

            stmt.setString(1, krajsiNazivField.getText().trim());
            stmt.setString(2, polniNazivField.getText().trim());
            stmt.setString(3, davcnaStevilkaField.getText().trim().isEmpty() ? null : davcnaStevilkaField.getText().trim());
            stmt.setString(4, maticnaStevilkaField.getText().trim().isEmpty() ? null : maticnaStevilkaField.getText().trim());

            OblikaPodjetja selectedOblika = (OblikaPodjetja) oblikaCombo.getSelectedItem();
            if (selectedOblika != null) {
                stmt.setInt(5, selectedOblika.getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, ulicaField.getText().trim().isEmpty() ? null : ulicaField.getText().trim());
            stmt.setString(7, hisnaStevilkaField.getText().trim().isEmpty() ? null : hisnaStevilkaField.getText().trim());
            stmt.setString(8, postnaStevilkaField.getText().trim().isEmpty() ? null : postnaStevilkaField.getText().trim());
            stmt.setString(9, krajField.getText().trim().isEmpty() ? null : krajField.getText().trim());
            stmt.setString(10, emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
            stmt.setString(11, telefonField.getText().trim().isEmpty() ? null : telefonField.getText().trim());
            stmt.setString(12, spletnaStranField.getText().trim().isEmpty() ? null : spletnaStranField.getText().trim());
            stmt.setString(13, (String) statusCombo.getSelectedItem());
            stmt.setString(14, opombeArea.getText().trim().isEmpty() ? null : opombeArea.getText().trim());

            if (partner == null) {
                // For INSERT, get the generated ID
                ResultSet rs = stmt.executeQuery();
                int newPartnerId = 0;
                if (rs.next()) {
                    newPartnerId = rs.getInt(1);
                }
            } else {
                // For UPDATE
                stmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                "Partner uspešno shranjen.",
                "Uspeh",
                JOptionPane.INFORMATION_MESSAGE);

            confirmed = true;
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Napaka pri shranjevanju: " + e.getMessage(),
                "Napaka",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
