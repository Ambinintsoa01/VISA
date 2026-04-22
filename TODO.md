# 📋 TODO - Fonctionnalités du projet VISA Transformable

## 1. Gestion des personnes (état civil)
- [ ] Saisir une nouvelle personne (nom, nom jeune fille, date/lieu naissance, situation familiale, nationalité, profession, adresse, contact, email)
- [ ] Ajouter une photo de la personne
- [ ] Rechercher une personne (par nom, email, numéro VISA, numéro carte)
- [ ] Modifier les informations d'une personne
- [ ] Consulter le détail d'une personne

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
- [ ] Générer une carte résident après validation d'une demande de transformation
- [ ] Attribuer un numéro de carte unique
- [ ] Définir la date d'expiration (ex: +2 ans)
- [ ] Changer le statut d'une carte (actif, expiré, suspendu, perdu, en_transfert, renouvelé)
- [ ] Faire un duplicata en cas de perte (sans VISA car la personne a encore son VISA)
- [ ] Transférer une carte
- [ ] Lister les cartes expirant dans 30 jours

## 5. Référentiels (statuts et listes déroulantes)
- [ ] Gérer les statuts VISA (valide, expiré, transformé, annulé, en_cours_transfert)
- [ ] Gérer les statuts carte résident (actif, expiré, suspendu, perdu, transfert, renouvelé)
- [ ] Gérer les statuts demande (en_attente, en_cours, validée, rejetée, annulée)
- [ ] Gérer les types de demande (transformation, duplicata, transfert_visa, transfert_carte)
- [ ] Gérer les types de VISA/carte (travailleur, investisseur)
- [ ] Gérer les nationalités
- [ ] Gérer les professions
- [ ] Gérer les situations familiales

## 6. Suivi et historique
- [ ] Tracer tous les changements de statut (VISA, carte, demande)
- [ ] Afficher l'historique des actions pour une entité donnée
- [ ] Savoir qui a traité une demande (traité_par)

## 7. Règles métier à implémenter
- [ ] Un VISA transformable ne peut être transformé qu'une seule fois
- [ ] Une demande de duplicata nécessite une carte existante avec statut "perdu"
- [ ] Un transfert crée une nouvelle entité et désactive l'ancienne
- [ ] Une demande sans "donné intérieur" doit être clairement identifiée
- [ ] Seuls les types travailleurs et investisseurs sont gérés (pas étudiants)

## 8. API (backend) - Endpoints nécessaires
- [ ] CRUD complet pour Personne
- [x] CRUD complet pour VISA
- [x] CRUD complet pour Demande
- [ ] CRUD complet pour Carte résident
- [ ] Endpoints pour Duplicata, Transfert VISA, Transfert carte
- [ ] Endpoints pour tous les référentiels (statuts, types, nationalités...)
- [ ] Endpoint de recherche multi-critères

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