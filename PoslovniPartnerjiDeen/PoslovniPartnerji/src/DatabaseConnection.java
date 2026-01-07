import com.zaxxer.hikari.HikariConfig;

import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;

import java.io.IOException;

import java.sql.Connection;

import java.sql.SQLException;

import java.util.Properties;

/**

 * Razred za upravljanje bazena podatkovnih povezav za PostgreSQL 

 * HikariCP za učinkovito upravljanje povezav z bazo podatkov 

 */

public final class DatabaseConnection {

  /* Konfiguracijska datoteka Database.Properties vsebuje nastavitve za povezavo z bazo */

          private static final String CONFIG_FILE = "Database.Properties"; 

  /* Statični podatkovni vir, ki se inicializira ob nalaganju razreda */

          private static final HikariDataSource DATA_SOURCE; 

  /* Statični inicializacijski blok, ki se izvede ob nalaganju razreda */

          static { 

    DATA_SOURCE = initializeDataSource(); 

  } 

  /* Zasebni konstruktor prepreči ustvarjanje instanc razreda */

          private void DataSourcePovezavaDB() {

    }

  /* 

  * Inicializira podatkovni vir z branjem konfiguracijske datoteke in nastavitez za HikariConfig 

  * @return HikariDataSource podatkovni vir 

  */

          private static HikariDataSource initializeDataSource() { 

    Properties properties = loadDatabaseProperties(); 

    HikariConfig config = createHikariConfig(properties); 

    return new HikariDataSource(config); 

  } 

  /* 

  * Naloži lastnosti iz konfiguracijske datoteke 

  * @return Properties objekt z nastavitvami iz datoteke 

  * @throws RuntimeException če datoteke ni mogoče najti ali prebrati 

  */

          private static Properties loadDatabaseProperties() { 

    Properties properties = new Properties(); 

    /* try-with-resources - samodejno zapiranje vhodnega toka FileInputStream */ 

    try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) { 

      properties.load(fis); 

    } catch (IOException e) { 

      throw new RuntimeException("Napaka pri branju konfiguracijske datoteke: " + CONFIG_FILE, e); 

    } 

    return properties; 

  } 

  /* 

  * Ustvari HikariConfig z optimizacijami za PostgreSQL 

  * @param properties lastnosti konfiguracijske datoteke 

  * @return HikariConfig konfiguriran objekt z nastavitvami 

  */

          private static HikariConfig createHikariConfig(Properties properties) { 

    HikariConfig config = new HikariConfig(); 

    /* Osnovne nastavitve povezave - zahtevane lastnosti */ 

    config.setJdbcUrl(getRequiredProperty(properties, "Database.dbUrl")); 

    config.setUsername(getRequiredProperty(properties, "Database.uporabnik")); 

    config.setPassword(getRequiredProperty(properties, "Database.geslo"));

    /* PostgreSQL specifične optimizacije in nastavitve bazena */ 

    configurePostgreSQLOptimizations(config, properties); 

    configureConnectionPool(config, properties); 

    return config; 

  } 

  /* 

  * Konfigurira PostgreSQL specifične optimizacije za maksimalno zmogljivost 

  * @param config HikariConfig objekt za konfiguracijo 

  * @param properties lastnosti iz konfiguracijske datoteke 

  */

          private static void configurePostgreSQLOptimizations(HikariConfig config, Properties properties) { 

    /* PostgreSQL gonilnik */ 

    config.setDriverClassName("org.postgresql.Driver"); 

    /* 

    * OPTIMIZACIJE ZA PreparedStatement: 

    * cachePrepStmts - omogoči predpomnilnik pripravljenih stavkov 

    * prepStmtCacheSize - velikost predpomnilnika za objekte PreparedStatement 

    * prepStmtCacheSqlLimit - maksimalna dolžina SQL stavka za predpomnilnik 

    * useServerPrepStmts - uporaba strežniških pripravljenih stavkov 

    */ 

    config.addDataSourceProperty("cachePrepStmts", "true"); 

    config.addDataSourceProperty("prepStmtCacheSize", "250"); 

    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); 

    config.addDataSourceProperty("useServerPrepStmts", "true"); 

    /* 

    * OPTIMIZACIJE SEJE: 

    * useLocalSessionState - uporaba lokalnega stanja seje 

    * rewriteBatchedStatements - preoblikovanje batch stavkov za hitrejše izvajanje 

    * cacheResultSetMetadata - predpomnilnik metapodatkov ResultSet 

    * cacheServerConfiguration - predpomnilnik konfiguracije strežnika 

    */ 

    config.addDataSourceProperty("useLocalSessionState", "true"); 

    config.addDataSourceProperty("rewriteBatchedStatements", "true"); 

    config.addDataSourceProperty("cacheResultSetMetadata", "true"); 

    config.addDataSourceProperty("cacheServerConfiguration", "true"); 

    /* 

    * OPTIMIZACIJE ZA UČINKOVITOST: 

    * elideSetAutoCommits - izogibanje nepotrebnim setAutoCommit klicem 

    * maintainTimeStats - onemogoči vzdrževanje časovne statistike (manj overhead) 

    */ 

    config.addDataSourceProperty("elideSetAutoCommits", "true"); 

    config.addDataSourceProperty("maintainTimeStats", "false"); 

    /* 

    * POSTGRESQL SPECIFIČNE NASTAVITVE: 

    * ApplicationName - identifikacija aplikacije v PostgreSQL 

    * assumeMinServerVersion - predpostavka minimalne različice strežnika 

    * characterEncoding - kodiranje znakov za povezavo 

    * stringtype - način obravnave nizov (unspecified za boljšo zmogljivost) 

    */ 

    config.addDataSourceProperty("ApplicationName", "App pošte"); 

    config.addDataSourceProperty("assumeMinServerVersion", "11"); 

    config.addDataSourceProperty("characterEncoding", "UTF-8"); 

    config.addDataSourceProperty("stringtype", "unspecified"); 

    /* 

    * NASTAVITVE TRANSAKCIJ: 

    * defaultAutoCommit - privzeto onemogoči auto-commit za boljši nadzor 

    * autocommit - eksplicitno onemogoči auto-commit 

    */ 

    config.addDataSourceProperty("defaultAutoCommit", "false"); 

    config.addDataSourceProperty("autocommit", "false"); 

    /* 

    * OPTIMIZACIJE OMREŽJA: 

    * socketTimeout - timeout za omrežne vtičnice v sekundah 

    * connectTimeout - timeout za vzpostavitev povezave v sekundah 

    * tcpKeepAlive - omogoči TCP keep-alive za dolgotrajne povezave 

    */ 

    config.addDataSourceProperty("socketTimeout", "30"); 

    config.addDataSourceProperty("connectTimeout", "10"); 

    config.addDataSourceProperty("tcpKeepAlive", "true"); 

    /* 

    * DODATNE OPTIMIZACIJE ZA PRIPRAVLJENE STAVKE: 

    * preparedStatementCacheQueries - število pripravljenih stavkov v predpomnilniku 

    * preparedStatementCacheSizeMiB - velikost predpomnilnika v MiB 

    */ 

    config.addDataSourceProperty("preparedStatementCacheQueries", "1024"); 

    config.addDataSourceProperty("preparedStatementCacheSizeMiB", "32"); 

    /* 

    * OPTIMIZACIJE ZA BATCH OPERACIJE IN ČIŠČENJE: 

    * reWriteBatchedInserts - preoblikuje batch INSERT stavke za hitrejše izvajanje 

    * logUnclosedConnections - beleži nezaključene povezave za odpravo napak 

    */ 

    config.addDataSourceProperty("reWriteBatchedInserts", "true"); 

    config.addDataSourceProperty("logUnclosedConnections", "true"); 

  } 

  /* 

  * Konfigurira nastavitve bazena povezav 

  * @param config HikariConfig objekt za konfiguracijo 

  * @param properties lastnosti iz konfiguracijske datoteke 

  */

          private static void configureConnectionPool(HikariConfig config, Properties properties) { 

    /* 

    * NASTAVITVE VELIKOSTI BAZENA: 

    * maximumPoolSize - maksimalno število povezav v bazenu 

    * minimumIdle - minimalno število prostih povezav v bazenu 

    */ 

    config.setMaximumPoolSize(getIntProperty(properties, "maximumPoolSize", 20)); 

    config.setMinimumIdle(getIntProperty(properties, "minimumIdle", 5)); 

    /* 

    * ČASOVNE NASTAVITVE: 

    * connectionTimeout - maksimalni čas čakanja na povezavo (ms) 

    * idleTimeout - čas, po katerem se neuporabljena povezava zapre (ms) 

    * maxLifetime - maksimalni življenjski čas povezave (ms) 

    * validationTimeout - timeout za validacijo povezave (ms) 

    */ 

    config.setConnectionTimeout(getIntProperty(properties, "connectionTimeout", 30000)); // 30 sekund 

    config.setIdleTimeout(getIntProperty(properties, "idleTimeout", 600000)); // 10 minut 

    config.setMaxLifetime(getIntProperty(properties, "maxLifetime", 1800000)); // 30 minut 

    config.setValidationTimeout(getIntProperty(properties, "validationTimeout", 5000)); // 5 sekund 

    /* 

    * NASTAVITVE PREVERJANJA POVEZAV: 

    * connectionTestQuery - SQL stavek za preverjanje veljavnosti povezave 

    * leakDetectionThreshold - čas, po katerem se zaziva puščanje povezav (ms) 

    */ 

    config.setConnectionTestQuery("SELECT 1"); 

    config.setLeakDetectionThreshold(getIntProperty(properties, "leakDetectionThreshold", 60000)); // 1 minuta 

    /* 

    * DODATNE OPTIMIZACIJE: 

    * keepaliveTime - interval za ohranjanje povezav (ms) 

    * initializationFailTimeout - timeout za neuspešno inicializacijo (s) 

    */ 

    config.setKeepaliveTime(getIntProperty(properties, "keepaliveTime", 0)); // 0 = privzeto 

    config.setInitializationFailTimeout(getIntProperty(properties, "initializationFailTimeout", 1)); 

  } 

  /* 

  * Pomožna metoda za pridobivanje zahtevane lastnosti iz konfiguracije 

  * @param properties objekt z lastnostmi 

  * @param key ključ lastnosti 

  * @return String vrednost lastnosti 

  * @throws RuntimeException če lastnost manjka ali je prazna 

  */

          private static String getRequiredProperty(Properties properties, String key) { 

    String value = properties.getProperty(key); 

    if (value == null || value.trim().isEmpty()) { 

      throw new RuntimeException("Manjkajoča ali prazna lastnost: " + key); 

    } 

    return value.trim(); 

  } 

  /* 

  * Pomožna metoda za pridobivanje celoštevilske lastnosti s privzeto vrednostjo 

  * @param properties objekt z lastnostmi 

  * @param key ključ lastnosti 

  * @param defaultValue privzeta vrednost, če lastnost ne obstaja 

  * @return int vrednost lastnosti ali privzeta vrednost 

  */

          private static int getIntProperty(Properties properties, String key, int defaultValue) { 

    String value = properties.getProperty(key); 

    if (value != null) { 

      try { 

        return Integer.parseInt(value.trim()); 

      } catch (NumberFormatException e) { 

        /* V primeru napake pri parsanju uporabimo privzeto vrednost */ 

      } 

    } 

    return defaultValue; 

  } 

  /* 

  * Vrne povezavo iz bazena povezav 

  * @return Connection objekt povezave z bazo 

  * @throws SQLException če pride do napake pri pridobivanju povezave 

  */

          public static Connection getConnection() throws SQLException { 

    return DATA_SOURCE.getConnection(); 

  } 

  /* 

  * Vrne povezavo z nastavljenim nivojem izolacije transakcij 

  * @param isolationLevel nivo izolacije transakcij (npr. Connection.TRANSACTION_READ_COMMITTED) 

  * @return Connection objekt povezave z nastavljenim nivojem izolacije 

  * @throws SQLException če pride do napake pri pridobivanju povezave 

  */
    public static Connection getConnection(int isolationLevel) throws SQLException {

    Connection conn = DATA_SOURCE.getConnection(); 

    conn.setTransactionIsolation(isolationLevel); 

    return conn; 

  } 

  /* 

  * Vrne število aktivnih povezav v bazen 

  * @return int število aktivnih povezav 

  */

          public static int getActiveConnections() { 

    return DATA_SOURCE.getHikariPoolMXBean().getActiveConnections(); 

  } 

  /* 

  * Vrne število prostih (neaktivnih) povezav v bazen 

  * @return int število prostih povezav 

  */

          public static int getIdleConnections() { 

    return DATA_SOURCE.getHikariPoolMXBean().getIdleConnections(); 

  } 

  /* 

  * Vrne skupno število povezav v bazen (aktivne + prosté) 

  * @return int skupno število povezav 

  */

          public static int getTotalConnections() { 

    return DATA_SOURCE.getHikariPoolMXBean().getTotalConnections(); 

  } 

  /* 

  * Vrne število niti, ki čakajo na pridobitev povezave 

  * @return int število čakajočih niti 

  */

          public static int getWaitingThreads() { 

    return DATA_SOURCE.getHikariPoolMXBean().getThreadsAwaitingConnection(); 

  } 

  /* 

  * Varno zapre bazen povezav 

  * To metodo je priporočljivo klicati ob ustavljanju aplikacije 

  */

          public static void close() { 

    if (DATA_SOURCE != null && !DATA_SOURCE.isClosed()) { 

      DATA_SOURCE.close(); 

    } 

  } 

  /* 

  * Preveri, ali je bazen povezav aktiven in deluje 

  * @return boolean true, če je bazen aktiven, false sicer 

  */

          public static boolean isRunning() { 

    return DATA_SOURCE != null && !DATA_SOURCE.isClosed(); 

  } 

  /* 

  * Vrne niz s statistiko bazena povezav 

  * @return String formatiran niz s statističnimi podatki 

  */

          public static String getPoolStats() { 

    return String.format(

                        "Pool stats: Active=%d, Idle=%d, Total=%d, Waiting=%d",

                        getActiveConnections(),

                        getIdleConnections(),

                        getTotalConnections(),

                        getWaitingThreads()

                    ); 

  }

} 