# 📋 TODO - Fonctionnalités du projet VISA Transformable

## 1. Gestion des personnes (état civil)
- [x] Saisir une nouvelle personne (nom, nom jeune fille, date/lieu naissance, situation familiale, nationalité, profession, adresse, contact, email)
- [x] Ajouter une photo de la personne
- [x] Rechercher une personne (par nom, email, numéro VISA, numéro carte)
- [x] Modifier les informations d'une personne
- [x] Consulter le détail d'une personne

## 2. Gestion des VISA
- [x] Enregistrer un VISA pour une personne (numéro VISA, date entrée, date fin, statut travailleur/investisseur)
- [x] Marquer un VISA comme "transformable" (VISA T)
- [x] Marquer un VISA comme "connu intérieur"
- [x] Transférer un VISA
- [x] Consulter l'historique des VISA d'une personne

## 3. Gestion des demandes
- [x] Créer une demande de transformation (VISA → Carte résident)
- [x] Créer une demande de duplicata (perte de carte)
- [x] Créer une demande de transfert de VISA
- [x] Créer une demande de transfert de carte
- [x] Mentionner "sans donné intérieur" si les données internes ne sont pas disponibles
- [x] Changer le statut d'une demande (en_attente, validée, rejetée, en_cours, annulée)
- [x] Lister les demandes en attente
- [x] Consulter l'historique des demandes d'une personne

## 4. Gestion des cartes résident
- [x] Générer une carte résident après validation d'une demande de transformation
- [x] Attribuer un numéro de carte unique
- [x] Définir la date d'expiration (ex: +2 ans)
- [x] Changer le statut d'une carte (actif, expiré, suspendu, perdu, en_transfert, renouvelé)
- [x] Faire un duplicata en cas de perte (sans VISA car la personne a encore son VISA)
- [x] Transférer une carte
- [x] Lister les cartes expirant dans 30 jours

## 5. Référentiels (statuts et listes déroulantes)
- [x] Gérer les statuts VISA (valide, expiré, transformé, annulé, en_cours_transfert)
- [x] Gérer les statuts carte résident (actif, expiré, suspendu, perdu, transfert, renouvelé)
- [x] Gérer les statuts demande (en_attente, en_cours, validée, rejetée, annulée)
- [x] Gérer les types de demande (transformation, duplicata, transfert_visa, transfert_carte)
- [x] Gérer les types de VISA/carte (travailleur, investisseur)
- [x] Gérer les nationalités
- [x] Gérer les professions
- [x] Gérer les situations familiales

## 6. Suivi et historique
- [ ] Tracer tous les changements de statut (VISA, carte, demande)
- [ ] Afficher l'historique des actions pour une entité donnée
- [ ] Savoir qui a traité une demande (traité_par)

## 7. Règles métier à implémenter
- [x] Un VISA transformable ne peut être transformé qu'une seule fois
- [x] Une demande de duplicata nécessite une carte existante avec statut "perdu"
- [x] Un transfert crée une nouvelle entité et désactive l'ancienne
- [x] Une demande sans "donné intérieur" doit être clairement identifiée
- [x] Seuls les types travailleurs et investisseurs sont gérés (pas étudiants)

## 8. API (backend) - Endpoints nécessaires
- [x] CRUD complet pour Personne
- [x] CRUD complet pour VISA
- [x] CRUD complet pour Demande
- [x] CRUD complet pour Carte résident
- [x] Endpoints pour Duplicata, Transfert VISA, Transfert carte
- [x] Endpoints pour tous les référentiels (statuts, types, nationalités...)
- [x] Endpoint de recherche multi-critères

## 9. APK Android - Écrans
- [ ] Écran d'accueil (menu principal)
- [ ] Écran de saisie personne + photo
- [ ] Écran de saisie VISA
- [ ] Écran de création de demande (avec case "sans donné intérieur")
- [ ] Écran de recherche
- [ ] Écran détail personne (avec onglets : infos, VISA, demandes, cartes)
- [ ] Écran liste des demandes en attente
- [ ] Écran liste des cartes expirant
- [ ] Écran de validation/traitement d'une demande
- [ ] Écran de gestion des référentiels (admin)

## 10. Livrables finaux
- [ ] Script SQL complet de la base de données
- [ ] Documentation API (Swagger ou fichier texte)
- [ ] Code source APK Android
- [ ] Guide d'installation et d'utilisation