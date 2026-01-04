import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        System.out.println("Evidenca poslovnih partnerjev - demo");
        System.out.println("\nUsage:\n  java -jar app.jar init-db        # create schema in configured Postgres DB\n  java -jar app.jar demo           # try a small DB demo (insert/select)\n  java -jar app.jar create-admin <username> <password>  # create an ADMIN user\n");

        String cmd = args != null && args.length > 0 ? args[0] : "help";
        try {
            if ("init-db".equalsIgnoreCase(cmd)) {
                initDb();
            } else if ("demo".equalsIgnoreCase(cmd)) {
                runDemo();
            } else if ("create-admin".equalsIgnoreCase(cmd)) {
                // create admin user: args[1]=username, args[2]=password
                if (args.length < 3) {
                    System.out.println("Usage: create-admin <username> <password>");
                } else {
                    String username = args[1];
                    String password = args[2];
                    javax.sql.DataSource ds = DBConfig.getDataSource();
                    AuthService auth = new AuthService(ds);
                    try {
                        auth.createUser(username, password, "ADMIN");
                        System.out.println("Admin user created: " + username);
                    } catch (Exception e) {
                        System.err.println("Failed to create admin: " + e.getMessage());
                        e.printStackTrace(System.err);
                    }
                }
            } else {
                System.out.println("No action specified. Run with 'init-db' or 'demo'.");
            }
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static void initDb() throws Exception {
        System.out.println("Initializing database using db/schema.sql (PostgreSQL)...");
        Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            javax.sql.DataSource ds = DBConfig.getDataSource();
            conn = ds.getConnection();

            InputStream is = Main.class.getResourceAsStream("/db/schema.sql");
            if (is == null) {
                throw new IllegalStateException("Could not find /db/schema.sql on the classpath");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            try { reader.close(); } catch (Exception ignored) {}
            try { is.close(); } catch (Exception ignored) {}

            String sql = sb.toString();
            java.util.List<String> statements = splitSqlStatements(sql);
            for (int i = 0; i < statements.size(); i++) {
                String s = statements.get(i).trim();
                if (s.length() == 0) continue;
                try {
                    stmt = conn.createStatement();
                    stmt.execute(s);
                } finally {
                    if (stmt != null) {
                        try { stmt.close(); } catch (Exception ignored) {}
                        stmt = null;
                    }
                }
            }
            System.out.println("Schema executed successfully.");
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (Exception ignored) {}
            }
            if (conn != null) {
                try { conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    private static void runDemo() throws SQLException {
        System.out.println("Trying to connect to the database and run a simple demo...");
        java.sql.Connection conn = null;
        try {
            javax.sql.DataSource ds = DBConfig.getDataSource();
            conn = ds.getConnection();
            System.out.println("Connected to DB: " + conn.getMetaData().getURL());
            PartnerDAO dao = new PartnerDAO(ds);

            Partner p = new Partner();
            p.setMaticnaStevilka("99999999");
            p.setDavcnaStevilka("11112222");
            p.setNaziv("Demo podjetje d.o.o.");
            p.setKratkiNaziv("Demo");
            p.setPravnaOblikaId("D.O.O.");
            p.setStatus("Aktiven");
            p.setDejavnost("Demo dejavnost");
            p.setNaslov("Ulica 1");
            p.setPostaStevilka("1000");
            p.setKraj("Ljubljana");
            p.setDrzavaId(new Integer(1));
            p.setTelefonskaStevilka("+38640123456");
            p.setElektronskaPosta("demo@example.com");
            p.setVelikostPodjetja("Mikro");
            p.setJeDavcniZavezanec(Boolean.TRUE);
            p.setStevilkaEvidenceDdv("SI-Demo-1");
            p.setOpombe("Vnos iz demo aplikacije");

            java.util.UUID id = dao.insert(p);
            System.out.println("Inserted partner with id: " + id);

            Partner loaded = dao.findById(id);
            System.out.println("Loaded partner: " + loaded);

            loaded.setNaziv(loaded.getNaziv() + " (posodobljeno)");
            dao.update(loaded);
            System.out.println("Updated partner name.");

            java.util.List<Partner> all = dao.findAll(25);
            System.out.println("Total partners loaded: " + all.size());

            // Cleanup
            dao.delete(id);
            System.out.println("Deleted demo partner.");
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Split SQL into statements while preserving dollar-quoted blocks (e.g. DO $$ ... $$;).
     */
    private static java.util.List<String> splitSqlStatements(String sql) {
        java.util.List<String> res = new java.util.ArrayList<String>();
        StringBuilder cur = new StringBuilder();
        boolean inDollar = false;
        String dollarTag = "";
        int len = sql.length();
        for (int i = 0; i < len; i++) {
            char c = sql.charAt(i);
            // detect start of dollar-quote: $tag$
            if (!inDollar && c == '$') {
                // attempt to read full tag
                int j = i + 1;
                while (j < len && sql.charAt(j) != '$' && Character.isJavaIdentifierPart(sql.charAt(j))) j++;
                if (j < len && sql.charAt(j) == '$') {
                    dollarTag = sql.substring(i, j + 1); // from $ to closing $
                    cur.append(dollarTag);
                    i = j; // advance
                    inDollar = true;
                    continue;
                }
            }
            if (inDollar) {
                // check for closing tag
                if (c == '$') {
                    // see if the following chars match the tag (without leading $) and then a $
                    int tagLen = dollarTag.length();
                    if (tagLen > 0 && i + tagLen - 1 < len) {
                        String maybe = sql.substring(i, i + tagLen);
                        if (maybe.equals(dollarTag)) {
                            cur.append(maybe);
                            i += tagLen - 1;
                            inDollar = false;
                            dollarTag = "";
                            continue;
                        }
                    }
                }
                cur.append(c);
                continue;
            }

            // normal mode: split on semicolon
            if (c == ';') {
                cur.append(c);
                res.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        if (cur.length() > 0) res.add(cur.toString());
        return res;
    }

