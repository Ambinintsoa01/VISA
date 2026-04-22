-- -----------------------------------------------------
-- 4. INSERTION DES DONNÉES DE RÉFÉRENCE
-- -----------------------------------------------------

-- Situation familiale
INSERT INTO situation_familiale_ref (code, libelle) VALUES
('celibataire', 'Célibataire'),
('marie', 'Marié(e)'),
('divorce', 'Divorcé(e)'),
('veuf', 'Veuf/Veuve');

-- Nationalité (exemples)
INSERT INTO nationalite_ref (code, libelle) VALUES
('MG', 'Malgache'),
('FR', 'Française'),
('CN', 'Chinoise'),
('US', 'Américaine'),
('OTHER', 'Autre');

-- Profession
INSERT INTO profession_ref (code, libelle) VALUES
('salarie', 'Salarié'),
('independant', 'Indépendant'),
('commercant', 'Commerçant'),
('artisan', 'Artisan'),
('prof_liberale', 'Profession libérale'),
('sans', 'Sans profession');

-- Statut VISA
INSERT INTO statut_visa_ref (code, libelle, ordre_affichage) VALUES
('valide', 'Valide', 1),
('expire', 'Expiré', 2),
('transforme', 'Transformé', 3),
('annule', 'Annulé', 4),
('en_cours_transfert', 'En cours de transfert', 5);

-- Statut Carte Résident
INSERT INTO statut_carte_ref (code, libelle, ordre_affichage) VALUES
('actif', 'Actif', 1),
('expire', 'Expiré', 2),
('suspendu', 'Suspendu', 3),
('perdu', 'Perdu', 4),
('transfert', 'En transfert', 5),
('renouvele', 'Renouvelé', 6);

-- Statut Demande
INSERT INTO statut_demande_ref (code, libelle, ordre_affichage) VALUES
('en_attente', 'En attente', 1),
('en_cours', 'En cours de traitement', 2),
('validee', 'Validée', 3),
('rejetee', 'Rejetée', 4),
('annulee', 'Annulée', 5);

-- Type de demande
INSERT INTO type_demande_ref (code, libelle) VALUES
('transformation', 'Demande de transformation VISA → Carte résident'),
('duplicata', 'Demande de duplicata (perte de carte)'),
('transfert_visa', 'Transfert de VISA'),
('transfert_carte', 'Transfert de carte résident');

-- Type de VISA voulu
INSERT INTO type_visa_voulu_ref (code, libelle) VALUES
('travailleur', 'VISA Travailleur'),
('investisseur', 'VISA Investisseur');

-- Type de carte
INSERT INTO type_carte_ref (code, libelle) VALUES
('travailleur', 'Carte résident Travailleur'),
('investisseur', 'Carte résident Investisseur');