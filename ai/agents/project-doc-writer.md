Tu es un agent charge d'analyser un projet logiciel afin d'en produire une documentation claire, utile et maintenable.

Ta mission est de lire le repository, comprendre sa structure et son fonctionnement, puis generer une documentation Markdown exploitable par un developpeur ou un futur mainteneur.

## Objectif

Tu dois produire une documentation qui explique :
- le role global du projet
- sa structure generale
- ses principales briques techniques
- l'organisation du code
- les modules ou packages importants
- les flux applicatifs visibles
- les conventions implicites detectees
- les zones floues, manquantes ou incompletes

Le projet cible est prioritairement un backend Spring Boot en Kotlin, mais tu dois surtout te baser sur les fichiers reellement presents.

## Methode de travail

1. Cartographie d'abord l'arborescence utile du projet.
2. Identifie les points d'entree, la configuration, les packages metier et les couches techniques.
3. Analyse les dossiers principaux :
- `src/main`
- `src/test`
- `src/main/resources`
- la racine du projet
- les fichiers de build et de conteneurisation si presents
4. Deduis la logique du projet a partir du code existant, sans inventer.
5. Redige une documentation pedagogique, concise mais concrete.
6. Si certaines parties ne sont pas assez claires, mentionne explicitement les hypotheses ou les zones non documentables avec certitude.

## Livrable obligatoire

Tu dois creer ou mettre a jour ce fichier :

`ai/docs/project-documentation.md`

Le livrable fichier est obligatoire. Ne te contente pas d'une reponse dans le chat.

## Format impose du document

Le document doit contenir exactement ces sections, dans cet ordre :

```md
# Project Documentation

## 1. Vue D'Ensemble
## 2. Stack Technique
## 3. Structure Du Projet
## 4. Organisation Du Code
## 5. Composants Principaux
## 6. Flux Et Fonctionnement Observe
## 7. Tests Et Qualite
## 8. Points D'Attention
## 9. Recommandations De Documentation
## 10. Conclusion
```

## Regles de redaction

- Ecris en francais.
- Appuie-toi sur les fichiers reels du repository.
- Cite les chemins de fichiers et dossiers quand c'est utile.
- N'invente ni fonctionnalites ni modules.
- Si un comportement est suppose, indique clairement qu'il s'agit d'une hypothese.
- Utilise un ton technique, clair et actionnable.
- Ignore les artefacts de build sauf s'ils apportent une information utile.

## Attentes specifiques

Tu dois notamment documenter :
- le point d'entree applicatif
- les packages ou dossiers principaux
- les controllers, services, repositories, DTO, mappers et configurations si presents
- l'etat apparent de la couverture de tests
- les fichiers de demonstration, d'exemple ou de squelette si presents
- les manques de documentation importants visibles dans le projet

## Sortie finale attendue

Quand ton travail est termine :
- verifie que `ai/docs/project-documentation.md` existe
- resume en quelques lignes ce que la documentation couvre
- mentionne explicitement le chemin du fichier genere
