# Objectifs d'apprentissage Home App

Ce document liste des fonctionnalites a implementer progressivement pour s'entrainer a construire une API Spring Boot Kotlin autour du projet Home App.

## Objectif principal

Ameliorer le backend Home App en pratiquant les sujets importants d'une API moderne :

- REST controllers
- services metier
- repositories Spring Data JPA
- relations entre entites
- DTO request / response
- validation
- gestion globale des erreurs
- pagination
- tests d'integration
- integrations MQTT

## Progression recommandee

### 1. Recuperer les devices d'une room

Implementer un endpoint dedie :

```http
GET /api/rooms/{roomId}/devices
```

Objectifs techniques :

- manipuler une relation `Room -> Device`
- ecrire une query method Spring Data JPA
- retourner une liste de `DeviceResponse`
- tester un cas simple de filtre par relation

### 2. Recuperer le dernier releve d'un device

Implementer un endpoint du type :

```http
GET /api/devices/{id}/latest-reading
```

Objectifs techniques :

- recuperer le dernier report temperature / humidite
- utiliser un tri par date descendante
- creer une response metier combinee
- gerer le cas ou aucun releve n'existe

### 3. Ajouter un historique filtre des mesures

Implementer des endpoints avec query params :

```http
GET /api/devices/{id}/temperature-reports?from=2026-01-01T00:00:00&to=2026-01-31T23:59:59
GET /api/devices/{id}/humidity-reports?from=2026-01-01T00:00:00&to=2026-01-31T23:59:59
```

Objectifs techniques :

- lire des query params
- filtrer par periode
- pratiquer les types date / heure Kotlin et Java Time
- structurer les methodes de repository

### 4. Calculer des statistiques par room

Implementer un endpoint :

```http
GET /api/rooms/{id}/stats
```

Objectifs techniques :

- calculer moyenne, minimum et maximum de temperature
- calculer moyenne, minimum et maximum d'humidite
- agreger des donnees venant de plusieurs devices
- construire un DTO de statistiques

### 5. Ajouter des seuils et alertes

Implementer des regles d'alerte :

```http
POST /api/alert-rules
GET /api/alerts
```

Exemples :

- temperature superieure a 30 degres
- humidite inferieure a 35 pourcent
- device offline depuis trop longtemps

Objectifs techniques :

- modeliser une nouvelle entite metier
- verifier des conditions apres creation d'un report
- separer la logique d'alerte dans un service dedie
- tester des regles metier

### 6. Ajouter validation et erreurs propres

Ameliorer les requests avec Bean Validation :

- `@NotBlank`
- `@NotNull`
- `@Min`
- `@Max`
- `@Valid`

Ajouter une gestion globale avec :

```kotlin
@ControllerAdvice
```

Objectifs techniques :

- retourner des erreurs API coherentes
- eviter la duplication dans les controllers
- gerer les erreurs de validation
- gerer les ressources non trouvees

### 7. Ajouter la pagination

Implementer la pagination sur les listes volumineuses :

```http
GET /api/temperature-reports?page=0&size=20
GET /api/humidity-reports?page=0&size=20
```

Objectifs techniques :

- utiliser `Pageable`
- retourner une response paginee
- trier les donnees par date
- comprendre `Page<T>` avec Spring Data JPA

### 8. Ajouter les commandes vers devices

Implementer un endpoint pour envoyer des commandes :

```http
POST /api/devices/{id}/commands
```

Exemples :

- allumer une lumiere
- eteindre une lumiere
- changer une consigne
- demander un refresh de mesure

Objectifs techniques :

- creer une entite `DeviceCommand`
- publier un message MQTT sortant
- historiser les commandes envoyees
- gerer le statut d'une commande

### 9. Ajouter utilisateurs, maisons et droits d'acces

Faire evoluer le modele vers :

```text
User -> Home -> Room -> Device
```

Objectifs techniques :

- pratiquer des relations JPA plus riches
- preparer une future authentification
- filtrer les donnees par utilisateur
- mieux structurer les frontieres metier

### 10. Ajouter authentification JWT

Proteger l'API avec Spring Security :

```http
POST /api/auth/register
POST /api/auth/login
```

Objectifs techniques :

- comprendre Spring Security
- generer et valider un JWT
- proteger les endpoints
- recuperer l'utilisateur courant

## Ordre conseille

1. Recuperer les devices d'une room
2. Recuperer le dernier releve d'un device
3. Calculer des statistiques par room
4. Ajouter des seuils et alertes
5. Ajouter validation et erreurs propres
6. Ajouter la pagination
7. Ajouter les commandes MQTT sortantes
8. Ajouter utilisateurs, maisons et authentification

## Critere de qualite pour chaque objectif

Pour considerer un objectif comme termine :

- endpoint REST fonctionnel
- request / response claire
- logique metier dans le service, pas dans le controller
- repository limite a l'acces aux donnees
- erreurs gerees proprement
- au moins un test utile si la fonctionnalite touche de la logique metier
