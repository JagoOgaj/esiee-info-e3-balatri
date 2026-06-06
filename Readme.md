# Balatri - Jeu de Cartes Rogue-lite

Balatri est une adaptation Java inspirée du célèbre jeu rogue-lite *Balatro*. Ce projet a été développé dans le cadre du cours de Programmation Orientée Objet (Java) à l'ESIEE Paris (E3 - 2026). Il implémente une architecture logicielle robuste basée sur le modèle **MVC (Modèle-Vue-Contrôleur)** et s'appuie sur la bibliothèque graphique de bas niveau **Zen6** (Zen Application) pour son affichage fluide en mode immédiat.


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

### 1. Graphique
Destiné à l'expérience de jeu finale complète, ce mode lance l'application interactive pilotée par le moteur graphique Zen6.
* **Commande CLI :**
    ```bash
    java -cp bin:lib/* esiee.info.e3.Main
    ```
  *(Ajustez le classpath `-cp` selon l'arborescence de compilation de votre IDE).*

---

## Génération de l'Archive Exécutable (.JAR)

Si vous souhaitez distribuer l'application ou l'exécuter de manière autonome sans ouvrir votre IDE, vous pouvez compiler le projet sous la forme d'un fichier `.jar` exécutable englobant ses dépendances (comme la bibliothèque graphique Zen6).

### Méthode A : Exportation sous Eclipse
1. Dans le **Package Explorer** d'Eclipse, faites un clic droit sur la racine du projet.
2. Sélectionnez **Export...** dans le menu contextuel.
3. Déroulez le dossier **Java**, sélectionnez **Runnable JAR file** puis cliquez sur **Next**.
4. Configurez les paramètres d'exportation :
    - **Launch configuration** : Sélectionnez la configuration d'exécution principale de votre projet (généralement nommée d'après votre classe `Main`). *Note : Si elle n'apparaît pas dans la liste, lancez l'application normalement une première fois depuis Eclipse.*
    - **Export destination** : Cliquez sur *Browse...* pour choisir l'emplacement et le nom du fichier de sortie (ex: `balatri.jar`).
    - **Library handling** : Sélectionnez **Package required libraries into generated JAR** (ou *Extract required libraries...*) pour s'assurer que Zen6 soit correctement embarqué dans l'archive.
5. Cliquez sur **Finish**. Validez les éventuels avertissements liés aux licences de bibliothèques.

### Méthode B : Exportation sous IntelliJ IDEA
1. Allez dans le menu supérieur **File** > **Project Structure...** (ou utilisez le raccourci `Ctrl+Alt+Shift+S` / `Cmd+;` sur Mac).
2. Dans le panneau de gauche, sous la section *Project Settings*, sélectionnez **Artifacts**.
3. Cliquez sur le bouton de l'icône **+** (Add), puis choisissez **JAR** > **From modules with dependencies...**.
4. Dans la boîte de dialogue qui s'ouvre :
    - **Main Class** : Cliquez sur l'icône de dossier à droite et recherchez/sélectionnez la classe principale : `esiee.info.e3.Main`.
    - **JAR files from libraries** : Cochez l'option **extract to the target JAR** afin d'unifier le code de l'application et ses dépendances graphiques dans un unique livrable.
    - Cliquez sur **OK**, puis sur **Apply** et enfin sur **OK** pour fermer la structure du projet.
5. Pour générer concrètement le fichier, allez dans le menu supérieur **Build** > **Build Artifacts...**.
6. Dans le mini-menu flottant qui apparaît, sélectionnez l'artefact créé (ex: `esiee-info-e3-balatri:jar`) et cliquez sur **Build**.
7. Une fois la compilation terminée, votre fichier `.jar` se trouvera dans le répertoire du projet sous : `out/artifacts/esiee_info_e3_balatri_jar/`.

---

## Exécution de l'Archive JAR

Une fois votre fichier `balatri.jar` récupéré, vous pouvez lancer le jeu d'un simple double-clic (si votre système lie les fichiers JAR à l'environnement Java) ou via votre terminal avec la commande suivante :

```bash
java -jar balatri.jar