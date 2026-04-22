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

-- Populate type_visa (idempotent)
INSERT INTO type_visa (code, libelle)
SELECT 'INVESTOR', 'Investisseur' WHERE NOT EXISTS (SELECT 1 FROM type_visa WHERE code='INVESTOR');
INSERT INTO type_visa (code, libelle)
SELECT 'EMPLOYEE', 'Employée' WHERE NOT EXISTS (SELECT 1 FROM type_visa WHERE code='EMPLOYEE');
INSERT INTO type_visa (code, libelle)
SELECT 'FAMILY', 'Regroupement familial' WHERE NOT EXISTS (SELECT 1 FROM type_visa WHERE code='FAMILY');

-- Populate type_identite
INSERT INTO type_identite (code, libelle)
SELECT 'CIN','Carte d''Identité Nationale' WHERE NOT EXISTS (SELECT 1 FROM type_identite WHERE code='CIN');
INSERT INTO type_identite (code, libelle)
SELECT 'PASSEPORT','Passeport' WHERE NOT EXISTS (SELECT 1 FROM type_identite WHERE code='PASSEPORT');
INSERT INTO type_identite (code, libelle)
SELECT 'PERMIS','Permis de conduire' WHERE NOT EXISTS (SELECT 1 FROM type_identite WHERE code='PERMIS');

-- Populate catalogue_piece_commune
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'PHOTO_IDENTITE','02 photos d\'identité', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='PHOTO_IDENTITE');
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'NOTICE_RENSEIGNEMENT','Notice de renseignement', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='NOTICE_RENSEIGNEMENT');
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'DEMANDE_MINISTERE','Demande adressée au Ministère de l\'Intérieur (avec email & téléphone)', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='DEMANDE_MINISTERE');
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'PHOTOCOPIE_VISA','Photocopie certifiée du visa en cours de validité', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='PHOTOCOPIE_VISA');
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'PHOTOCOPIE_PASSPORT','Photocopie certifiée de la première page du passeport', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='PHOTOCOPIE_PASSPORT');
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'PHOTOCOPIE_CARTE_RESIDENT','Photocopie certifiée de la carte résident en cours de validité', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='PHOTOCOPIE_CARTE_RESIDENT');
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'CERTIFICAT_RESIDENCE','Certificat de résidence à Madagascar', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='CERTIFICAT_RESIDENCE');
INSERT INTO catalogue_piece_commune (code, libelle, obligatoire)
SELECT 'CASIER_JUDICIAIRE','Extrait de casier judiciaire moins de 3 mois', true WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_commune WHERE code='CASIER_JUDICIAIRE');

-- Populate catalogue_piece_complementaire (par catégorie)
-- Investisseur
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'STATUT_SOCIETE','Statut de la Société', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='STATUT_SOCIETE'), 1;
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'EXTRAIT_REGISTRE_COMMERCE','Extrait d\'inscription au registre de commerce', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='EXTRAIT_REGISTRE_COMMERCE'), 1;
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'CARTE_FISCALE','Carte fiscale', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='CARTE_FISCALE'), 1;

-- Travailleur
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'AUTORISATION_EMPLOI','Autorisation emploi délivrée à Madagascar par le Ministère de la Fonction publique', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='AUTORISATION_EMPLOI'), 2;
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'ATTESTATION_EMPLOI','Attestation d\'emploi délivrée par l\'employeur (Original)', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='ATTESTATION_EMPLOI'), 2;

-- Regroupement familial
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'ACTE_NAISSANCE_MOINS_6M','Acte de naissance (enfant) délivré moins de 6 mois / acte de mariage (livret de famille)', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='ACTE_NAISSANCE_MOINS_6M'), 3;
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'JUSTIFICATIF_RESSOURCES','Justificatif de ressources pour les cas de regroupement familial', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='JUSTIFICATIF_RESSOURCES'), 3;
INSERT INTO catalogue_piece_complementaire (code, libelle, obligatoire, type_visa_id)
SELECT 'AUTORISATION_EMPLOI_REG','Autorisation emploi pour le Regroupement familial des travailleurs', false WHERE NOT EXISTS (SELECT 1 FROM catalogue_piece_complementaire WHERE code='AUTORISATION_EMPLOI_REG'), 3;
