-- Données d'initialisation pour la base de données VISA
-- PostgreSQL DDL
-- Date: 2026-04-22

-- Sample seed values for types/status (optionnel)
INSERT INTO statut_demande (code, libelle) VALUES
  ('CREATED','Créée'),
  ('APPROVED','Approuvée'),
  ('REJECTED','Rejetée')
ON CONFLICT DO NOTHING;

INSERT INTO statut_dossier (code, libelle) VALUES
  ('OPEN','Ouvert'),
  ('CLOSED','Clôturé')
ON CONFLICT DO NOTHING;

INSERT INTO statut_piece (code, libelle) VALUES
  ('FOURNI','Fourni'),
  ('NON_FOURNI','Non fourni')
ON CONFLICT DO NOTHING;

-- Fin du fichier

-- Populate nationalite
INSERT INTO nationalite (code_iso, libelle)
SELECT 'MDG','Madagascar' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='MDG');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'FRA','France' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='FRA');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'COM','Comores' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='COM');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'MUS','Maurice' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='MUS');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'USA','États-Unis' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='USA');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'GBR','Royaume-Uni' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='GBR');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'CHN','Chine' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='CHN');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'IND','Inde' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='IND');
INSERT INTO nationalite (code_iso, libelle)
SELECT 'ZAF','Afrique du Sud' WHERE NOT EXISTS (SELECT 1 FROM nationalite WHERE code_iso='ZAF');

-- Populate sexe
INSERT INTO sexe (code, libelle)
SELECT 'M','Masculin' WHERE NOT EXISTS (SELECT 1 FROM sexe WHERE code='M');
INSERT INTO sexe (code, libelle)
SELECT 'F','Féminin' WHERE NOT EXISTS (SELECT 1 FROM sexe WHERE code='F');

-- Populate situation_familiale
INSERT INTO situation_familiale (code, libelle)
SELECT 'CEL','Célibataire' WHERE NOT EXISTS (SELECT 1 FROM situation_familiale WHERE code='CEL');
INSERT INTO situation_familiale (code, libelle)
SELECT 'MAR','Marié(e)' WHERE NOT EXISTS (SELECT 1 FROM situation_familiale WHERE code='MAR');
INSERT INTO situation_familiale (code, libelle)
SELECT 'DIV','Divorcé(e)' WHERE NOT EXISTS (SELECT 1 FROM situation_familiale WHERE code='DIV');
INSERT INTO situation_familiale (code, libelle)
SELECT 'VEU','Veuf/Veuve' WHERE NOT EXISTS (SELECT 1 FROM situation_familiale WHERE code='VEU');
INSERT INTO situation_familiale (code, libelle)
SELECT 'PAC','Pacsé(e)' WHERE NOT EXISTS (SELECT 1 FROM situation_familiale WHERE code='PAC');
INSERT INTO situation_familiale (code, libelle)
SELECT 'SEP','Séparé(e)' WHERE NOT EXISTS (SELECT 1 FROM situation_familiale WHERE code='SEP');

-- Populate type_visa
INSERT INTO type_visa (code, libelle) VALUES
('INVESTOR', 'Investisseur'),
('EMPLOYEE', 'Employée');

-- Populate type_identite
INSERT INTO type_identite (code, libelle)
SELECT 'CIN','Carte d''Identité Nationale' WHERE NOT EXISTS (SELECT 1 FROM type_identite WHERE code='CIN');
INSERT INTO type_identite (code, libelle)
SELECT 'PASSEPORT','Passeport' WHERE NOT EXISTS (SELECT 1 FROM type_identite WHERE code='PASSEPORT');
INSERT INTO type_identite (code, libelle)
SELECT 'PERMIS','Permis de conduire' WHERE NOT EXISTS (SELECT 1 FROM type_identite WHERE code='PERMIS');