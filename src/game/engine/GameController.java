package game.engine;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.CombatSystem;
import game.combat.RangedFighter;
import game.gui.CongratulationsGUI;
import game.gui.GameMapGUI;
import game.gui.GameOverGUI;
import game.items.GameItem;
import game.items.Interactable;
import game.items.Treasure;
import game.map.GameMap;
import game.map.Position;

import java.util.Scanner;

/**
 * Controls the game logic by mediating between the game world and the GUI.
 * Handles player actions such as movement, attacking, and item interaction.
 * Implements the ScreenListener interface to respond to GUI-triggered events.
 */
public class GameController implements ScreenListener {

    // Data Members
    private GameWorld gameWorld;
    private final GameMap map;
    private final CombatSystem combatSystem = CombatSystem.getInstance();
    private final GameMapGUI gameMapGUI;
    private boolean endTurn = false;
    private static final Scanner scanner = new Scanner(System.in);

    // Methods
    /**
     * Constructs the GameController, initializes the game world and GUI components.
     * If the GameWorld is already initialized, it retrieves the existing instance.
     * Sets up the game map and connects it with the GameMapGUI.
     */
    public GameController() {
        if(GameWorld.isInitialized())
            gameWorld = GameWorld.getInstance();
        map = GameMap.getInstance();
        this.gameMapGUI = new GameMapGUI(this, map, this);
    }

    /**
     * Returns whether the player's turn has ended.
     *
     * @return true if the player's turn has ended, false otherwise
     */
    public boolean getEndTurn() {return endTurn;}

    /**
     * Sets the end-of-turn flag.
     *
     * @param endTurn true if the turn should end, false otherwise
     * @return always true after setting
     */
    public boolean setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
        return true;
    }

    /**
     * Returns the singleton instance of the combat system.
     *
     * @return the combat system instance
     */
    public CombatSystem getCombatSystem() {return combatSystem;}

    /**
     * Returns the GUI responsible for rendering the game map.
     *
     * @return the GameMapGUI instance
     */
    public GameMapGUI getGameMapGUI() {return gameMapGUI;}

    /**
     * Gets the current player whose turn it is.
     *
     * @return the current PlayerCharacter
     */
    public PlayerCharacter getCurrentPlayer() {
        return gameWorld.getCurrentPlayer();
    }

    /**
     * Checks if the specified position is within 2 tiles of the current player.
     *
     * @param clickedPos the position to check
     * @return true if the position is within 2 tiles, false otherwise
     */
    public boolean checkDistanceFromPlayer(Position clickedPos) {
        return gameWorld.getCurrentPlayer().getPosition().distanceTo(clickedPos) <= 2;
    }

    // For Mouse and keyboard support
    /**
     * Attempts to move the current player to the given position if it's adjacent and valid.
     * Used for mouse-based movement.
     *
     * @param newPosition the target position to move to
     * @return the new position if the move was successful, otherwise null
     */
    public Position attemptMoveTo(Position newPosition) {
        System.out.println("Entered function");
        if(gameWorld.getCurrentPlayer().getPosition().distanceTo(newPosition) <= 1 && map.isValidPosition(newPosition)) {
            if (map.isGameItemBlocking(newPosition) || map.isEnemyBlocking(newPosition))
                return null;
            if((gameWorld.getCurrentPlayer().getPosition().getRow() + 1) == newPosition.getRow())
                return gameWorld.getCurrentPlayer().moveDown();
            else if((gameWorld.getCurrentPlayer().getPosition().getRow() - 1) == newPosition.getRow())
                return gameWorld.getCurrentPlayer().moveUp();
            else if((gameWorld.getCurrentPlayer().getPosition().getCol() + 1) == newPosition.getCol())
                return gameWorld.getCurrentPlayer().moveRight();
            else if((gameWorld.getCurrentPlayer().getPosition().getCol() - 1) == newPosition.getCol())
                return gameWorld.getCurrentPlayer().moveLeft();
        }
        System.out.println("DIDNT MOVE");
        return null;
    }

    /**
     * Handles actions triggered by the GUI or game loop.
     * Supports movement, attacking, picking up items, ending turns, and exiting the game.
     *
     * @param action the action being performed
     * @param data optional parameters related to the action
     * @return true if the action was handled successfully, false otherwise
     */
    @Override
    public boolean onAction(ScreenAction action, Object... data) {
        switch (action) {
            case ScreenAction.MOVE -> {
                System.out.println("PLAYER IS TRYING TO MOVE");
                if(data[0] instanceof Position position && map.isValidPosition(position)) {
                    Position newPosition = attemptMoveTo(position);
                    updateAfterMovingPlayer(newPosition, map);
                    this.onAction(ScreenAction.END_TURN, (Object) null);
                    return true;
                }
            }
            case ScreenAction.ATTACK -> {
                // Getting player position and attack range
                Enemy target;
                if(data[0] instanceof Enemy enemy)
                    target = enemy;
                else {
                    System.out.println("Tried to fight not an enemy");
                    return false;
                }

                PlayerCharacter player = gameWorld.getCurrentPlayer();
                int range = 1; // default
                if (player instanceof RangedFighter)
                    range = ((RangedFighter) player).getRange();

                if(player.getPosition().distanceTo(enemy.getPosition()) <= range) {
                    // Combat resolution
                    combatSystem.resolveCombat(player, target);
                    // Handle death for map entity
                    if (target.isDead()) {
                        System.out.println("You defeated the " + target.getClass().getSimpleName() + "!");
                        gameWorld.removeEnemy(target);
                    }
                    if(player.isDead()) {
                        gameWorld.removePlayerFromMap(player);
                    }
                    map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                    this.onAction(ScreenAction.END_TURN, (Object) null);
                    return true; // End turn after successful attack
                }
            }
            case ScreenAction.PICKUP -> {
                Position pos;
                PlayerCharacter player = gameWorld.getCurrentPlayer();
                if(data[0] instanceof Position p)
                    pos = p;
                else
                    return false;
                // ✅ Check for Manhattan distance == 1 (and valid bounds)
                if (!map.isValidPosition(pos)) return false;
                if (player.getPosition().distanceTo(pos) != 1) return false;

                GameItem chosen = map.getEntityGameItemAt(pos);
                if(chosen instanceof Interactable interactable) {
                    gameWorld.removeItem(chosen); // removing from map directly trough game world before changing Position
                    if(interactable instanceof Treasure)
                        interactable.interact(player);
                    else {
                        // changing Position of Interactable to make sure it's usable afterward
                        chosen.setPosition(new Position(-1, -1));
                        player.addToInventory(interactable);
                    }
                    map.updatePlayerView(player.getPosition());
                    this.onAction(ScreenAction.END_TURN, (Object) null);
                    return true;
                }
                return false;
            }
            case ScreenAction.END_TURN -> {
                endTurn = true;
                map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
            }
            case ScreenAction.EXIT_GAME -> {
                boolean gameIsOver = true;
                boolean gameIsWon = false;
                for(PlayerCharacter player : gameWorld.getPlayers()) {
                    if(!player.isDead()) {
                        gameIsOver = false;
                        break;
                    }
                }

                if(gameWorld.getEnemies().isEmpty())
                    gameIsWon = true;

                if(gameIsOver) {
                    GameOverGUI gameOver = new GameOverGUI(gameMapGUI, gameWorld.getPlayers());
                    gameOver.showDialog();
                    gameOver.setVisible(true);  // blocks until closed
                    scanner.close();
                    for(PlayerCharacter p : gameWorld.getPlayers())
                        System.out.println(p.getName() + " - Treasure Points: " + p.getTreasurePoints());
                    System.exit(0);
                }
                else if(gameIsWon) {
                    CongratulationsGUI congratulationsGUI = new CongratulationsGUI(gameMapGUI, gameWorld.getPlayers());
                    congratulationsGUI.showDialog();
                    congratulationsGUI.setVisible(true);
                    scanner.close();
                    for(PlayerCharacter p : gameWorld.getPlayers())
                        System.out.println(p.getName() + " - Treasure Points: " + p.getTreasurePoints());
                    System.exit(0);
                }
            }
            default -> System.out.println("Not an option");
        }
        return false;
    }

    /**
     * Updates the game state after the player has moved to a new position.
     * Removes the player from their old position, updates their position,
     * re-adds them to the map, updates the visible game map, and ends the turn.
     *
     * @param newPosition the new position to move the player to
     * @param map the game map instance to update
     */
    private void updateAfterMovingPlayer(Position newPosition, GameMap map) {
        if(newPosition != null) {
            // ✅ Move the player
            System.out.println("PLAYER MOVED SUCCESSFULLY");
            map.removeEntity(gameWorld.getCurrentPlayer()); // Remove from old position
            gameWorld.getCurrentPlayer().setPosition(newPosition);
            map.addEntity(gameWorld.getCurrentPlayer());    // Add to new position
            map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition()); // Updates entities view
            this.onAction(ScreenAction.END_TURN, (Object) null);
        }
    }
}
