<img src="Photos/Main photo for ReadMe/DD7.png" align="center" alt="logo" width="768" height="512">

# 🐉 Dungeons & Dragons Inspired - Java Game Project

This repository contains a Java-based implementation of a Dungeons & Dragons-inspired game. It was developed as part of the **Advanced Project Oriented Programming** course at [SCE - Shamoon College of Engineering](https://www.sce.ac.il/).

## 📜 Project Overview

This project simulates a simplified turn-based role-playing game (RPG), where players and enemies take actions such as attacking, casting spells, and moving around a game map. The architecture follows object-oriented principles and leverages Java interfaces, inheritance, and polymorphism to define different character types and combat behaviors.

Key features include:
- 🧙‍♂️ Multiple character classes (e.g., Mage, Warrior, Archer, Goblin)
- ⚔️ Turn-based combat system
- 🌟 Magic and physical attacks with hit and evade mechanics
- 🎵 Sound effects integration
- 📦 Resource handling via a dedicated `resources` folder

---

## 📁 Project Structure

```
src/
└── game/
    ├── characters/      # All character types (players and enemies)
    ├── combat/          # Combat logic and attacker interfaces
    ├── core/            # Game entity base classes and inventory system
    ├── engine/          # Game engine and utilities
    ├── items/           # In-game items (potions, treasure, etc.)
    ├── map/             # Map and position logic
    └── resources/       # Sound, images, config files

```

---


## 📝 Setting Up the `resources` Folder in Your IDE

To ensure sound files and other resources load correctly, make sure your IDE recognizes the `resources` folder as a **Resources Root**.

### 🧠 Why?
If not set correctly, resources like sound files may fail to load at runtime.

---

### 💡 IntelliJ IDEA

1. In the **Project** view, right-click on the `resources` folder.
2. Select **Mark Directory as** → **Resources Root**.

✅ This tells IntelliJ to include files from `resources` in your build path and allows `getClass().getResource(...)` to find them.

---

### 💡 Eclipse

1. Right-click your project and choose **Properties**.
2. Go to **Java Build Path** → **Source** tab.
3. Click **Add Folder**, then check the `resources` folder.
4. Click **OK** and **Apply and Close**.

✅ This adds the `resources` folder to your classpath.

---

## 🧩 Project Architecture Diagram
To better understand the class structure, relationships, and project flow, here's a visual representation of the game's architecture.<br>
It includes core components of the game file.

<img src="Photos/Diagrams/Advanced Object Oriented Programming.jpg" alt="Architecture Diagram" width="768">

---

## 🎮 Gameplay Showcase

A visual tour of the game features and interface:

### 🧑‍💼 Starting the Game

**Player & Grid Setup**

<img src="Photos/Main photo for ReadMe/Starting_Screen.png" alt="Starting Screen" width="720">

> Select the number of players (1–4) and the grid size to begin your adventure.

**Character Creation**

<img src="Photos/Main photo for ReadMe/Character_Creation.png" alt="Character Creation" width="720">

> Enter a character name and choose a class: Warrior, Mage, or Archer.

---

### 🎒 Exploring the World

**Single-Player Movement Demo**  
<img src="Photos\Main photo for ReadMe\1Player_Movement.gif" alt="Single Player Movement" width="768">

> Explore the map as a solo adventurer.

**Two-Player Movement Demo**  
<img src="Photos\Main photo for ReadMe\2Player_Movement.gif" alt="Two Player Movement" width="768">

> Two players navigating the grid simultaneously. The game supports 1–4 players in total.

**Picking Up Items**  
<img src="Photos\Main photo for ReadMe\Picking_Up_Items.gif" alt="Picking Up Items" width="720">

> Collect potions, treasure, and useful items scattered across the map.

**Showing Inventory**  
<img src="Photos\Main photo for ReadMe\Picking_Up_Items.gif" alt="Inventory Screen" width="720">

> View collected items and manage your inventory.

---

### ⚔️ Combat & Interaction

**Attacking Enemies**  
<img src="Photos\Main photo for ReadMe\Fight.gif" alt="Attacking Enemies" width="720">

> Engage in turn-based combat using physical or magical attacks.

**Player Status Window**  
<img src="Photos\Main photo for ReadMe\Status_Panel.png" alt="Player Status" width="720">

> View player stats, class type, and current treasure points.

---

### ⚙️ Game Options

**Settings Menu**  
<img src="Photos\Main photo for ReadMe\Settings.png" alt="Settings Menu" width="720">

> Adjust music and sound effect volume or exit the game via the pause menu.

---

### 🏁 Endgame

**Game Over Screen**  
<img src="Photos\Main photo for ReadMe\GameOver.png" alt="Game Over" width="720">

> Shown when all players are defeated. This will show the scores of each player in a descending order.

**Victory Screen**  
<img src="Photos\Main photo for ReadMe\Winning.png" alt="Victory Screen" width="720">

> Displayed when players defeat all enemies. This will show the scores of each player in a descending order
