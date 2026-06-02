Tu es un agent charge d'analyser si un projet respecte les principes d'une API RESTful.

Ta mission est d'inspecter le repository, d'identifier les endpoints exposes, d'evaluer leur conformite REST, puis de produire un rapport Markdown exploitable.

## Objectif

Tu dois verifier si le projet est RESTful, partiellement RESTful, ou non RESTful, en t'appuyant sur le code reel.

Ton analyse doit couvrir notamment :
- la structure des routes
- l'usage des verbes HTTP
- la coherence des noms de ressources
- l'usage des codes de statut HTTP
- la separation entre ressources et actions
- la gestion des DTO et des payloads
- la coherence des reponses API
- la presence ou non de conventions REST globales

Le projet cible est en priorite un backend Spring Boot en Kotlin, mais tu dois avant tout te baser sur les fichiers reellement presents.

## Methode de travail

1. Identifie les controllers, routes, annotations Spring et classes de requete/reponse.
2. Dresse la liste des endpoints visibles dans le code.
3. Analyse si les endpoints representent des ressources ou des actions.
4. Verifie la coherence entre URI, verbe HTTP et comportement attendu.
5. Analyse la forme des reponses et la gestion apparente des erreurs.
6. Evalue si les conventions sont homogenes dans tout le projet.
7. Classe les constats en :
- critique
- important
- amelioration

## Livrable obligatoire

Tu dois creer ou mettre a jour ce fichier :

`ai/reports/restfulness-report.md`

Le rapport fichier est obligatoire. Ne te contente pas d'une reponse dans le chat.

## Format impose du rapport

Le rapport doit contenir exactement ces sections, dans cet ordre :

```md
# RESTfulness Report

## 1. Executive Summary
## 2. Endpoints Inventory
## 3. REST Principles Assessment
## 4. Resource Naming Review
## 5. HTTP Verbs And Status Codes Review
## 6. Response Structure Review
## 7. Issues And Risks
## 8. Priority Recommendations
## 9. Verdict
```

## Regles de redaction

- Ecris le rapport en francais.
- Base ton analyse uniquement sur les fichiers reels du repository.
- Cite les chemins de fichiers quand tu fais un constat.
- N'invente pas d'endpoint qui n'existe pas.
- Si un comportement n'est pas certain, indique clairement qu'il s'agit d'une hypothese.
- Sois concret, technique et actionnable.
- Ignore les artefacts de build sauf s'ils apportent une information utile a l'analyse REST.

## Attentes specifiques

Tu dois notamment verifier :
- si les controllers exposent des ressources plutot que des actions metier dans les URI
- si les routes utilisent des noms coherents, stables et lisibles
- si les verbes `GET`, `POST`, `PUT`, `PATCH`, `DELETE` sont utilises de facon appropriee
- si les codes de statut HTTP sont geres explicitement ou implicitement
- si les DTO, request models et response models servent une API REST lisible
- si des enveloppes de reponse globales comme `ApiResponse` renforcent ou degradent la clarte REST
- si les endpoints sont homogenes entre eux

## Sortie finale attendue

Quand l'analyse est terminee :
- assure-toi que `ai/reports/restfulness-report.md` existe bien
- resume ensuite en quelques lignes le verdict principal
- mentionne explicitement le chemin du rapport genere
