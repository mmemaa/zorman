import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class AuthService {
    private final DataSource ds;

    public AuthService(DataSource ds) {
        this.ds = ds;
    }

    public void createUser(String username, String password, String role) throws SQLException {
        String hash = hashPassword(password);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement("INSERT INTO users (username, password_hash, role) VALUES (?,?,?)");
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, role);
            ps.executeUpdate();
        } finally {
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    public boolean verify(String username, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement("SELECT password_hash FROM users WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                String stored = rs.getString(1);
                return verifyPassword(password, stored);
            }
            return false;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    // PBKDF2 helpers (salted)
    private static String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            byte[] hash = pbkdf2(password.toCharArray(), salt, 10000, 32);
            String sSalt = Base64.getEncoder().encodeToString(salt);
            String sHash = Base64.getEncoder().encodeToString(hash);
            return "pbkdf2$10000$" + sSalt + "$" + sHash;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean verifyPassword(String password, String stored) {
        try {
            if (stored == null) return false;
            if (!stored.startsWith("pbkdf2$")) return false;
            String[] parts = stored.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(password.toCharArray(), salt, iterations, expected.length);
            if (actual.length != expected.length) return false;
            int diff = 0;
            for (int i = 0; i < actual.length; i++) diff |= actual[i] ^ expected[i];
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        javax.crypto.SecretKeyFactory skf = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(password, salt, iterations, bytes * 8);
        byte[] res = skf.generateSecret(spec).getEncoded();
        return res;
    }
}
