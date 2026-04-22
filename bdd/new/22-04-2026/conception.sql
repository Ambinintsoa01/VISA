-- Conception des tables pour le projet VISA
-- PostgreSQL DDL
-- Date: 2026-04-22

-- Schéma principal (optionnel)
CREATE DATABASE visa;
\c visa;

-- Référentiels / types / statuts
CREATE TABLE statut_demande (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    libelle VARCHAR(200) NOT NULL
);

CREATE TABLE statut_dossier (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    libelle VARCHAR(200) NOT NULL
);

CREATE TABLE statut_piece (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    libelle VARCHAR(200) NOT NULL
);

CREATE TABLE type_demande (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    libelle VARCHAR(200) NOT NULL
);

CREATE TABLE type_visa (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    libelle VARCHAR(200) NOT NULL
);

CREATE TABLE nationalite (
    id BIGSERIAL PRIMARY KEY,
    code_iso CHAR(3),
    libelle VARCHAR(200) NOT NULL
);

CREATE TABLE situation_familiale (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE type_identite (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE sexe (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    libelle VARCHAR(100) NOT NULL
);

-- Demandeur
CREATE TABLE demandeur (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    prenom VARCHAR(150),
    date_naissance DATE,
    sexe_id BIGINT REFERENCES sexe(id),
    nationalite_id BIGINT REFERENCES nationalite(id),
    situation_familiale_id BIGINT REFERENCES situation_familiale(id),
    email VARCHAR(255),
    telephone VARCHAR(50),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_demandeur_nom_prenom ON demandeur (lower(nom), lower(prenom));
CREATE INDEX idx_demandeur_nationalite ON demandeur (nationalite_id);
CREATE INDEX idx_demandeur_sexe ON demandeur (sexe_id);

-- Passeport
CREATE TABLE passeport (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(100) NOT NULL,
    pays_emission VARCHAR(100),
    date_emission DATE,
    date_expiration DATE,
    demandeur_id BIGINT NOT NULL REFERENCES demandeur(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now()
);
CREATE UNIQUE INDEX ux_passeport_numero_demandeur ON passeport (numero, demandeur_id);
CREATE INDEX idx_passeport_numero ON passeport (numero);

-- VisaTransformable (entité distincte, pré-demande/infos de transformation)
CREATE TABLE visa_transformable (
    id BIGSERIAL PRIMARY KEY,
    passeport_id BIGINT REFERENCES passeport(id),
    type_visa_id BIGINT REFERENCES type_visa(id),
    infos JSONB,
    remarque TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_vt_passeport ON visa_transformable (passeport_id);
CREATE INDEX idx_vt_typevisa ON visa_transformable (type_visa_id);

-- Demande (peut référencer un visa_transformable nullable)
CREATE TABLE demande (
    id BIGSERIAL PRIMARY KEY,
    demandeur_id BIGINT NOT NULL REFERENCES demandeur(id) ON DELETE RESTRICT,
    passeport_id BIGINT REFERENCES passeport(id),
    id_visa_transformable BIGINT REFERENCES visa_transformable(id), -- nullable
    type_demande_id BIGINT REFERENCES type_demande(id),
    statut_demande_id BIGINT REFERENCES statut_demande(id),
    reference_externe VARCHAR(200),
    date_creation TIMESTAMPTZ DEFAULT now(),
    date_modification TIMESTAMPTZ DEFAULT now(),
    motif_rejet TEXT
);
CREATE INDEX idx_demande_demandeur ON demande (demandeur_id);
CREATE INDEX idx_demande_type_statut_date ON demande (type_demande_id, statut_demande_id, date_creation);

-- Visa (visa délivré, lié à une demande)
CREATE TABLE visa (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(150) UNIQUE,
    demande_id BIGINT REFERENCES demande(id),
    type_visa_id BIGINT REFERENCES type_visa(id),
    date_debut DATE,
    date_fin DATE,
    commentaire TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- index pour recherche par numéro et période
CREATE INDEX idx_visa_numero ON visa (numero);
CREATE INDEX idx_visa_demande ON visa (demande_id);
CREATE INDEX idx_visa_type_date ON visa (type_visa_id, date_debut, date_fin);

-- Historique statut demande
CREATE TABLE historique_statut_demande (
    id BIGSERIAL PRIMARY KEY,
    demande_id BIGINT NOT NULL REFERENCES demande(id) ON DELETE CASCADE,
    statut_demande_id BIGINT NOT NULL REFERENCES statut_demande(id),
    date_changement TIMESTAMPTZ DEFAULT now(),
    utilisateur VARCHAR(200),
    commentaire TEXT
);
CREATE INDEX idx_hist_demande_date ON historique_statut_demande (demande_id, date_changement);

-- Dossier (regroupe la demande et les pièces)
CREATE TABLE dossier (
    id BIGSERIAL PRIMARY KEY,
    demande_id BIGINT NOT NULL REFERENCES demande(id) ON DELETE CASCADE,
    numero_dossier VARCHAR(200) UNIQUE,
    statut_dossier_id BIGINT REFERENCES statut_dossier(id),
    date_creation TIMESTAMPTZ DEFAULT now(),
    date_cloture TIMESTAMPTZ,
    created_by VARCHAR(200)
);
CREATE INDEX idx_dossier_demande_statut ON dossier (demande_id, statut_dossier_id, date_creation);

-- Carte résident (demande spécifique)
CREATE TABLE carte_resident_demande (
    id BIGSERIAL PRIMARY KEY,
    demande_id BIGINT NOT NULL REFERENCES demande(id) ON DELETE CASCADE,
    reference VARCHAR(200),
    date_debut DATE,
    date_fin DATE,
    remarques TEXT
);
CREATE INDEX idx_carte_demande ON carte_resident_demande (demande_id);

-- Catalogue des pièces communes et complémentaires
CREATE TABLE catalogue_piece_commune (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    libelle VARCHAR(300) NOT NULL,
    obligatoire BOOLEAN DEFAULT true
);

CREATE TABLE catalogue_piece_complementaire (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    libelle VARCHAR(300) NOT NULL,
    obligatoire BOOLEAN DEFAULT false
);

-- Pièces attachées au dossier
CREATE TABLE dossier_piece_commune (
    id BIGSERIAL PRIMARY KEY,
    dossier_id BIGINT NOT NULL REFERENCES dossier(id) ON DELETE CASCADE,
    catalogue_piece_commune_id BIGINT NOT NULL REFERENCES catalogue_piece_commune(id),
    statut_piece_id BIGINT REFERENCES statut_piece(id),
    fichier_path TEXT,
    date_fourni TIMESTAMPTZ,
    remarque TEXT
);
CREATE INDEX idx_dpc_dossier_piece ON dossier_piece_commune (dossier_id, catalogue_piece_commune_id, statut_piece_id);

CREATE TABLE dossier_piece_complementaire (
    id BIGSERIAL PRIMARY KEY,
    dossier_id BIGINT NOT NULL REFERENCES dossier(id) ON DELETE CASCADE,
    catalogue_piece_complementaire_id BIGINT NOT NULL REFERENCES catalogue_piece_complementaire(id),
    statut_piece_id BIGINT REFERENCES statut_piece(id),
    fichier_path TEXT,
    date_fourni TIMESTAMPTZ,
    remarque TEXT
);
CREATE INDEX idx_dpcp_dossier_piece ON dossier_piece_complementaire (dossier_id, catalogue_piece_complementaire_id, statut_piece_id);

-- Indexes utilitaires pour recherches multi-critères
CREATE INDEX idx_demande_date_statut ON demande (date_creation, statut_demande_id);
CREATE INDEX idx_dossier_numero ON dossier (numero_dossier);
CREATE INDEX idx_passeport_demandeur ON passeport (demandeur_id);

-- Fin du fichier
