import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfig {
    private static DataSource ds;

    public static DataSource getDataSource() {
        if (ds == null) {
            Properties p = new Properties();
            InputStream is = null;
            try {
                is = DBConfig.class.getResourceAsStream("/db.properties");
                if (is != null) p.load(is);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load DB config", e);
            } finally {
                if (is != null) {
                    try { is.close(); } catch (IOException ignored) {}
                }
            }

            final String url = p.getProperty("jdbc.url", "jdbc:postgresql://localhost:5432/partnersdb");
            final String user = p.getProperty("jdbc.user", "postgres");
            final String pass = p.getProperty("jdbc.password", "postgres");

            ds = new DataSource() {
                public Connection getConnection() throws SQLException { return DriverManager.getConnection(url, user, pass); }
                public Connection getConnection(String username, String password) throws SQLException { return DriverManager.getConnection(url, username, password); }
                // The following operations are optional and not needed for this simple demo.
                public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
                public boolean isWrapperFor(Class<?> iface) { return false; }
                public java.io.PrintWriter getLogWriter() { throw new UnsupportedOperationException(); }
                public void setLogWriter(java.io.PrintWriter out) { throw new UnsupportedOperationException(); }
                public void setLoginTimeout(int seconds) { throw new UnsupportedOperationException(); }
                public int getLoginTimeout() { throw new UnsupportedOperationException(); }
                public java.util.logging.Logger getParentLogger() { throw new UnsupportedOperationException(); }
            };
        }
        return ds;
    }

    public static void close() {
        // No-op for simple DataSource
    }
}
