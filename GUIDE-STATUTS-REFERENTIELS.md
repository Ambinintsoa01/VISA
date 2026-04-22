# Guide des statuts et référentiels

Ce document explique:
- comment récupérer la liste des statuts par table
- comment utiliser l'API existante pour alimenter les listes déroulantes
- la signification des statuts utilisés dans le projet

## 1. Tables de statuts (base de données)

Les statuts sont stockés dans ces tables:
- `statut_visa_ref`
- `statut_carte_ref`
- `statut_demande_ref`

Chaque table suit la même logique:
- `code`: identifiant technique (ex: `valide`, `en_attente`)
- `libelle`: texte affiché à l'utilisateur
- `description`: explication métier
- `ordre_affichage`: ordre dans la liste déroulante
- `actif`: statut disponible ou non

## 2. Lister les statuts via SQL

### 2.1 Statuts VISA
```sql
SELECT id_statut_visa, code, libelle, description, ordre_affichage, actif
FROM statut_visa_ref
ORDER BY ordre_affichage, libelle;
```

### 2.2 Statuts Carte Résident
```sql
SELECT id_statut_carte, code, libelle, description, ordre_affichage, actif
FROM statut_carte_ref
ORDER BY ordre_affichage, libelle;
```

### 2.3 Statuts Demande
```sql
SELECT id_statut_demande, code, libelle, description, ordre_affichage, actif
FROM statut_demande_ref
ORDER BY ordre_affichage, libelle;
```

## 3. Lister les statuts via API (recommandé pour le front)

Le projet expose des endpoints référentiels.

### 3.1 Tous les référentiels actifs (en une seule requête)
```http
GET /api/referentiels
```

Réponse: map par type (`statut-visa`, `statut-carte`, `statut-demande`, etc.).

### 3.2 Un type précis
```http
GET /api/referentiels/{type}
```

Exemples:
- `GET /api/referentiels/statut-visa`
- `GET /api/referentiels/statut-carte`
- `GET /api/referentiels/statut-demande`

Option:
- `actifOnly=true` (par défaut): ne retourne que les lignes actives
- `actifOnly=false`: retourne aussi les lignes inactives

Exemple:
```http
GET /api/referentiels/statut-demande?actifOnly=false
```

## 4. Ajouter un nouveau statut

Vous pouvez créer un nouveau statut (ou type) avec l'API référentiels.

### 4.1 Nouveau statut VISA
```http
POST /api/referentiels/statut-visa
Content-Type: application/json

{
  "code": "suspendu_temp",
  "libelle": "Suspendu temporaire",
  "description": "Statut provisoire pour contrôle",
  "ordreAffichage": 99,
  "actif": true
}
```

### 4.2 Nouveau statut Carte
```http
POST /api/referentiels/statut-carte
Content-Type: application/json

{
  "code": "bloque",
  "libelle": "Bloqué",
  "description": "Carte bloquée administrativement",
  "ordreAffichage": 99,
  "actif": true
}
```

### 4.3 Nouveau statut Demande
```http
POST /api/referentiels/statut-demande
Content-Type: application/json

{
  "code": "a_completer",
  "libelle": "A compléter",
  "description": "Demande incomplète",
  "ordreAffichage": 99,
  "actif": true
}
```

## 5. Signification des statuts actuels

### 5.1 Statuts VISA
- `valide`: VISA en cours de validité
- `expire`: VISA expiré
- `transforme`: VISA déjà transformé en carte résident
- `annule`: VISA annulé
- `en_cours_transfert`: VISA en cours de transfert

### 5.2 Statuts Carte Résident
- `actif`: carte valide et utilisable
- `expire`: carte expirée
- `suspendu`: carte temporairement suspendue
- `perdu`: carte déclarée perdue
- `transfert`: ancienne carte en état de transfert
- `renouvele`: carte renouvelée

### 5.3 Statuts Demande
- `en_attente`: demande déposée, pas encore traitée
- `en_cours`: demande en traitement
- `validee`: demande validée
- `rejetee`: demande refusée
- `annulee`: demande annulée

## 6. Types référentiels supportés par l'API

Valeurs acceptées pour `{type}`:
- `statut-visa`
- `statut-carte`
- `statut-demande`
- `type-demande`
- `type-visa-voulu`
- `type-carte`
- `situation-familiale`
- `nationalite`
- `profession`

## 7. Bonnes pratiques

- Toujours utiliser le champ `code` pour la logique métier.
- Afficher `libelle` dans l'interface utilisateur.
- Utiliser `actif=false` pour retirer un statut d'une liste sans supprimer l'historique.
- Éviter de supprimer des statuts déjà utilisés dans des enregistrements existants.
