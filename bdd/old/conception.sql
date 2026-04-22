-- =====================================================
-- BASE DE DONNÉES AVEC TABLES DE STATUTS
-- =====================================================

-- -----------------------------------------------------
-- 1. TABLES DE RÉFÉRENCE (STATUTS)
-- -----------------------------------------------------

-- Statut VISA
CREATE TABLE statut_visa_ref (
    id_statut_visa SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    description TEXT,
    ordre_affichage INTEGER DEFAULT 0,
    actif BOOLEAN DEFAULT TRUE
);

-- Statut Carte Résident
CREATE TABLE statut_carte_ref (
    id_statut_carte SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    description TEXT,
    ordre_affichage INTEGER DEFAULT 0,
    actif BOOLEAN DEFAULT TRUE
);

-- Statut Demande
CREATE TABLE statut_demande_ref (
    id_statut_demande SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    description TEXT,
    ordre_affichage INTEGER DEFAULT 0,
    actif BOOLEAN DEFAULT TRUE
);

-- Type de demande
CREATE TABLE type_demande_ref (
    id_type_demande SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE
);

-- Type de VISA voulu
CREATE TABLE type_visa_voulu_ref (
    id_type_visa_voulu SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE
);

-- Type de carte résident
CREATE TABLE type_carte_ref (
    id_type_carte SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE
);

-- Situation familiale
CREATE TABLE situation_familiale_ref (
    id_situation SERIAL PRIMARY KEY,
    code VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    actif BOOLEAN DEFAULT TRUE
);

-- Nationalité
CREATE TABLE nationalite_ref (
    id_nationalite SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    actif BOOLEAN DEFAULT TRUE
);

-- Profession
CREATE TABLE profession_ref (
    id_profession SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    actif BOOLEAN DEFAULT TRUE
);

-- -----------------------------------------------------
-- 2. TABLES PRINCIPALES
-- -----------------------------------------------------

-- Personne (état civil)
CREATE TABLE personne (
    id_personne SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    nom_jeune_fille VARCHAR(100),
    date_naissance DATE NOT NULL,
    lieu_naissance VARCHAR(150),
    id_situation_familiale INTEGER REFERENCES situation_familiale_ref(id_situation),
    id_nationalite INTEGER REFERENCES nationalite_ref(id_nationalite),
    id_profession INTEGER REFERENCES profession_ref(id_profession),
    adresse TEXT,
    contact VARCHAR(50),
    email VARCHAR(150) UNIQUE,
    photo_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VISA
CREATE TABLE visa (
    id_visa SERIAL PRIMARY KEY,
    id_personne INTEGER NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE,
    num_visa VARCHAR(50) UNIQUE NOT NULL,
    date_entree DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_statut_visa INTEGER REFERENCES statut_visa_ref(id_statut_visa),
    est_transformable BOOLEAN DEFAULT FALSE,
    est_connu_interieur BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Demande
CREATE TABLE demande (
    id_demande SERIAL PRIMARY KEY,
    id_personne INTEGER NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE,
    id_visa_original INTEGER REFERENCES visa(id_visa),
    id_type_demande INTEGER REFERENCES type_demande_ref(id_type_demande),
    id_type_visa_voulu INTEGER REFERENCES type_visa_voulu_ref(id_type_visa_voulu),
    id_statut_demande INTEGER REFERENCES statut_demande_ref(id_statut_demande),
    sans_donne_interieur BOOLEAN DEFAULT FALSE,
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motif TEXT,
    traite_par VARCHAR(100),
    date_traitement TIMESTAMP
);

-- Carte Résident
CREATE TABLE carte_resident (
    id_carte SERIAL PRIMARY KEY,
    id_personne INTEGER NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE,
    id_demande INTEGER REFERENCES demande(id_demande),
    num_carte VARCHAR(50) UNIQUE NOT NULL,
    date_emission DATE NOT NULL,
    date_expiration DATE NOT NULL,
    id_statut_carte INTEGER REFERENCES statut_carte_ref(id_statut_carte),
    id_type_carte INTEGER REFERENCES type_carte_ref(id_type_carte),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Duplicata (perte de carte)
CREATE TABLE duplicata (
    id_duplicata SERIAL PRIMARY KEY,
    id_carte_original INTEGER NOT NULL REFERENCES carte_resident(id_carte),
    id_demande INTEGER REFERENCES demande(id_demande),
    num_nouvelle_carte VARCHAR(50) UNIQUE NOT NULL,
    motif VARCHAR(255) DEFAULT 'perte',
    date_delivrance DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transfert VISA
CREATE TABLE transfert_visa (
    id_transfert SERIAL PRIMARY KEY,
    id_visa_original INTEGER NOT NULL REFERENCES visa(id_visa),
    id_nouveau_visa INTEGER REFERENCES visa(id_visa),
    id_demande INTEGER REFERENCES demande(id_demande),
    date_transfert DATE NOT NULL,
    motif TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transfert Carte
CREATE TABLE transfert_carte (
    id_transfert SERIAL PRIMARY KEY,
    id_carte_original INTEGER NOT NULL REFERENCES carte_resident(id_carte),
    id_nouvelle_carte INTEGER REFERENCES carte_resident(id_carte),
    id_demande INTEGER REFERENCES demande(id_demande),
    date_transfert DATE NOT NULL,
    motif TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Historique des statuts
CREATE TABLE historique_status (
    id_historique SERIAL PRIMARY KEY,
    entite_type VARCHAR(50) CHECK (entite_type IN ('visa', 'carte_resident', 'demande')),
    entite_id INTEGER NOT NULL,
    id_ancien_statut INTEGER,
    id_nouveau_statut INTEGER,
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    raison TEXT
);

-- -----------------------------------------------------
-- 3. INDEX
-- -----------------------------------------------------

CREATE INDEX idx_personne_nom ON personne(nom);
CREATE INDEX idx_personne_email ON personne(email);
CREATE INDEX idx_visa_num_visa ON visa(num_visa);
CREATE INDEX idx_visa_personne ON visa(id_personne);
CREATE INDEX idx_visa_statut ON visa(id_statut_visa);
CREATE INDEX idx_demande_personne ON demande(id_personne);
CREATE INDEX idx_demande_statut ON demande(id_statut_demande);
CREATE INDEX idx_demande_type ON demande(id_type_demande);
CREATE INDEX idx_carte_num_carte ON carte_resident(num_carte);
CREATE INDEX idx_carte_personne ON carte_resident(id_personne);
CREATE INDEX idx_carte_statut ON carte_resident(id_statut_carte);



-- -----------------------------------------------------
-- 5. FONCTIONS ET TRIGGERS
-- -----------------------------------------------------

-- Mise à jour automatique de updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_personne_updated_at
    BEFORE UPDATE ON personne
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_visa_updated_at
    BEFORE UPDATE ON visa
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_carte_updated_at
    BEFORE UPDATE ON carte_resident
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Trigger pour historiser changement statut VISA
CREATE OR REPLACE FUNCTION log_visa_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.id_statut_visa IS DISTINCT FROM NEW.id_statut_visa THEN
        INSERT INTO historique_status (entite_type, entite_id, id_ancien_statut, id_nouveau_statut, raison)
        VALUES ('visa', NEW.id_visa, OLD.id_statut_visa, NEW.id_statut_visa, 'Changement statut VISA');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_visa_status_log
    AFTER UPDATE ON visa
    FOR EACH ROW
    EXECUTE FUNCTION log_visa_status_change();

-- Trigger pour historiser changement statut Carte
CREATE OR REPLACE FUNCTION log_carte_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.id_statut_carte IS DISTINCT FROM NEW.id_statut_carte THEN
        INSERT INTO historique_status (entite_type, entite_id, id_ancien_statut, id_nouveau_statut, raison)
        VALUES ('carte_resident', NEW.id_carte, OLD.id_statut_carte, NEW.id_statut_carte, 'Changement statut Carte Résident');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_carte_status_log
    AFTER UPDATE ON carte_resident
    FOR EACH ROW
    EXECUTE FUNCTION log_carte_status_change();

-- Trigger pour historiser changement statut Demande
CREATE OR REPLACE FUNCTION log_demande_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.id_statut_demande IS DISTINCT FROM NEW.id_statut_demande THEN
        INSERT INTO historique_status (entite_type, entite_id, id_ancien_statut, id_nouveau_statut, raison)
        VALUES ('demande', NEW.id_demande, OLD.id_statut_demande, NEW.id_statut_demande, 'Changement statut Demande');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_demande_status_log
    AFTER UPDATE ON demande
    FOR EACH ROW
    EXECUTE FUNCTION log_demande_status_change();

