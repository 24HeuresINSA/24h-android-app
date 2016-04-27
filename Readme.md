# Application des 24 heures de l'INSA

## Une nouvelle edition
Voici la liste des elements a mettre à jour pour faire vivre l'appli une édition de plus. Rien de bien compliqué si tu connais Android.

### Design
Pour custom le design de l'appli pour une édition voici ce qu'il faut mettre à jour et uniquement ca (tout se trouve dans /src/main/res) :
* /values/colors.xml
* toutes les colors sous la section <!--BASE COLORS-->
* tous les dossiers commençant /drawable

Les différents dossiers sont pour les différentes résolutions de téléphones. Chaque drawable (images) doit se trouver dans chacun des dossiers avec le meme noms et les memes dimensions, seule la résolution change d'un dossier à l'autre. Le téléphone se démerde pour prendre celle qui correspond le mieux à sa résolution.

Les détails des résolutions se trouvent ici  http://developer.android.com/guide/practices/screens_support.html dans le paragraphe  "Using configuration qualifiers" il y a un tableau récapitulatif dont voici un extrait de ce qui est indispensable (a mettre à jour en fonction des résolutions les plus utilisés en ce moment) :
* hdpi : 240dpi
* mdpi : 160dpi (base line)
* xhdpi : 320dpi
* xxhdpi : 480dpi

Toutes les images ne sont pas à changer, il s'agit d'identifier ce qui est lié à l'édition ce qui ce qui peut rester générique.

### Labels
Les labels sont ici app/src/main/res/values/strings.xml.

### Params
Plusieurs parametres sont tres important (app/src/main/res/values/settings.xml)
* DISPLAY_ARTIST_HOUR (integer [0;24[)
  *  defini l'heure a partir de laquelle l'application va s'ouvrir sur la page artist plutot que la page animations
  *  defini l'heure a partir de laquelle les horaires des artistes seront affiches
* FRIDAY_DATE, SATURDAY_DATE, SUNDAY_DATE (string, DD,mm,YYYY)
  * les jours du festival, utilisés pour n'afficher que les horaires des artisites de la soirée en cours (ou passée)

Tous ces parametres peuvent etre override dans le fichier de debug (app/src/debug/res/values/settings.xml).

### Deploy

Classiquement, changer le versioCode et le versioName dans le fichier Gradle
* versionCode 14
* versionName "2.0"

Et **changer le flag "Version de l'appli mobile"** ici http://assomaker.24heures.org/configv/ . Ce flag est utilisé par l'application pour savoir si elle une nouvelle version de l'application est disponible :
* changement de version major : l'application ne telecharge plus les données, si elle en a en local elle les utilise et un message de demande de mise à jour s'affiche toutes les 10 secondes => a n'utiliser que lors d'un changement d'edition
* changement de version minor : un message s'affiche au demarrage de l'application pour informer d'une nouvelle version disponible.



Console developpeur
https://play.google.com/apps/publish

Ne pas oublier de modifier la description et les captures d'écran.



Gmail account :
dsi-android@24heures.org
Me demander le password (RemiP, cf Github collaborateur)

Signing application
http://developer.android.com/tools/publishing/app-signing.html

Pour ajouter une clef de signature
https://console.developers.google.com

Building release with Android Studio

Upload new APK



## La connection avec Assomaker

L'application recupere les données depuis Assomaker (https://github.com/24HeuresINSA/PHPM mais il n'y pas de docs).

Deux endpoints sont utilises :
* http://mobile.24heures.org/
* http://mobile.24heures.org/version

### /
Recupere toutes les données JSON :
* artists : [] les artistes exraits des fichers artistes
* ressources : [] les animations extraites des fiches anims
* categories : [] les categories mobiles pour les animations
* version : int numero de versions des données. L'application s'en sert pour savoir si elle doit telecharger un nouveau jeu de données

Les artists et ressources contiennent des liens pour telecharger les images.

### /version
Recupere la derniere version de l'application actuellement sur le PlayStore

