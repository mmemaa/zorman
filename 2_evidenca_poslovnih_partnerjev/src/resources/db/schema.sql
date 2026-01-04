-- PostgreSQL schema for partnerji
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- enum types
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'partner_status') THEN
        CREATE TYPE partner_status AS ENUM ('Aktiven', 'V likvidaciji', 'Brisan', 'V stečaju', 'V prisilni poravnavi');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'velikost_podjetja_type') THEN
        CREATE TYPE velikost_podjetja_type AS ENUM ('Mikro', 'Malo', 'Srednje', 'Veliko');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS pravne_oblike (
    id VARCHAR(50) PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL,
    opis TEXT
);

CREATE TABLE IF NOT EXISTS drzave (
    id SERIAL PRIMARY KEY,
    kod VARCHAR(10) UNIQUE,
    naziv VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS partnerji (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    maticna_stevilka VARCHAR(8) NOT NULL UNIQUE CHECK (maticna_stevilka ~ '^[0-9]{8}$'),
    davcna_stevilka VARCHAR(8) UNIQUE CHECK (davcna_stevilka ~ '^[0-9]{8}$'),
    naziv VARCHAR(250) NOT NULL,
    kratki_naziv VARCHAR(100),
    pravna_oblika_id VARCHAR(50) REFERENCES pravne_oblike(id) ON DELETE RESTRICT,
    status partner_status DEFAULT 'Aktiven',
    dejavnost VARCHAR(500),
    naslov VARCHAR(80),
    posta_stevilka VARCHAR(10),
    kraj VARCHAR(100),
    drzava_id INTEGER REFERENCES drzave(id),
    telefonska_stevilka VARCHAR(20),
    elektronska_posta VARCHAR(100),
    spletna_stran VARCHAR(250),
    velikost_podjetja velikost_podjetja_type,
    je_davcni_zavezanec BOOLEAN DEFAULT FALSE,
    stevilka_evidence_ddv VARCHAR(20),
    opombe TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION trg_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_updated_at ON partnerji;
CREATE TRIGGER set_updated_at
BEFORE UPDATE ON partnerji
FOR EACH ROW
EXECUTE PROCEDURE trg_set_updated_at();

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(150),
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER','ADMIN')) DEFAULT 'USER',
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- sample data
INSERT INTO pravne_oblike (id, naziv) VALUES
('D.D.', 'Delniška družba'),
('D.O.O.', 'Družba z omejeno odgovornostjo'),
('K.D.', 'Komanditna družba'),
('S.P.', 'Samostojni podjetnik'),
('D.N.O.', 'Družba z neomejeno odgovornostjo'),
('G.S.', 'Gospodarsko društvo (zadruga)')
ON CONFLICT (id) DO NOTHING;

INSERT INTO drzave (kod, naziv) VALUES
('SI', 'Slovenija'),
('HR', 'Hrvaška'),
('AT', 'Avstrija')
ON CONFLICT (kod) DO NOTHING;

INSERT INTO partnerji (maticna_stevilka, davcna_stevilka, naziv, kratki_naziv, pravna_oblika_id, status, dejavnost, naslov, posta_stevilka, kraj, drzava_id, telefonska_stevilka, elektronska_posta, velikost_podjetja, je_davcni_zavezanec, stevilka_evidence_ddv, opombe)
VALUES
('12345678','87654321','Podjetje d.o.o.','Podjetje','D.O.O.','Aktiven','Proizvodnja','Glavna ulica 1','1000','Ljubljana', (SELECT id FROM drzave WHERE kod='SI'), '+38612345678','info@example.com','Srednje',true,'SI12345','Testni zapis')
ON CONFLICT DO NOTHING;

