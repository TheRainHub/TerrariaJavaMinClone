# Project Wiki

## Overview

Welcome to the developer’s guide for this 2D sandbox game inspired by Terraria. Written in Java with JavaFX, the project demonstrates a custom tile-based engine, physics, crafting system, NPC interactions, and multi-level world management. This page covers everything from gameplay instructions to code architecture and technology stack.

---

## Table of Contents

* [Quick Start](#quick-start)
* [Gameplay Guide](#gameplay-guide)
* [Key Features](#key-features)
* [Project Structure](#project-structure)
* [Package Breakdown](#package-breakdown)
* [Core Components](#core-components)
* [Technology Stack](#technology-stack)
* [Module System](#module-system)
* [Contributing](#contributing)
* [License](#license)

---

## Quick Start

These steps will get you up and running quickly:

1. **Install Java 11+**: Make sure your `JAVA_HOME` points to a JDK 11 or newer.
2. **Clone the repository**:

   ```bash
   git clone https://gitlab.com/yourusername/game-project.git
   cd game-project
   ```
3. **Build with Maven**:

   ```bash
   mvn clean package
   ```
4. **Run the game**:

   ```bash
   java -jar target/GameApp.jar
   ```
5. **Play!** A window sized 1720×820 px will appear.
<img width="1686" height="574" alt="image-2" src="https://github.com/user-attachments/assets/a4d98f4b-cfed-4e37-b100-46b772a57120" />

---

## Gameplay Guide

### Controls

* **A / ←**: Move left
* **D / →**: Move right
* **W / ↑ / Space**: Jump
* **E**: Interact with NPCs or advance dialogue
* **C**: Toggle crafting menu
* **Left Click**: Mine a block
* **Right Click**: Place a dirt block
* **ESC**: Open pause menu

### Mechanics

* **Mining & Placing**: Click blocks to mine. Right-click emptiness to place dirt.
* **Inventory**: Shown top-left, lists item IDs and counts.
* **Crafting**: Press `C`, navigate recipes with arrow keys, press Enter to craft if you have materials.
* **NPC Dialogue**: Approach an NPC, press `E` to open the dialogue box. Press `E` again to continue or close.
* **Level Transition**: Walking off screen left/right loads previous/next map and repositions you.
* **Save & Load**: In pause menu, choose **Save** or **Save & Quit**. Upon victory, the game auto-clears saves and exits after 5 seconds.

### Troubleshooting

* **Missing maps or resources**: Ensure `src/main/resources` contains `map1.txt`, `map2.txt`, `map3.txt`, `recipes.txt`, and image assets under `animation/`.
* **Apiguardian annotation errors**: Confirm `apiguardian-api.jar` is on the module path if building manually.

---

## Key Features

* **Custom Tile Engine**: ASCII-based maps parsed into 2D tile grids.
* **Parallax Scrolling**: Optional background image with parallax effect.
* **Player Physics**: Gravity, jumping, horizontal movement, collision detection.
* **Animation System**: Idle, run, jump states for player and NPCs, including item-equipped variants.
* **Crafting & Inventory**: Flexible recipe definitions, dynamic inventory management.
* **NPC Interaction**: Dialogue system tied to proximity-triggered events.
* **Multi-Level World**: Seamless transition between map files.
* **Persistence**: Save/load of game state (inventory, level index, player position).
* **JavaFX UI**: Canvas-based rendering for game world and UI overlays.
* **Modular Codebase**: Organized into clear packages, facilitating extension.

---

## Project Structure
<img width="6560" height="9898" alt="MIWGame-1" src="https://github.com/user-attachments/assets/6ff06b2d-312f-4ed6-a931-7308b203bffe" />

```
game-project/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── main/GameApp.java
│   │   │   ├── engine/
│   │   │   │   ├── core/GameLoop.java
│   │   │   │   ├── input/InputHandler.java
│   │   │   │   ├── level/LevelManager.java
│   │   │   │   ├── save/SaveLoadManager.java
│   │   │   │   └── ui/UIManager.java
│   │   │   ├── entity/Player.java
│   │   │   ├── entity/NPC.java
│   │   │   ├── entity/ItemEntity.java
│   │   │   ├── world/WorldLoader.java
│   │   │   └── util/
│   │   │       ├── Inventory.java
│   │   │       ├── Recipe.java
│   │   │       ├── RecipeLoader.java
│   │   │       └── CraftingManager.java
│   │   └── resources/
│   │       ├── map1.txt, map2.txt, map3.txt
│   │       ├── recipes.txt
│   │       └── animation/*.png, background.png
│   └── test/java/
│       └── tests/CraftingManagerTest.java
└── README.md
```

---

## Package Breakdown

* **main**: Entry point (`GameApp`) and JavaFX launch.
* **engine.core**: `GameLoop` handles the main update-render cycle.
* **engine.input**: `InputHandler` maps keyboard/mouse events to game actions.
* **engine.level**: `LevelManager` loads maps, spawns entities, manages transitions.
* **engine.save**: `SaveLoadManager` persists and restores game state.
* **engine.ui**: `UIManager` draws inventory, dialogues, menus, and victory screen.
* **entity**: `Player`, `NPC`, and `ItemEntity` classes represent dynamic world actors.
* **world**: `WorldLoader` and `Level` parse and hold tile maps and spawn data.
* **util**: Utility classes: `Inventory`, `Recipe`, `RecipeLoader`, `CraftingManager`.

---

## Core Components

1. **GameLoop**

   * Extends `AnimationTimer`, calculates delta-time, calls `update(dt)` and `render()` each tick.
2. **LevelManager**

   * Parses ASCII maps and spawn directives, initializes `World`, `WorldRenderer`, and entity lists.
3. **InputHandler**

   * Forwards events to UI when appropriate (pause or crafting open), otherwise drives player and world.
4. **UIManager**

   * Renders UI overlays: inventory, NPC dialogue, crafting menu, pause menu, and win screen.
5. **Player & NPC**

   * Player: physics, collision, animation state machine, rendering.
     NPC: idle animation, proximity-based dialogue progression.
6. **SaveLoadManager**

   * Saves inventory file and a simple properties file for level and player coords.

---

## Technology Stack

* **Java 17+**
* **JavaFX** (Canvas, Scene, AnimationTimer)
* **Maven** for dependency management and build lifecycle
* **JUnit 5 (Jupiter)** for unit testing
* **Apiguardian API** for annotation metadata

---

## Module System

The codebase uses Java modules:

* **`org.example.game`**: Main application module
* **`org.example.game.tests`**: Test module, requires `org.junit.jupiter.api` and `org.apiguardian.api`

Module descriptors are located in `src/main/java/module-info.java` and `src/test/java/module-info.java`.

---


## Technical Documentation

This section dives into the detailed internals of the application, including data formats, class interactions, and configuration.

### Architecture Overview

The engine follows an MVC-like pattern:

* **Model**: `World`, `Level`, `Inventory`, `Recipe`, and entity state (`Player`, `NPC`, `ItemEntity`).
* **View**: JavaFX `Canvas` rendering via `WorldRenderer`, `UIManager`, and entity `render()` methods.
* **Controller**: `GameLoop` orchestrates the update-render cycle; `InputHandler` maps user input to model changes.

Communication flows:

1. **Startup**: `GameApp` loads resources, initializes managers and services.
2. **Loop**: `GameLoop.handle()` → compute `dt` → `update(dt)` → `render()`.
3. **Update**: Player physics, NPC behavior, item updates, level transitions in `LevelManager`.
4. **Render**: Background, tiles, entities, UI overlays via `GraphicsContext`.

### Data Formats

* **Map files (`.txt`)**:

  * First N lines: fixed-width ASCII for `TileType` (characters mapped via `TileRegistry`).
  * Following lines: spawn directives:

    * `ITEM <itemId> <x> <y>`
    * `NPC <npcId> <x> <y>`
* **Recipes (`recipes.txt`)**:

  * Each line: `<output>=<ing1>:<qty1>,<ing2>:<qty2>`
  * Comments start with `#`.
* **Inventory (`inventory.txt`)**:

  * Each line: `<itemId>=<quantity>`
* **Savegame (`savegame.txt`)**:

  * Properties format:

    ```
    level=<currentLevelIndex>
    playerX=<xCoordinate>
    playerY=<yCoordinate>
    ```

### Class Diagram (Simplified)

```
GameApp
  └─ GameLoop ──> InputHandler
               ├─ LevelManager ──> World, WorldRenderer
               ├─ UIManager
               └─ SaveLoadManager
Player ──> Physics & Animation
NPC ──> Animation & Dialogue
Inventory, RecipeLoader, CraftingManager
```

## Contributing

1. Fork the repo and create a feature branch.
2. Write clear, commented code and Javadoc in English.
3. Add or update unit tests in `src/test`.
4. Submit a merge request with a descriptive title and summary.
5. Ensure the CI pipeline (if configured) passes all checks.

---

## License

This project is released under the **MIT License**. See the `LICENSE` file for details.
