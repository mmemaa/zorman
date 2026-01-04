import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartnerDAO {
    private final DataSource ds;

    public PartnerDAO(DataSource ds) {
        this.ds = ds;
    }

    public UUID insert(Partner p) throws SQLException {
        String sql = "INSERT INTO partnerji (maticna_stevilka, davcna_stevilka, naziv, kratki_naziv, pravna_oblika_id, status, dejavnost, naslov, posta_stevilka, kraj, drzava_id, telefonska_stevilka, elektronska_posta, spletna_stran, velikost_podjetja, je_davcni_zavezanec, stevilka_evidence_ddv, opombe) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            int idx = 1;
            ps.setString(idx++, p.getMaticnaStevilka());
            ps.setString(idx++, p.getDavcnaStevilka());
            ps.setString(idx++, p.getNaziv());
            ps.setString(idx++, p.getKratkiNaziv());
            ps.setString(idx++, p.getPravnaOblikaId());
            ps.setString(idx++, p.getStatus());
            ps.setString(idx++, p.getDejavnost());
            ps.setString(idx++, p.getNaslov());
            ps.setString(idx++, p.getPostaStevilka());
            ps.setString(idx++, p.getKraj());
            if (p.getDrzavaId() == null) ps.setNull(idx++, Types.INTEGER); else ps.setInt(idx++, p.getDrzavaId());
            ps.setString(idx++, p.getTelefonskaStevilka());
            ps.setString(idx++, p.getElektronskaPosta());
            ps.setString(idx++, p.getSpletnaStran());
            ps.setString(idx++, p.getVelikostPodjetja());
            if (p.getJeDavcniZavezanec() == null) ps.setNull(idx++, Types.BOOLEAN); else ps.setBoolean(idx++, p.getJeDavcniZavezanec());
            ps.setString(idx++, p.getStevilkaEvidenceDdv());
            ps.setString(idx++, p.getOpombe());

            rs = ps.executeQuery();
            if (rs.next()) {
                UUID id = (UUID) rs.getObject(1);
                p.setId(id);
                return id;
            } else {
                throw new SQLException("Insert failed, no ID obtained");
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    public Partner findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM partnerji WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
            return null;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    public List<Partner> findAll(int limit) throws SQLException {
        String sql = "SELECT * FROM partnerji ORDER BY created_at DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            List<Partner> res = new ArrayList<Partner>();
            while (rs.next()) res.add(mapRow(rs));
            return res;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    public void update(Partner p) throws SQLException {
        String sql = "UPDATE partnerji SET maticna_stevilka=?, davcna_stevilka=?, naziv=?, kratki_naziv=?, pravna_oblika_id=?, status=?, dejavnost=?, naslov=?, posta_stevilka=?, kraj=?, drzava_id=?, telefonska_stevilka=?, elektronska_posta=?, spletna_stran=?, velikost_podjetja=?, je_davcni_zavezanec=?, stevilka_evidence_ddv=?, opombe=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            int idx = 1;
            ps.setString(idx++, p.getMaticnaStevilka());
            ps.setString(idx++, p.getDavcnaStevilka());
            ps.setString(idx++, p.getNaziv());
            ps.setString(idx++, p.getKratkiNaziv());
            ps.setString(idx++, p.getPravnaOblikaId());
            ps.setString(idx++, p.getStatus());
            ps.setString(idx++, p.getDejavnost());
            ps.setString(idx++, p.getNaslov());
            ps.setString(idx++, p.getPostaStevilka());
            ps.setString(idx++, p.getKraj());
            if (p.getDrzavaId() == null) ps.setNull(idx++, Types.INTEGER); else ps.setInt(idx++, p.getDrzavaId());
            ps.setString(idx++, p.getTelefonskaStevilka());
            ps.setString(idx++, p.getElektronskaPosta());
            ps.setString(idx++, p.getSpletnaStran());
            ps.setString(idx++, p.getVelikostPodjetja());
            if (p.getJeDavcniZavezanec() == null) ps.setNull(idx++, Types.BOOLEAN); else ps.setBoolean(idx++, p.getJeDavcniZavezanec());
            ps.setString(idx++, p.getStevilkaEvidenceDdv());
            ps.setString(idx++, p.getOpombe());
            ps.setObject(idx++, p.getId());
            int updated = ps.executeUpdate();
            if (updated != 1) throw new SQLException("Update affected " + updated + " rows");
        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    public void delete(UUID id) throws SQLException {
        String sql = "DELETE FROM partnerji WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ps.executeUpdate();
        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private Partner mapRow(ResultSet rs) throws SQLException {
        Partner p = new Partner();
        p.setId((UUID) rs.getObject("id"));
        p.setMaticnaStevilka(rs.getString("maticna_stevilka"));
        p.setDavcnaStevilka(rs.getString("davcna_stevilka"));
        p.setNaziv(rs.getString("naziv"));
        p.setKratkiNaziv(rs.getString("kratki_naziv"));
        p.setPravnaOblikaId(rs.getString("pravna_oblika_id"));
        p.setStatus(rs.getString("status"));
        p.setDejavnost(rs.getString("dejavnost"));
        p.setNaslov(rs.getString("naslov"));
        p.setPostaStevilka(rs.getString("posta_stevilka"));
        p.setKraj(rs.getString("kraj"));
        int did = rs.getInt("drzava_id");
        if (rs.wasNull()) p.setDrzavaId(null); else p.setDrzavaId(new Integer(did));
        p.setTelefonskaStevilka(rs.getString("telefonska_stevilka"));
        p.setElektronskaPosta(rs.getString("elektronska_posta"));
        p.setSpletnaStran(rs.getString("spletna_stran"));
        p.setVelikostPodjetja(rs.getString("velikost_podjetja"));
        boolean ddv = rs.getBoolean("je_davcni_zavezanec");
        if (rs.wasNull()) p.setJeDavcniZavezanec(null); else p.setJeDavcniZavezanec(new Boolean(ddv));
        p.setStevilkaEvidenceDdv(rs.getString("stevilka_evidence_ddv"));
        p.setOpombe(rs.getString("opombe"));
        return p;
    }
}
