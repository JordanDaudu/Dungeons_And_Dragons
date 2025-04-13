package game.engine;

import game.characters.*;
import game.combat.CombatSystem;
import game.combat.RangedFighter;
import game.core.GameEntity;
import game.items.GameItem;
import game.items.Interactable;
import game.map.GameMap;
import game.map.Position;
import java.util.ArrayList;
import java.util.List;

public class GameWorld {

    public static void main(String[] args) {
        SoundManager.loadMusic();
        gameLoop();
    }

    private List<PlayerCharacter> players;
    private List<Enemy> enemies;
    private List<GameItem> items;
    private GameMap map;

    private CombatSystem combatSystem = new CombatSystem();
    private final java.util.Scanner scanner = new java.util.Scanner(System.in);

    public GameWorld(int row, int col) {
        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.map = GameMap.getInstance();
        this.map.init(row, col);
    }

    public List<PlayerCharacter> getPlayers() {
        return players;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<GameItem> getItems() {
        return items;
    }

    public GameMap getMap() {
        return map;
    }

    public static void gameLoop() {
        SoundManager.playMusic("preparations", true);
        GameWorld game = new GameWorld(10, 10);
        game.getPlayers().add(game.createCharacter());
        game.getMap().placePlayerRandomly(game.getPlayers().getFirst());

        game.collectPlayersFromMap();
        game.collectEnemiesFromMap();
        game.collectItemsFromMap();

        SoundManager.crossfadeTo("battle1", true);
        do {
            game.map.printEntitiesPerTile();

            System.out.println("--------------------------------------------------------\n");
            for (PlayerCharacter player : game.getPlayers()) {
                game.map.printPlayerView(player.getPosition());
                game.playerMenu(player);
                // game.getMap().printEntitiesPerTile();
            }
        }
        while (!game.getEnemies().isEmpty() && !game.getPlayers().isEmpty());
    }

    public PlayerCharacter createCharacter() {
        System.out.println("What is your name?: ");
        String name = scanner.nextLine();
        int choice = getIntInput("Choose your class:\n1.Warrior\n2.Archer\n3.Mage", 1, 3);
        switch (choice) {
            case 1 -> {
                return new Warrior(name);
            }
            case 2 -> {
                return new Archer(name);
            }
            case 3 -> {
                return new Mage(name);
            }
            default -> {
                return null;
            }
        }
    }

    public void playerMenu(PlayerCharacter player) {
        boolean turnEnded = false;

        while (!turnEnded) {
            System.out.println("\n--- " + player.getName() + "'s Turn ---");
            map.printPlayerView(player.getPosition());
            System.out.println("Choose an action:");
            System.out.println("1. Move");
            System.out.println("2. Interact");
            System.out.println("3. Attack");
            System.out.println("4. Use Item");
            System.out.println("5. End Turn");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> turnEnded = PlayerMovements(player);
                case "2" -> turnEnded = playerInteract(player);
                case "3" -> turnEnded = playerAttack(player);
                case "4" -> turnEnded = playerUseItem(player);
                case "5" -> {
                    System.out.println("Ending turn.");
                    turnEnded = true;
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }

    private boolean PlayerMovements(PlayerCharacter player) {
        int choice = getIntInput("Where to move ? (0.Back 1.Right, 2.Left, 3.Up, 4.Down", 0, 4);

        Position newPosition = null;

        switch (choice) {
            case 1 -> newPosition = player.MoveRight(player); // Move Right
            case 2 -> newPosition = player.MoveLeft(player); // Move Left
            case 3 -> newPosition = player.MoveUp(player); // Move Up
            case 4 -> newPosition = player.MoveDown(player); // Move Down
            default -> {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                return false;
            }
        }

        // Check if new position is within the map
        if (!map.isValidPosition(newPosition)) {
            System.out.println("Move failed: position is outside the map.");
            return false;
        }

        // Check if a game item is blocking
        if (map.isGameItemBlocking(newPosition)) {
            System.out.println("Move failed: an item is blocking the path.");
            return false;
        }

        /*
        if (map.isOccupied(newPosition)) {
            System.out.println("Move failed: position is already occupied.");
            return false;
        }
        */
        if (map.isEnemyBlocking(newPosition)) {
            System.out.println("Move failed: an enemy is blocking the path.");
            choice = getIntInput("Do you want to engage in combat?\n1.Yes\n2.No", 1, 2);
            if(choice == 1)
                return playerAttack(player);
            return false;
        }

            // ✅ Move the player
        map.removeEntity(player);                // Remove from old position
        player.setPosition(newPosition);         // Update player's position
        map.addEntity(player);                   // Add to new position

        System.out.println("Player moved successfully to " + newPosition);
        return true;
    }

    private boolean playerInteract(PlayerCharacter player) {
        Position playerPos = player.getPosition();
        List<GameEntity> nearbyInteractables = new ArrayList<>();

        for (int row = playerPos.getRow() - 1; row <= playerPos.getRow() + 1; row++) {
            for (int col = playerPos.getCol() - 1; col <= playerPos.getCol() + 1; col++) {
                Position pos = new Position(row, col);

                // ✅ Check for Manhattan distance == 1 (and valid bounds)
                if (!map.isValidPosition(pos)) continue;
                if (playerPos.distanceTo(pos) != 1) continue;

                List<GameEntity> entities = map.getEntitiesAt(pos);
                for (GameEntity entity : entities) {
                    if (entity instanceof GameItem item && item instanceof Interactable) {
                        nearbyInteractables.add(item);
                    }
                }
            }
        }

        if (nearbyInteractables.isEmpty()) {
            System.out.println("There is nothing to interact with nearby.");
            return false;
        }

        while (true) {
            System.out.println("\n--- Interactables Nearby ---");
            for (int i = 0; i < nearbyInteractables.size(); i++) {
                GameItem item = (GameItem) nearbyInteractables.get(i);
                System.out.println((i + 1) + ". " + item.getDescription() + " [Position" + item.getPosition() + "]");
            }
            System.out.println("0. Back");

            System.out.print("Choose an item to interact with: ");
            String input = scanner.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            if (choice == 0) {
                System.out.println("Returning to player menu.");
                return false;
            } else if (choice > 0 && choice <= nearbyInteractables.size()) {
                GameItem chosen = (GameItem) nearbyInteractables.get(choice - 1);
                if(chosen instanceof Interactable) {
                    ((Interactable) chosen).interact(player);
                    removeItem(chosen);
                    return true; // After one interaction, exit
                }
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private boolean playerAttack(PlayerCharacter player) {
        Position playerPos = player.getPosition();
        int range = 1; // default
        if (player instanceof RangedFighter)
            range = ((RangedFighter) player).getRange();

        List<Enemy> targetsInRange = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && playerPos.distanceTo(enemy.getPosition()) <= range) {
                targetsInRange.add(enemy);
            }
        }

        if (targetsInRange.isEmpty()) {
            System.out.println("No enemies in range to attack.");
            return false;
        }

        while (true) {
            System.out.println("\n--- Targets in Range ---");
            for (int i = 0; i < targetsInRange.size(); i++) {
                Enemy enemy = targetsInRange.get(i);
                System.out.println((i + 1) + ". " + enemy.getClass().getSimpleName()
                        + " at " + enemy.getPosition()
                        + " (HP: " + enemy.getHealth() + ")");
            }
            System.out.println("0. Back");

            int choice = getIntInput("Choose a target to attack:", 0, targetsInRange.size());

            if (choice == 0) {
                System.out.println("Returning to player menu.");
                return false;
            }

            Enemy target = targetsInRange.get(choice - 1);

            // Combat resolution
            combatSystem.resolveCombat(player, target);
            // Handle death
            if (target.isDead()) {
                System.out.println("You defeated the " + target.getClass().getSimpleName() + "!");
                removeEnemy(target);
            }
            return true; // End turn after successful attack
        }
    }

    public void removePlayer(PlayerCharacter player) {
        players.remove(player);         // Remove from player list
        map.removeEntity(player);       // Remove from map
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);          // Remove from enemy list
        map.removeEntity(enemy);        // Remove from map
    }

    public void removeItem(GameItem item) {
        items.remove(item);             // Remove from item list
        map.removeEntity(item);         // Remove from map
    }

    private void collectPlayersFromMap() {
        for (GameEntity entity : map.getAllEntities()) {
            if (entity instanceof PlayerCharacter player) {
                players.add(player);
            }
        }
    }

    private void collectEnemiesFromMap() {
        for (GameEntity entity : map.getAllEntities()) {
            if (entity instanceof Enemy enemy) {
                enemies.add(enemy);
            }
        }
    }

    private void collectItemsFromMap() {
        for (GameEntity entity : map.getAllEntities()) {
            if (entity instanceof GameItem item) {
                items.add(item);
            }
        }
    }

    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine();

            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public boolean playerUseItem(PlayerCharacter player) {
        if(!player.haveInteractableInInventory()) {
            System.out.println(player.getName() + " you don't have a interactable item.");
            return false;
        }
        else {
            player.printInventoryOfPlayer();
        }
        int choice = getIntInput("\n--- Use Item ---\n0. Cancel\n1. Use Potion\n2. Use Power Potion", 0, 2);

        switch (choice) {
            case 1 -> {
                if (player.usePotion()) {
                    System.out.println("Potion used successfully.");
                    return true;
                } else {
                    System.out.println("No Potion available.");
                    return false;
                }
            }
            case 2 -> {
                if (player.usePowerPotion()) {
                    System.out.println("Power Potion used successfully.");
                    return true;
                } else {
                    System.out.println("No Power Potion available.");
                    return false;
                }
            }
            case 0 -> {
                System.out.println("Cancelled item usage.");
                return false;
            }
            default -> {
                System.out.println("Invalid choice.");
                return false;
            }
        }
    }
}

