-- -----------------------------------------------------
-- 6. VUES UTILES
-- -----------------------------------------------------

-- Vue personne avec libellés
CREATE VIEW vue_personne_detail AS
SELECT 
    p.*,
    sf.libelle AS situation_familiale_libelle,
    n.libelle AS nationalite_libelle,
    pr.libelle AS profession_libelle
FROM personne p
LEFT JOIN situation_familiale_ref sf ON p.id_situation_familiale = sf.id_situation
LEFT JOIN nationalite_ref n ON p.id_nationalite = n.id_nationalite
LEFT JOIN profession_ref pr ON p.id_profession = pr.id_profession;

-- Vue VISA avec libellés
CREATE VIEW vue_visa_detail AS
SELECT 
    v.*,
    sv.libelle AS statut_visa_libelle,
    p.nom AS personne_nom,
    p.email AS personne_email
FROM visa v
LEFT JOIN statut_visa_ref sv ON v.id_statut_visa = sv.id_statut_visa
LEFT JOIN personne p ON v.id_personne = p.id_personne;

-- Vue demande avec libellés
CREATE VIEW vue_demande_detail AS
SELECT 
    d.*,
    td.libelle AS type_demande_libelle,
    tv.libelle AS type_visa_voulu_libelle,
    sd.libelle AS statut_demande_libelle,
    p.nom AS personne_nom,
    p.email AS personne_email
FROM demande d
LEFT JOIN type_demande_ref td ON d.id_type_demande = td.id_type_demande
LEFT JOIN type_visa_voulu_ref tv ON d.id_type_visa_voulu = tv.id_type_visa_voulu
LEFT JOIN statut_demande_ref sd ON d.id_statut_demande = sd.id_statut_demande
LEFT JOIN personne p ON d.id_personne = p.id_personne;

-- Vue carte résident avec libellés
CREATE VIEW vue_carte_detail AS
SELECT 
    c.*,
    sc.libelle AS statut_carte_libelle,
    tc.libelle AS type_carte_libelle,
    p.nom AS personne_nom,
    p.email AS personne_email
FROM carte_resident c
LEFT JOIN statut_carte_ref sc ON c.id_statut_carte = sc.id_statut_carte
LEFT JOIN type_carte_ref tc ON c.id_type_carte = tc.id_type_carte
LEFT JOIN personne p ON c.id_personne = p.id_personne;

-- Demandes en attente
CREATE VIEW vue_demandes_en_attente AS
SELECT *
FROM vue_demande_detail
WHERE statut_demande_libelle = 'En attente'
ORDER BY date_demande ASC;

-- Cartes expirant dans 30 jours
CREATE VIEW vue_cartes_expirant AS
SELECT 
    c.*,
    sc.libelle AS statut_carte_libelle,
    tc.libelle AS type_carte_libelle,
    p.nom,
    p.email,
    p.contact,
    (c.date_expiration - CURRENT_DATE) AS jours_restants
FROM carte_resident c
JOIN statut_carte_ref sc ON c.id_statut_carte = sc.id_statut_carte
JOIN type_carte_ref tc ON c.id_type_carte = tc.id_type_carte
JOIN personne p ON c.id_personne = p.id_personne
WHERE c.date_expiration BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days'
AND sc.code = 'actif';