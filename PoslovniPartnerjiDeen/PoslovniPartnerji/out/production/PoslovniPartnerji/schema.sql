CREATE TABLE oblike_podjetij (
                                 id SERIAL PRIMARY KEY,
                                 kratica VARCHAR(10) NOT NULL UNIQUE,
                                 polno_ime VARCHAR(100) NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE uporabniki (
                            id SERIAL PRIMARY KEY,
                            uporabnisko_ime VARCHAR(50) NOT NULL UNIQUE,
                            geslo VARCHAR(255) NOT NULL,
                            ime VARCHAR(100) NOT NULL,
                            priimek VARCHAR(100) NOT NULL,
                            email VARCHAR(150) UNIQUE,
                            vloga VARCHAR(20) NOT NULL CHECK (vloga IN ('USER', 'ADMIN')),
                            aktiven BOOLEAN DEFAULT true,
                            zadnja_prijava TIMESTAMP,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE poslovni_partnerji (
                                    id SERIAL PRIMARY KEY,
                                    krajsi_naziv VARCHAR(100) NOT NULL,
                                    polni_naziv VARCHAR(255) NOT NULL,
                                    davcna_stevilka VARCHAR(20),
                                    maticna_stevilka VARCHAR(20) UNIQUE,
                                    oblika_id INTEGER REFERENCES oblike_podjetij(id) ON DELETE RESTRICT,
                                    ulica VARCHAR(150),
                                    hisna_stevilka VARCHAR(20),
                                    postna_stevilka VARCHAR(10),
                                    kraj VARCHAR(100),
                                    email VARCHAR(150),
                                    telefon VARCHAR(30),
                                    spletna_stran VARCHAR(255),
                                    status VARCHAR(20) DEFAULT 'aktiven' CHECK (status IN ('aktiven', 'neaktiven', 'blokiran')),
                                    opombe TEXT,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_partnerji_naziv ON poslovni_partnerji(krajsi_naziv);
CREATE INDEX idx_partnerji_davcna ON poslovni_partnerji(davcna_stevilka);
CREATE INDEX idx_partnerji_maticna ON poslovni_partnerji(maticna_stevilka);
CREATE INDEX idx_partnerji_status ON poslovni_partnerji(status);
CREATE INDEX idx_uporabniki_uporabnisko_ime ON uporabniki(uporabnisko_ime);
CREATE INDEX idx_dejavnosti_uporabnik ON dejavnosti(uporabnik_id);
CREATE INDEX idx_dejavnosti_akcija ON dejavnosti(akcija);
CREATE INDEX idx_dejavnosti_entiteta ON dejavnosti(entiteta);
CREATE INDEX idx_dejavnosti_datum ON dejavnosti(created_at);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_poslovni_partnerji_updated_at
    BEFORE UPDATE ON poslovni_partnerji
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_oblike_podjetij_updated_at
    BEFORE UPDATE ON oblike_podjetij
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_uporabniki_updated_at
    BEFORE UPDATE ON uporabniki
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


INSERT INTO oblike_podjetij (kratica, polno_ime) VALUES
                                                     ('D.D.', 'Delniška družba'),
                                                     ('D.O.O.', 'Družba z omejeno odgovornostjo'),
                                                     ('K.D.', 'Komanditna družba'),
                                                     ('S.P.', 'Samostojni podjetnik'),
                                                     ('D.N.O.', 'Družba z neomejeno odgovornostjo'),
                                                     ('G.S.', 'Gospodarsko društvo (zadruga)');

-- Insert default users (password is 'admin123' and 'user123' hashed with BCrypt)
-- Note: These hashes are examples - in production, generate proper BCrypt hashes
INSERT INTO uporabniki (uporabnisko_ime, geslo, ime, priimek, email, vloga, aktiven) VALUES
                                                                                         ('admin', 'admin1234', 'Admin', 'Uporabnik', 'admin@example.com', 'ADMIN', true),
                                                                                         ('user', 'user1234', 'Navaden', 'Uporabnik', 'user@example.com', 'USER', true);

-- Insert sample business partners
INSERT INTO poslovni_partnerji (krajsi_naziv, polni_naziv, davcna_stevilka, maticna_stevilka, oblika_id,
                                ulica, hisna_stevilka, postna_stevilka, kraj, email, telefon, spletna_stran, status, opombe) VALUES
                                                                                                                                 ('ABC Trgovina', 'ABC Trgovina d.o.o.', 'SI12345678', '1234567000', 2, 'Slovenska cesta', '10', '1000', 'Ljubljana', 'info@abc-trgovina.si', '+386 1 234 5678', 'www.abc-trgovina.si', 'aktiven', 'Dolgoletni partner'),
                                                                                                                                 ('XYZ Transport', 'XYZ Transport d.d.', 'SI87654321', '7654321000', 1, 'Mariborska ulica', '25', '2000', 'Maribor', 'info@xyz-transport.si', '+386 2 345 6789', 'www.xyz-transport.si', 'aktiven', NULL),
                                                                                                                                 ('Janez Novak s.p.', 'Samostojni podjetnik Janez Novak', 'SI11223344', '1122334400', 4, 'Glavni trg', '5', '3000', 'Celje', 'janez.novak@email.si', '+386 40 123 456', NULL, 'aktiven', 'Novi partner'),
                                                                                                                                 ('Tech Solutions', 'Tech Solutions d.o.o.', 'SI99887766', '9988776600', 2, 'Tehniška pot', '15', '1000', 'Ljubljana', 'kontakt@techsolutions.si', '+386 1 987 6543', 'www.techsolutions.si', 'neaktiven', 'Začasno neaktiven'),
                                                                                                                                 ('Zadruga KME', 'Kmetijska zadruga KME', 'SI55667788', '5566778800', 6, 'Kmetijska cesta', '3', '4000', 'Kranj', 'info@zadruga-kme.si', '+386 4 234 5678', 'www.zadruga-kme.si', 'aktiven', 'Partner od 2020');

