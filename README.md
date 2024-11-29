# KittyLost : Help the kitty to find his way ! 

![Sad Cat Platformer](assets/kittylos_.png)


A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Description

Kittylost is a 2D plateform game, the player (cat) has to find the way to go back home. The player needs to avoid dogs, water and pics and has to collect dry food and go through 10 levels to acheive the game. 

## Fonctionnalities

- Lives count : The player start the game with 5 lifes for all the levels.
- Maps/level : The game is composed by 10 levels. One map for one level. It gets more complicate/complex each time. 
- Ennemies : There are two types of ennemy little dog "lildawg" that can be killed by one paw punch and big dog "Bulldawg" that can be killed by two paw punches.
Score : Collect dry food to enhance your score. 
- NPC : At the beginning of the game talk to Nekosensei to find your way!
Obstacle : Avoid ennemies, water and pics.


## Playing mechanics

### Movements :

- Jump : space bar
- Double jump : double space bar
- Attack : K button (pawpunch)

### Characters: 

- Hero : The player is a kitty (Poticha).
- Neko sensei : Big old cat at the beginning that told the rules.
- Lildawg : Little fragile but fast ennemy.
- Bulldawg : More robust but slower.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
