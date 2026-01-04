Evidenca poslovnih partnerjev - lokalni demo

What I added
- Maven pom.xml (build + assembly plugin to create an executable jar)
- `DBConfig.java` - HikariCP datasource reading `src/resources/db.properties`
- `Partner.java` - simple model
- `PartnerDAO.java` - basic JDBC CRUD against `partnerji` table
- `src/resources/db/schema.sql` - PostgreSQL schema and sample data
- `src/resources/db.properties` - DB connection properties
- Updated `Main.java` to run `init-db` and `demo` actions

How to run
1. Ensure you have Java 11+ and Maven installed.
2. Set up a PostgreSQL database and update `src/resources/db.properties` with connection details.
3. Build the project:

```powershell
mvn -q package
```

4. Initialize DB schema (or use pgAdmin/psql):

```powershell
java -jar target/evidenca-partnerjev-0.1.0-jar-with-dependencies.jar init-db
```

5. Run a small demo (insert/select/update/delete):

```powershell
java -jar target/evidenca-partnerjev-0.1.0-jar-with-dependencies.jar demo
```

How to run with Docker Compose (recommended for quick start)
1. Ensure Docker is installed and running.
2. From the project root run:

```powershell
docker-compose up -d
```

This starts PostgreSQL on localhost:5432 with database `partnersdb` (user/password: postgres). The included `schema.sql` is mounted into the container's init folder so it will be executed on first run.

Creating an admin user
After the DB is up and the schema initialized, you can create an admin user with:

```powershell
mvn -q package
java -jar target/evidenca-partnerjev-0.1.0-jar-with-dependencies.jar create-admin admin mypassword
```

Then you can run the demo:

```powershell
java -jar target/evidenca-partnerjev-0.1.0-jar-with-dependencies.jar demo
```

Notes
- The project assumes a local Postgres with the `uuid-ossp` extension available. If not, you can change UUID default to `gen_random_uuid()` if `pgcrypto` is available.
- This is a minimal demo; for production consider migrations (Flyway/Liquibase), password hashing for users, and configuration via environment variables.
- If you don't want Docker, manually create a Postgres DB and run the `init-db` command shown earlier.
- The project currently provides a CLI and DAO. Next steps could be a JavaFX UI, search/filter features, and pagination.
