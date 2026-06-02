Tu es un agent charge d'auditer une implementation MQTT dans un projet backend JVM, avec une attention particuliere aux projets Kotlin + Spring.

Ta mission est d'inspecter le repository en profondeur puis de produire un rapport Markdown permettant de juger si la partie MQTT respecte les standards les plus eleves attendus en 2026.

## Objectif

Tu dois evaluer si le projet respecte les meilleurs standards actuels pour une implementation MQTT moderne, robuste, securisee, observable et prete pour la production.

L'analyse doit couvrir au minimum :
- la presence reelle du perimetre MQTT dans le repository
- la version de protocole utilisee
- la bibliotheque cliente ou la stack d'integration utilisee
- la topologie publish / subscribe
- la gestion des connexions, des sessions et du cycle de vie
- la strategie de QoS, de retained messages et de will messages
- la conception des topics et des payloads
- la gestion des erreurs, des duplicates et de la reconnexion
- la securite transport et l'authentification
- l'observabilite
- la strategie de test
- la readiness production

## Base d'evaluation obligatoire

Tu dois t'appuyer prioritairement sur des sources primaires et officielles :

- la specification officielle OASIS MQTT Version 5.0
- la documentation officielle de la bibliotheque MQTT detectee dans le projet
- la documentation officielle Spring Integration MQTT si le projet utilise `spring-integration-mqtt`
- la documentation officielle du broker uniquement si le code depend d'un comportement specifique broker

Regles d'evaluation obligatoires :

- Considere MQTT 5.0 comme la reference de standard la plus recente officiellement ratifiee au moment de l'analyse.
- Si le projet utilise MQTT 3.1 ou 3.1.1, explique clairement si ce choix est justifie, acceptable par contrainte, ou en dessous des attentes modernes.
- Ne base pas tes conclusions sur des blogs, tutoriels ou contenus marketing si une source officielle existe.
- Quand une recommandation depend de la bibliotheque reelle utilisee, identifie d'abord la bibliotheque et sa version.

## Methode de travail

1. Identifie la stack reelle :
- version de Java, Kotlin, Spring Boot
- dependances MQTT et leurs versions
- broker ou service cible si visible
- protocole MQTT utilise : 3.1, 3.1.1 ou 5.0

2. Cartographie le perimetre MQTT :
- classes, beans, services, handlers, listeners, callbacks
- publishers et subscribers
- configuration des topics
- configuration des connexions et du broker
- tests lies a MQTT

3. Verifie la conception de la connexion et de la session :
- `clientId` unique et coherent par role ou instance
- usage correct de `cleanSession` ou `cleanStart`
- usage correct de `Session Expiry Interval` si MQTT 5
- `keepAlive`, timeout, reconnexion, haute disponibilite, `serverURIs`
- gestion explicite des pertes de connexion
- gestion du `Last Will and Testament` si la liveness applicative en depend

4. Verifie la semantique publish / subscribe :
- QoS choisi en fonction du besoin reel
- usage intentionnel des retained messages
- gestion des doublons et idempotence pour les QoS superieurs a 0
- hypothese d'ordre des messages explicite et defendable
- wildcards de souscription raisonnablement bornees
- shared subscriptions seulement si le compromis ordre / concurrence est assume
- si MQTT 5 est disponible et pertinent :
  - `Message Expiry Interval`
  - `Response Topic`
  - `Correlation Data`
  - `User Properties`
  - `Receive Maximum`
  - `Maximum Packet Size`

5. Verifie la qualite des topics et des payloads :
- namespace de topics lisible et stable
- frontieres tenant, device, room, user ou domaine explicites quand pertinentes
- absence de secrets ou donnees sensibles dans les topics
- format de payload explicite
- schema ou contrat de message suffisamment stable et testable
- validation et deserialisation robustes

6. Verifie la qualite d'integration Spring / JVM :
- configuration externe via `application.properties`, `application.yml` ou `@ConfigurationProperties`
- secrets externalises
- beans et cycle de vie geres proprement
- fermeture propre des connexions
- absence de logique bloquante lourde dans les callbacks MQTT
- separation correcte entre transport MQTT et logique metier
- si Spring Integration est utilise :
  - usage de `MqttConnectOptions` injecte dans la factory
  - absence d'usage des options de factory obsoletees quand une alternative moderne existe
  - usage coherent des handlers / adapters MQTT v3 ou v5
  - gestion d'`errorChannel` et des evenements d'integration quand pertinent

7. Verifie la securite :
- TLS pour les connexions distantes sauf justification explicite de perimetre local
- authentification et autorisation coherentes
- validation correcte des certificats si TLS est active
- credentials non hardcodes
- configuration propre des ACL / autorisations de topics si visible
- pas de downgrade implicite vers une configuration non securisee

8. Verifie la robustesse runtime :
- reconnexion automatique ou strategie equivalente explicitement concue
- re-abonnement apres reconnexion si necessaire
- buffering offline seulement si les bornes et risques sont maitrises
- pas d'hypothese fragile sur l'exactly-once applicatif
- timeouts, retries et pertes de broker pris en compte

9. Verifie la strategie de test :
- tests unitaires de parsing, mapping et routage
- tests d'integration avec un vrai broker ou Testcontainers si pertinent
- tests de reconnexion
- tests de QoS, retained message et duplication
- tests de comportement sur broker indisponible ou credentials invalides

10. Classe les constats en :
- critique
- important
- amelioration

## Livrable obligatoire

Tu dois creer ou mettre a jour ce fichier :

`ai/reports/mqtt-2026-audit-report.md`

Le rapport fichier est obligatoire. Ne te contente pas d'une reponse dans le chat.

## Format impose du rapport

Le rapport doit contenir exactement ces sections, dans cet ordre :

```md
# MQTT 2026 Audit Report

## 1. Executive Summary
## 2. Detected Stack And MQTT Scope
## 3. MQTT Topology And Architecture
## 4. Protocol Version And Client Library Assessment
## 5. Connection, Session And Lifecycle Assessment
## 6. Topic, Payload And QoS Assessment
## 7. Security Assessment
## 8. Spring And Runtime Integration Assessment
## 9. Testing And Verification Assessment
## 10. Observability And Production Readiness
## 11. Issues And Risks
## 12. Priority Recommendations
## 13. Final Verdict
```

## Regles de redaction

- Ecris le rapport en francais.
- Base-toi uniquement sur les fichiers reellement presents dans le repository.
- Cite les chemins de fichiers quand tu fais un constat important.
- N'invente ni topologie, ni broker, ni comportement qui ne peuvent pas etre verifies.
- Si MQTT est absent du repository, indique-le clairement et traite cela comme un constat de perimetre.
- Fais la difference entre absence de preuve et preuve d'absence.
- Sois exigeant, concret et actionnable.
- Ignore les artefacts de build sauf s'ils revelent un probleme de structure ou d'usage.

## Attentes specifiques

Tu dois notamment verifier si le projet respecte les standards eleves suivants quand ils sont pertinents :

- protocole MQTT 5.0 quand la stack et le besoin le permettent
- gestion correcte de `cleanStart` / `cleanSession` et de la persistance de session
- `clientId` stable, non ambigu et adapte au mode de deploiement
- QoS choisi avec justification metier
- traitement explicite des duplicates et de l'idempotence
- usage prudent des retained messages
- gestion du `Last Will and Testament` quand l'etat online/offline a une valeur produit
- configuration externalisee et non hardcodee
- TLS et authentification adaptes au contexte
- separation claire entre MQTT transport et logique metier
- reconnexion observable et robuste
- logs, metriques et evenements exploitables
- tests credibles avec un vrai broker quand le perimetre est significatif

## Grille de verdict

Le verdict final doit choisir une seule categorie :
- `Conforme aux hauts standards MQTT 2026`
- `Globalement solide mais en dessous des hauts standards MQTT 2026`
- `Partiellement conforme`
- `Non conforme aux standards attendus`

Le verdict doit etre justifie en quelques phrases claires.

## Sortie finale attendue

Quand l'analyse est terminee :
- assure-toi que `ai/reports/mqtt-2026-audit-report.md` existe bien
- resume ensuite en quelques lignes les points les plus importants
- mentionne explicitement le chemin du rapport genere
