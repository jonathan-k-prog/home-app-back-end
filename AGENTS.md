# Instructions Projet

## Fichiers de contexte
- Toujours lire `PROJECT_CONTEXT.md` avant d'analyser, modifier ou expliquer le projet.
- Utiliser `PROJECT_CONTEXT.md` comme source prioritaire pour comprendre la structure, les modules, les conventions et les commandes du projet.

## Comportement attendu
- Si la demande de l'utilisateur porte sur une zone deja documentee, ne pas reanalyser toute l'application. Partir d'abord du contexte existant.
- Si un element de structure, de flux, de convention ou d'architecture manque dans `PROJECT_CONTEXT.md`, l'identifier pendant l'analyse.
- Quand une information manquante est stable et utile pour les prochaines sessions, mettre `PROJECT_CONTEXT.md` a jour dans la meme intervention.
- Ne pas ajouter d'informations temporaires ou bruyantes dans `PROJECT_CONTEXT.md` :
  - logs d'execution
  - erreurs ponctuelles
  - details de debug temporaires
  - hypotheses non confirmees

## Regle de mise a jour automatique
- A chaque demande, verifier silencieusement si `PROJECT_CONTEXT.md` couvre deja la zone concernee.
- Si ce n'est pas le cas, completer automatiquement `PROJECT_CONTEXT.md` apres verification dans le code.
- Limiter les mises a jour aux informations durables :
  - nouveaux modules
  - organisation des packages
  - endpoints principaux
  - dependances structurantes
  - conventions de code ou de nommage
  - commandes projet
  - integrations externes

## Priorites
1. Lire `PROJECT_CONTEXT.md`
2. Repondre ou intervenir sur la demande
3. Enrichir `PROJECT_CONTEXT.md` si une info structurelle manquait

## Style de maintenance
- Garder `PROJECT_CONTEXT.md` court, factuel et facile a rescanner.
- Preferer des sections stables et explicites.
- Mettre a jour l'existant au lieu d'empiler des notes redondantes.
