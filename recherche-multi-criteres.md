# Recherche Multi-Critères dans le Code

## Aperçu Général

La fonctionnalité de **recherche multi-critères** permet d'effectuer des recherches croisées sur plusieurs entités liées du système (Personne, Visa, Demande, CarteResident). Elle est exposée via un endpoint REST et utilise une approche modulaire avec un service orchestrateur.

**Fichiers principaux impliqués :**
- `RechercheRestController.java` : Contrôleur REST
- `MultiCritereSearchService.java` : Service central
- `MultiCritereSearchResult.java` : Modèle de réponse
- Services et repositories : `PersonneService/Repository`, `VisaService/Repository`, etc.

## 1. Endpoint REST (RechercheRestController.java)

**URL :** `GET /api/recherche/multi-criteres`

**Paramètres d'entrée (tous optionnels sauf indication) :**
| Paramètre          | Type    | Description |
|--------------------|---------|-------------|
| `nom`             | String | Nom de la personne (LIKE `%nom%`) |
| `email`           | String | Email de la personne (LIKE `%email%`) |
| `numVisa`         | String | Numéro de visa (LIKE `%numVisa%`) |
| `numCarte`        | String | Numéro de carte résident (LIKE `%numCarte%`) |
| `idPersonne`      | Long   | ID personne |
| `idStatutVisa`    | Integer| ID statut visa |
| `idStatutDemande` | Integer| ID statut demande |
| `idTypeDemande`   | Integer| ID type demande |
| `idStatutCarte`   | Integer| ID statut carte |
| `sansDonneInterieur` | Boolean | Exclure données intérieures ? |

**Réponse :** `MultiCritereSearchResult` avec listes et totaux.

**Exemple d'appel :**
```
GET /api/recherche/multi-criteres?nom=Dupont&numVisa=123&email=exemple@gmail.com
```

## 2. Modèle de Réponse (MultiCritereSearchResult.java)

```java
public class MultiCritereSearchResult {
    private List<Personne> personnes;
    private List<Visa> visas;
    private List<Demande> demandes;
    private List<CarteResident> cartesResident;
    
    private Integer totalPersonnes;
    private Integer totalVisas;
    private Integer totalDemandes;
    private Integer totalCartesResident;
    // getters/setters
}
```

## 3. Orchestration (MultiCritereSearchService.java)

Le service central appelle les recherches spécifiques sur chaque entité :

```java
public MultiCritereSearchResult search(String nom, String email, String numVisa, String numCarte, 
                                      Long idPersonne, Integer idStatutVisa, Integer idStatutDemande, 
                                      Integer idTypeDemande, Integer idStatutCarte, Boolean sansDonneInterieur) {
    
    List<Personne> personnes = personneService.search(nom, email, numVisa, numCarte);
    List<Visa> visas = visaService.search(numVisa, idPersonne, idStatutVisa);
    List<Demande> demandes = demandeService.search(idPersonne, idStatutDemande, idTypeDemande, sansDonneInterieur);
    List<CarteResident> cartes = carteResidentService.search(numCarte, idPersonne, idStatutCarte);
    
    // Aggregation dans MultiCritereSearchResult
    return result;
}
```

## 4. Implémentations dans les Repositories

### PersonneRepository.search() - Recherche pivot
```sql
SELECT DISTINCT p.*
FROM personne p
LEFT JOIN visa v ON v.id_personne = p.id_personne
LEFT JOIN carte_resident c ON c.id_personne = p.id_personne
WHERE (? IS NULL OR LOWER(p.nom) LIKE LOWER('%' || ? || '%'))
  AND (? IS NULL OR LOWER(p.email) LIKE LOWER('%' || ? || '%'))
  AND (? IS NULL OR LOWER(v.num_visa) LIKE LOWER('%' || ? || '%'))
  AND (? IS NULL OR LOWER(c.num_carte) LIKE LOWER('%' || ? || '%'))
ORDER BY p.id_personne DESC
```
- **Critères combinés** sur personne + jointures visa/carte
- **LIKE insensible à la casse** avec wildcards `%`

Les autres repositories (VisaRepository, DemandeRepository, CarteResidentRepository) ont des méthodes `search()` similaires avec filtres sur leurs champs spécifiques + ID personne.

## 5. Flux Complet

```
Client → RechercheRestController → MultiCritereSearchService 
→ [PersonneService → PersonneRepository.search() | VisaService → VisaRepository.search() | ...]
→ MultiCritereSearchResult → JSON Response
```

## 6. Points Forts
- ✅ **Flexible** : Paramètres optionnels (NULL = ignorer critère)
- ✅ **Performant** : Requêtes SQL optimisées avec LEFT JOIN et DISTINCT
- ✅ **Modulaire** : Chaque entité gère sa logique de recherche
- ✅ **Agrégé** : Résultats croisés en une seule réponse

## 7. Améliorations Possibles
- Pagination pour gros volumes
- Index DB sur champs de recherche (nom, email, num_visa, num_carte)
- Cache Redis pour recherches fréquentes
- Validation @Valid sur contrôleur
- Recherche full-text avec PostgreSQL `tsvector`

## 8. Tests
Pour tester :
```bash
curl \"http://localhost:8080/api/recherche/multi-criteres?nom=Dupont\"
```

Ce fichier `recherche-multi-criteres.md` résume complètement l'implémentation. Il sera créé à la racine du projet.
