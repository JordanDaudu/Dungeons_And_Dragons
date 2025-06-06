package game.engine;

import game.characters.Enemy;
import game.characters.EnemyFactory;
import game.characters.EnemyTask;
import game.characters.PlayerCharacter;
import game.combat.CombatSystem;
import game.combat.RangedFighter;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.global_events.GlobalEventManager;
import game.global_events.MagicWaveEvent;
import game.global_events.SandstormEvent;
import game.gui.CongratulationsGUI;
import game.gui.GameMapGUI;
import game.gui.GameOverGUI;
import game.items.GameItem;
import game.items.Interactable;
import game.items.Treasure;
import game.logging.GameLogger;
import game.map.GameMap;
import game.map.Position;

import javax.swing.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private ScheduledExecutorService enemyScheduler;
    private final AtomicBoolean enemyRunningFlag = new AtomicBoolean(true);
    private static GlobalEventManager manager;
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
     * Starts the main turn-based game loop using a timer.
     * Each iteration checks if a player's turn has ended and advances to the next alive player.
     */
    public void startGameLoop() {
        // checks if the turn has ended, if it did switches to the next current player
        Timer turnTimer = new Timer(200, e -> {
            if (endTurn) {
                PlayerCharacter currentPlayer = gameWorld.getCurrentPlayer();
                setEndTurn(false); // Reset for next turn

                // Advance to next player
                int currentIndex = gameWorld.getPlayers().indexOf(currentPlayer);
                int size = gameWorld.getPlayers().size();
                boolean foundAlive = false;

                for (int i = 0; i < size; i++) {
                    int nextIndex = (currentIndex + 1 + i) % size;
                    PlayerCharacter nextPlayer = gameWorld.getPlayers().get(nextIndex);
                    if (!nextPlayer.isDead()) {
                        gameWorld.setCurrentPlayer(nextPlayer);
                        GameMap.getInstance().updatePlayerView(nextPlayer.getPosition());
                        callPanelRefreshers();
                        foundAlive = true;
                        break;
                    }
                }
                if (!foundAlive || gameWorld.getEnemies().isEmpty()) {
                    this.onAction(ScreenAction.EXIT_GAME, (Object) null);
                }
            }
        });
        turnTimer.start();
    }

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
     * Returns the AtomicBoolean flag indicating whether enemy actions are running.
     *
     * @return the AtomicBoolean enemy running flag
     */
    public AtomicBoolean getEnemyRunningFlag() {return enemyRunningFlag;}

    /**
     * Starts the global event manager for the current map and controller.
     */
    public void startManagerEvent() {
        manager = new GlobalEventManager(map, this);
        manager.start();
    }

    /**
     * Stops the global event manager if it is running.
     */
    public void stopManagerEvent() {
        if (manager != null) {
            manager.stop();  // Stop the manager
        }
    }

    /**
     * Sets the ScheduledExecutorService responsible for scheduling enemy actions.
     *
     * @param scheduler the ScheduledExecutorService to be set
     * @return true after setting the scheduler
     */
    public boolean setEnemyScheduler(ScheduledExecutorService scheduler) {
        this.enemyScheduler = scheduler;
        return true;
    }

    /**
     * Shuts down the enemy scheduler and sets the enemy running flag to false.
     */
    public void shutdownEnemyScheduler() {
        if (enemyScheduler != null && !enemyScheduler.isShutdown()) {
            enemyRunningFlag.set(false);
            enemyScheduler.shutdown();
        }
    }

    /**
     * Refreshes the status and inventory panels on the GUI with the current player's data.
     */
    private void callPanelRefreshers() {
        if(gameMapGUI.getSidePanelsVisible()) {
            gameMapGUI.getStatusPanel().updatePlayer(getCurrentPlayer());
            gameMapGUI.getInventoryPanel().updatePlayer(getCurrentPlayer());
        }
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
        if(gameWorld.getCurrentPlayer().getPosition().distanceTo(newPosition) <= 1 && map.isValidPosition(newPosition)) {
            if (map.isGameItemBlocking(newPosition) || map.isEnemyBlocking(newPosition) || map.isPlayerBlocking(newPosition))
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
        return null;
    }

    /**
     * Logs the current player's movement to the game logger.
     */
    public void logMovements() {
        GameLogger.getInstance().log("Player: " + getCurrentPlayer().getName() + " successfully moved to position:" + getCurrentPlayer().getPosition());
    }

    /**
     * Shuts down all background threads including enemy scheduler and event manager,
     * and stops the game logger.
     */
    private void shutDownThreads() {
        shutdownEnemyScheduler();
        stopManagerEvent();
        GameLogger.getInstance().stop();
    }

    private void checkIfNewEnemySpawning() {
        System.out.println("Active threads: " + EnemyTask.getScheduledEnemyCount());
        System.out.println("calculateStartingEnemyThreadPoolSize: " + EnemyTask.calculateStartingEnemyThreadPoolSize(map.getRows() * map.getCols()));
        while(EnemyTask.getScheduledEnemyCount() < EnemyTask.calculateStartingEnemyThreadPoolSize(map.getRows() * map.getCols())) {
            Enemy enemy = EnemyFactory.createEnemy(map.getLeastCommonEnemyType());
            enemy.setScreenListener(this);
            map.placeEnemyRandomly(enemy);
            gameWorld.addEnemy(enemy);
            enemyScheduler.schedule(new EnemyTask(enemy, enemyScheduler, getEnemyRunningFlag()), 1, TimeUnit.SECONDS);
        }
    }

    /**
     * Handles actions triggered by the GUI or game loop.
     * Supports movement, attacking, picking up items, ending turns, and exiting the game.
     * Logs relevant events such as item pickups and enemy movements.
     *
     * @param action the action being performed
     * @param data optional parameters related to the action
     * @return true if the action was handled successfully, false otherwise
     */
    @Override
    public boolean onAction(ScreenAction action, Object... data) {
        switch (action) {
            case ScreenAction.MOVE -> {
                if(data[0] instanceof Position position && map.isValidPosition(position)) {
                    Position newPosition = attemptMoveTo(position);
                    if(newPosition != null) {
                        updateAfterMovingPlayer(newPosition, map);
                        logMovements();
                        this.onAction(ScreenAction.END_TURN, (Object) null);
                        return true;
                    }
                }
            }
            case ScreenAction.PLAYER_ATTACK -> {
                // Getting player position and attack range
                Enemy target;
                if(data[0] instanceof Enemy enemy)
                    target = enemy;
                else {
                    return false;
                }

                PlayerCharacter player = gameWorld.getCurrentPlayer();
                int range = 1; // default
                if (player instanceof RangedFighter)
                    range = player.getRangeModifier();

                if(player.getPosition().distanceTo(enemy.getPosition()) <= range) {
                    // Combat resolution
                    combatSystem.resolveCombat(player, target);
                    // Handle death for map entity
                    if (target.isDead()) {
                        gameWorld.removeEnemy(target);
                        checkIfNewEnemySpawning();
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
                    SoundManager.playEffect("pickupInteractable");
                    if(interactable instanceof Treasure)
                        interactable.interact(player);
                    else {
                        // changing Position of Interactable to make sure it's usable afterward
                        chosen.setPosition(new Position(-1, -1));
                        player.addToInventory(interactable);
                        // ✅ Log pickup of non-treasure item
                        GameLogger.getInstance().log(player.getName() + " picked up a " + chosen.getClass().getSimpleName() + ".");
                    }
                    map.updatePlayerView(player.getPosition());
                    this.onAction(ScreenAction.END_TURN, (Object) null);
                    return true;
                }
                return false;
            }
            case ScreenAction.ENEMY_ACTION -> {
                if(data[0] instanceof Enemy enemy) {
                    Position currentEnemyPosition = enemy.getPosition();
                    if(currentEnemyPosition.distanceTo(gameWorld.getCurrentPlayer().getPosition()) <= 2) {
                        // Move toward player
                        List<Position> possibleMoves = gameWorld.getAdjacentFreePositions(currentEnemyPosition);
                        Position bestMove = null;
                        int minDistance = Integer.MAX_VALUE;

                        for (Position pos : possibleMoves) {
                            int distance = pos.distanceTo(gameWorld.getCurrentPlayer().getPosition());
                            if (distance < minDistance) {
                                minDistance = distance;
                                bestMove = pos;
                            }
                        }

                        if (bestMove != null) {
                            if(gameWorld.getMap().tryMoveEnemy(enemy, bestMove)) {
                                GameLogger.getInstance().log("Enemy: " + enemy.getClass().getSimpleName() +
                                        " moved towards player " + getCurrentPlayer().getName() + " to position " + bestMove + ".");
                                map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                                SwingUtilities.invokeLater(() -> getGameMapGUI().repaint());
                                return true;
                            }
                        }
                    }
                    else if(RandomUtil.getRandomDouble() < 0.20) {
                        // 20% chance to move randomly
                        Position newEnemyPosition = RandomUtil.getRandomAdjacentPosition(enemy.getPosition());
                        if(map.tryMoveEnemy(enemy, newEnemyPosition)) {
                            map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                            SwingUtilities.invokeLater(() -> getGameMapGUI().repaint());
                            return true;
                        }
                    }
                    else {
                        // Stayed in place
                        return true;
                    }
                }
            }
            case ScreenAction.GLOBAL_EVENT -> {
                if(data[0] instanceof MagicWaveEvent) {
                    gameMapGUI.magicWaveAnimation();
                }
                else if(data[0] instanceof SandstormEvent) {
                    gameMapGUI.sandstormAnimation();
                    map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                }
                for(PlayerCharacter player : gameWorld.getPlayers()) {
                    if(player.isDead()) {
                        player.defeat();
                    }
                }
                for(Enemy enemy : gameWorld.getEnemies()) {
                    if(enemy.isDead()) {
                        enemy.defeat();
                        checkIfNewEnemySpawning();
                    }
                }
                if(gameWorld.getCurrentPlayer().isDead()) {
                    onAction(ScreenAction.END_TURN, (Object) null);
                }
                callPanelRefreshers();
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
                    GameLogger.getInstance().log("Game Over!");
                    for(PlayerCharacter p : gameWorld.getPlayers())
                        GameLogger.getInstance().log(p.getName() + " - Treasure Points: " + p.getTreasurePoints());
                    shutDownThreads();
                    GameOverGUI gameOver = new GameOverGUI(gameMapGUI, gameWorld.getPlayers());
                    gameOver.showDialog();
                    gameOver.setVisible(true);  // blocks until closed
                    scanner.close();
                    System.exit(0);
                }
                else if(gameIsWon) {
                    GameLogger.getInstance().log("Game Won!");
                    for(PlayerCharacter p : gameWorld.getPlayers())
                        GameLogger.getInstance().log(p.getName() + " - Treasure Points: " + p.getTreasurePoints());
                    shutDownThreads();
                    CongratulationsGUI congratulationsGUI = new CongratulationsGUI(gameMapGUI, gameWorld.getPlayers());
                    congratulationsGUI.showDialog();
                    congratulationsGUI.setVisible(true);
                    scanner.close();
                    System.exit(0);
                }
            }
            default -> {}
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
            map.removeEntity(gameWorld.getCurrentPlayer()); // Remove from old position
            gameWorld.getCurrentPlayer().setPosition(newPosition);
            map.addEntity(gameWorld.getCurrentPlayer());    // Add to new position
            map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition()); // Updates entities view
            this.onAction(ScreenAction.END_TURN, (Object) null);
        }
    }
}
