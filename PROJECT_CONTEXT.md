# Project Context

## Identite du projet
- Nom applicatif Spring : `homeApp`
- Type : backend Kotlin / Spring Boot
- Langages : Kotlin, configuration Gradle Kotlin DSL
- Java cible : 21

## Stack principale
- Spring Boot `4.0.6`
- Spring Web
- Spring Data JPA
- PostgreSQL en runtime principal (driver: `42.7.11` — CVE-2026-42198 patché)
- H2 en runtime de test
- MQTT via Eclipse Paho
- Jackson Kotlin
- Tomcat Embed `10.1.55` (core, el, websocket)

## Structure racine
- `src/main/kotlin/com/back/homeapp` : code source principal
- `src/main/resources` : configuration applicative
- `src/test/kotlin/com/back/homeapp` : tests
- `docker-compose.yml` : orchestration locale app + PostgreSQL + broker MQTT
- `Dockerfile` : image de l'application
- `build.gradle.kts` : build, dependances, versioning plugins
- `OBJECTIVES.md` : roadmap d'objectifs d'apprentissage pour faire evoluer l'API

## Packages backend
- `apiResponse` : wrapper de reponse API commun
- `config` : configuration Spring, CORS et Jackson
- `config` : configuration Spring, CORS, Jackson et clients HTTP partages
- `device` : domaine devices avec controller, service, repository, request, response, entity
- `room` : domaine rooms avec controller, service, repository, request, response, entity
- `humidityReport` : domaine des releves d'humidite
- `temperatureReport` : domaine des releves de temperature
- `weather` : integration WeatherAPI avec controller, service et reponse associee
- `mqtt` : abonnement MQTT et transformation des messages entrants en rapports humidite / temperature
- `globalExecption` : gestion globale des erreurs API via `RestControllerAdvice`

## Architecture metier
- Organisation par domaine, avec dossiers dedies par fonctionnalite.
- Les domaines CRUD suivent en general cette structure :
  - `*Controller`
  - `*Service`
  - `*Repository`
  - `*Request`
  - `*Response`
  - entite JPA
- Les controllers REST renvoient majoritairement `ApiResponse<T>` encapsule dans `ResponseEntity`.

## Endpoints identifies
- `GET/POST /api/rooms`
- `GET/PUT/DELETE /api/rooms/{id}`
- `GET/POST /api/devices`
- `GET /api/devices/roomId/{roomId}`
- `GET/PUT/DELETE /api/devices/{id}`
- `GET /api/weather`
- Les modules `humidityReport` et `temperatureReport` possedent aussi leurs propres controllers REST.

## Flux MQTT
- Service principal : `mqtt/MqttSubscriberService.kt`
- Activation conditionnelle via `mqtt.enabled`
- Broker par defaut : `tcp://localhost:1883`
- Topic par defaut : `esp32/dht11`
- Le subscriber lit un payload JSON mappe sur `SensorReadingMessage`
- Chaque message cree :
  - un `HumidityReportRequest`
  - un `TemperatureReportRequest`
- Les donnees sont ensuite persistees via `HumidityReportService` et `TemperatureReportService`

## Base de donnees et configuration
- Datasource par defaut : `jdbc:postgresql://localhost:5432/home_app`
- Identifiants par defaut : `postgres` / `postgres`
- Driver : PostgreSQL
- Hibernate DDL : `update`
- Affichage SQL actif : `spring.jpa.show-sql=true`

## Variables de configuration importantes
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `MQTT_ENABLED`
- `MQTT_BROKER_URL`
- `MQTT_CLIENT_ID`
- `MQTT_TOPIC`
- `WEATHER_API`
- `WEATHER_BASE_URL`
- `WEATHER_LOCATION`
- `GOOGLE_GENAI_API_KEY`

## Execution locale
- Lancement Gradle habituel : `./gradlew bootRun`
- Sous Windows PowerShell : `.\gradlew.bat bootRun`
- Tests : `.\gradlew.bat test`
- Le port HTTP configure par defaut est `8080`

## Docker Compose local
- Service `app` : backend Spring Boot expose sur `8080`
- Service `postgres` : PostgreSQL 16 expose sur `5432`
- Service `mqtt` : Eclipse Mosquitto 2 expose sur `1883`

## Etat actuel des tests visibles
- Test de chargement Spring : `HomeAppApplicationTests`
- Test d'integration domaine room : `RoomServiceIntegrationTest`
- Test d'integration domaine device (MockMvc + JWT reel via `JwtService`) : `DeviceControllerIntegrationTest`

## Probleme connu non resolu : mismatch Jackson 2.x / 3.x
- `build.gradle.kts` importe `tools.jackson:jackson-bom:3.1.4` (Jackson 3.x, utilise reellement par l'`ObjectMapper` de Spring Boot 4.x).
- `implementation("com.fasterxml.jackson.module:jackson-module-kotlin")` resout vers la coordonnee Jackson **2.x** (`com.fasterxml.jackson.module:jackson-module-kotlin:2.21.2`, qui tire `com.fasterxml.jackson.core:jackson-databind:2.21.2`).
- Consequence : le support Kotlin (valeurs par defaut de parametres, non-nullabilite) n'est pas applique sur le vrai `ObjectMapper` Jackson 3.x. Un champ primitif avec valeur par defaut Kotlin (ex. `connected: Boolean = false` dans `DeviceRequest`) absent du JSON provoque une `HttpMessageNotReadableException` ("Cannot map `null` into type `boolean`") au lieu d'utiliser le defaut.
- Impact reel (pas seulement les tests) : tout client omettant un champ optionnel dans une requete de creation recoit un 400.
- Non corrige pour le moment (decision explicite le 2026-07-23) : il faut trouver/valider la bonne coordonnee du module Kotlin compatible Jackson 3.x avant de remplacer la dependance.

## Conventions utiles pour les prochaines interventions
- Commencer par verifier `PROJECT_CONTEXT.md` avant d'explorer le code.
- Si une nouvelle zone du projet est analysee et qu'elle est structurelle, enrichir ce fichier.
- Ne pas utiliser le dossier `build/` comme source de verite fonctionnelle ; se baser sur `src/`.
- Bean Validation est disponible via `spring-boot-starter-validation`.
- Les contraintes de validation sont portees par les `*Request` avec annotations `jakarta.validation`.
- Les controllers doivent annoter les corps de requete avec `@Valid @RequestBody` pour declencher la validation.
- Les erreurs de validation sont centralisees dans `globalExecption/GlobalExceptionHandler.kt` via `RestControllerAdvice`.
- Les champs enum des `*Request` doivent utiliser `@NotNull` plutot que `@NotBlank` / `@Size`.
- Les corps JSON illisibles ou avec une valeur enum non supportee sont convertis en reponse 400 par `GlobalExceptionHandler`.
