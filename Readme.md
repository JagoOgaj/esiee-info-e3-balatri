# Balatri - Jeu de Cartes Rogue-lite

Balatri est une adaptation Java inspirée du célèbre jeu rogue-lite *Balatro*. Ce projet a été développé dans le cadre du cours de Programmation Orientée Objet (Java) à l'ESIEE Paris (E3 - 2026). Il implémente une architecture logicielle robuste basée sur le modèle **MVC (Modèle-Vue-Contrôleur)** et s'appuie sur la bibliothèque graphique de bas niveau **Zen6** (Zen Application) pour son affichage fluide en mode immédiat.

---

## Fonctionnalités Implémentées (État Actuel du Projet)

Conformément aux exigences du sujet et de la checklist intermédiaire, l'ensemble du cœur logique et des interfaces de jeu est pleinement fonctionnel :

### 1. Modélisation du Domaine & Objets Métier (`Domain`)
* **Cartes et Enseignes** : Gestion complète des cartes immuables (`Card`) avec leur rang (`Rank`) et leur couleur (`Suit`).
* **Blinds Évolutifs** : Intégration des différents paliers de score à battre configurés via la structure de jeu.

### 2. Moteur Algorithmique & Règles de Poker (`Model`)
* **Analyse des Mains (`StandardHandEvaluator`)** : Détection automatique de l'intégralité des combinaisons de poker requises :
    * Carte Haute (*High Card*)
    * Paire (*Pair*) & Double Paire (*Double Pair*)
    * Brelan (*Three of a Kind*)
    * Suite (*Straight*) - **Y compris la quinte spécifique de l'As au 5 (A-2-3-4-5)**
    * Couleur (*Flush*)
    * Full (*Full House*)
    * Carré (*Square / Four of a Kind*)
    * Quinte Flush (*Straight Flush*)
* **Calculateur de Score Évolutif (`StandardScoreCalculator`)** : Calcul mathématique exact basé sur la formule :  
  $$\text{Score} = (\text{Jetons de base du combo} + \text{Valeur cumulée des cartes}) \times \text{Multiplicateur}$$
* **Système de Planètes** : Prise en compte dynamique des améliorations permanentes par type de main (bonus de jetons et de multiplicateurs via l'évolution des niveaux dans la `GameState`).

### 3. Gestion du Cycle de Jeu & Deck (`GameModel`)
* **Gestion des Piles** : Pioche (`drawPile`) et défausse (`discardPile`) gérées rigoureusement.
* **Recyclage Automatique** : Remélange automatique et transparent de la défausse via `Collections.shuffle()` dès que la pioche est insuffisante au cours d'un tirage.
* **Suivi des Ressources** : Décompte réactif des mains restantes, des défausses disponibles et mise à jour de l'état de la partie à chaque action.

### 4. Architecture Globale (Strict Respect du MVC)
* **Découplage Total** : Isolation stricte du modèle vis-à-vis de l'affichage informatique.
* **Abstraction des Vues** : Utilisation de l'interface `IView` permettant au contrôleur d'orchestrer la partie de façon agnostique vis-à-vis du support visuel utilisé.

---

## Importation du Projet

Après avoir réaliser un ```git clone https://github.com/JagoOgaj/esiee-info-e3-balatri.git```

### Option A : Importation sous Eclipse (Recommandé)
1.  Ouvrez **Eclipse IDE**.
2.  Allez dans `File` > `Import...`.
3.  Sélectionnez `General` > `Existing Projects into Workspace` puis cliquez sur `Next`.
4.  Choisissez `Select root directory`, cliquez sur `Browse...` et sélectionnez le dossier racine du projet contenant ce fichier `README.md`.
5.  Vérifiez que le projet est bien coché dans la liste, puis cliquez sur `Finish`.

### Option B : Importation sous IntelliJ IDEA
1.  Lancez **IntelliJ IDEA**.
2.  Cliquez sur `Open` ou `Import`.
3.  Naviguez jusqu'au dossier racine du projet et sélectionnez le dossier (ou le fichier de configuration de build si présent).
4.  Laissez IntelliJ détecter automatiquement la structure du projet Java et configurer le SDK sur **Java 25**.

---

## Lancement du Programme

Le programme intègre un double point d'entrée commutable par argument en ligne de commande pour s'adapter à l'environnement d'évaluation.

### 1. Mode Graphique (Par défaut, sans argument)
Destiné à l'expérience de jeu finale complète, ce mode lance l'application interactive pilotée par le moteur graphique Zen6.
* **Commande CLI :**
    ```bash
    java -cp bin:lib/* esiee.info.e3.Main
    ```
  *(Ajustez le classpath `-cp` selon l'arborescence de compilation de votre IDE).*

### 2. Mode Console (Avec l'argument `-console`)
Idéal pour le débuggage léger ou pour une exécution textuelle pure au sein d'un terminal standard (gestion des saisies via flux standard `Scanner` et affichages alphanumériques).
* **Commande CLI :**
    ```bash
    java -cp bin:lib/* esiee.info.e3.Main -console
    ```
