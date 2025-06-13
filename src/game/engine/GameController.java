package game.engine;

import game.characters.Enemy;
import game.characters.EnemyTask;
import game.combat.CombatSystem;
import game.core.GameEntity;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.decorator.PlayerDecorator;
import game.decorator.RegenerationPlayerDecorator;
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
import java.util.Objects;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private GameMapGUI gameMapGUI;
    private final AtomicBoolean endTurn = new AtomicBoolean(false);
    private ScheduledExecutorService enemyScheduler;
    private static final AtomicBoolean enemyRunningFlag = new AtomicBoolean(true);
    private static GlobalEventManager manager;
    private static final AtomicBoolean globalEventRunning = new AtomicBoolean(true);
    private static final Scanner scanner = new Scanner(System.in);
    private long lastEnemyAttackTime = 0; // in milliseconds
    private int intervalBetweenEnemyAttacks = 3000; // in milliseconds
    private Timer turnTimer;

    // Methods
    /**
     * Constructs the GameController, initializes the game world and GUI components.
     * If the GameWorld is already initialized, it retrieves the existing instance.
     * Sets up the game map and connects it with the GameMapGUI.
     */
    public GameController() {
        if(GameWorld.isInitialized())
            gameWorld = GameWorld.getInstance();
        gameWorld.setControllerListener(this);
        map = GameMap.getInstance();
        this.gameMapGUI = new GameMapGUI(this, map, this);
    }

    /**
     * Starts the main turn-based game loop using a timer.
     * Each iteration checks if a player's turn has ended and advances to the next alive player.
     */
    public void startGameLoop() {
        // checks if the turn has ended, if it did switches to the next current player
        turnTimer = new Timer(200, e -> {
            if (endTurn.get()) {
                game.characters.PlayerCharacter currentPlayer = gameWorld.getCurrentPlayer();
                endTurn.set(false); // Reset for next turn

                // Advance to next player
                int currentIndex = gameWorld.getPlayers().indexOf(currentPlayer);
                int size = gameWorld.getPlayers().size();
                boolean foundAlive = false;

                for (int i = 0; i < size; i++) {
                    int nextIndex = (currentIndex + 1 + i) % size;
                    game.characters.PlayerCharacter nextPlayer = gameWorld.getPlayers().get(nextIndex);
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
     * Stops the main turn-based game loop timer if running.
     */
    public void stopGameLoop() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
    }

    /**
     * Returns the AtomicBoolean flag indicating whether enemy actions are running.
     *
     * @return the AtomicBoolean enemy running flag
     */
    public AtomicBoolean getEnemyRunningFlag() {return enemyRunningFlag;}

    /**
     * Pauses enemy tasks by setting the running flag to false.
     */
    public static void pauseEnemyTasks() {
        enemyRunningFlag.set(false);
    }

    /**
     * Resumes enemy tasks by setting the running flag to true.
     */
    public static void resumeEnemyTasks() {
        enemyRunningFlag.set(true);
    }

    /**
     * Starts the global event manager for the current map and controller.
     */
    public void startManagerEvent() {
        manager = new GlobalEventManager(map, this, globalEventRunning);
        manager.start();
    }

    /**
     * Pauses all global events by setting the global event running flag to false.
     */
    public static void pauseManagerEvent() {globalEventRunning.set(false);}

    /**
     * Resumes global events if previously paused.
     * Restarts the scheduler if it has been shutdown.
     */
    public static void resumeManagerEvent() {
        if (!globalEventRunning.get()) { // Only restart if previously paused
            globalEventRunning.set(true);

            if (manager.isSchedulerShutdown()) { // Check if scheduler is inactive
                manager.restartScheduler(); // Restart it properly
            }

            manager.start(); // Resume scheduling new events
        }
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
            EnemyTask.clearScheduledEnemies();
        }
    }

    /**
     * Restarts the enemy scheduler by shutting down the current one,
     * setting the running flag to true, and creating a new thread pool
     * with the size based on the map size.
     * Also attaches the game controller to all enemies for scheduling.
     */
    public void restartEnemyScheduler() {
        shutdownEnemyScheduler(); // Always stop the old one first
        enemyRunningFlag.set(true);
        enemyScheduler = Executors.newScheduledThreadPool(EnemyTask.calculateStartingEnemyThreadPoolSize(map.getRows() * map.getCols()));
        gameWorld.attachGameControllerToEnemies(this, enemyScheduler, enemyRunningFlag);
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
    public game.characters.PlayerCharacter getCurrentPlayer() {
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

    /**
     * Checks if enemies are allowed to attack based on the time since last attack.
     *
     * @return true if the cooldown has passed and enemies can attack, false otherwise
     */
    private boolean canEnemiesAttack() {
        if(System.currentTimeMillis() - lastEnemyAttackTime >= intervalBetweenEnemyAttacks) {
            lastEnemyAttackTime = System.currentTimeMillis();
            return true;
        }
        return false;
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

    /**
     * Checks if new enemies should spawn based on current active enemy count and max allowed.
     * Creates and schedules new enemies if slots are available in the scheduler.
     */
    private void checkIfNewEnemySpawning() {
        int max = EnemyTask.calculateStartingEnemyThreadPoolSize(map.getRows() * map.getCols());
        System.out.println("Active threads: " + EnemyTask.getScheduledEnemyCount());
        System.out.println("calculateStartingEnemyThreadPoolSize: " + EnemyTask.calculateStartingEnemyThreadPoolSize(map.getRows() * map.getCols()));

        while (EnemyTask.getScheduledEnemyCount() <= max) {
            Enemy enemy = map.createEnemy(map.getLeastCommonEnemyType());
            enemy.setScreenListener(this);

            // Only schedule and proceed if scheduling succeeded
            boolean scheduled = EnemyTask.tryScheduleAndRegisterEnemy(enemy, max, enemyScheduler, getEnemyRunningFlag());
            if (scheduled) {
                map.placeEnemyRandomly(enemy);
                gameWorld.addEnemy(enemy);
            }
            else {
                break;
            }
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

                game.characters.PlayerCharacter player = gameWorld.getCurrentPlayer();
                int range = player.getRangeModifier(); // default

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
                game.characters.PlayerCharacter player = gameWorld.getCurrentPlayer();
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
                    if(currentEnemyPosition.distanceTo(gameWorld.getCurrentPlayer().getPosition()) <= 1) {
                        if(canEnemiesAttack()) {
                            onAction(ScreenAction.ENEMY_ATTACK, enemy);
                            return true;
                        }
                    }
                    else if(currentEnemyPosition.distanceTo(gameWorld.getCurrentPlayer().getPosition()) <= 2) {
                        // Added a 50% to fail and instead  move toward player
                        if(enemy.getRangeModifier() >= enemy.getPosition().distanceTo(getCurrentPlayer().getPosition()) && RandomUtil.getRandomInt(2) == 0) {
                            if(canEnemiesAttack()) {
                                onAction(ScreenAction.ENEMY_ATTACK, enemy);
                                return true;
                            }
                        }
                        // Move toward player
                        else {
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
                                    GameLogger.getInstance().log("Enemy: " + enemy.getEnemyTypeName() +
                                            " moved towards player " + getCurrentPlayer().getName() + " to position " + bestMove + ".");
                                    map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                                    SwingUtilities.invokeLater(() -> gameMapGUI.repaint());
                                    return true;
                                }
                            }
                        }
                    }
                    else if(RandomUtil.getRandomDouble() < 0.20) {
                        // 20% chance to move randomly
                        Position newEnemyPosition = RandomUtil.getRandomAdjacentPosition(enemy.getPosition());
                        if(map.tryMoveEnemy(enemy, newEnemyPosition)) {
                            map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                            SwingUtilities.invokeLater(() -> gameMapGUI.repaint());
                            return true;
                        }
                    }
                    else {
                        // Stayed in place
                        return true;
                    }
                }
            }
            case ScreenAction.ENEMY_ATTACK -> {
                // Getting player position and attack range
                Enemy sourceEnemy;
                if (data[0] instanceof Enemy enemy)
                    sourceEnemy = enemy;
                else {
                    System.out.println("Tried to fight not an enemy");
                    return false;
                }

                game.characters.PlayerCharacter player = gameWorld.getCurrentPlayer();
                if (player.isDead())
                    return false;
                int range = sourceEnemy.getRangeModifier(); // default

                if (sourceEnemy.getPosition().distanceTo(player.getPosition()) <= range) {
                    // Combat resolution
                    combatSystem.resolveCombat(sourceEnemy, player);
                    // Handle death for map entity
                    if (sourceEnemy.isDead()) {
                        gameWorld.removeEnemy(sourceEnemy);
                        checkIfNewEnemySpawning();
                    }
                    if(player.isDead()) {
                        player.defeat();
                        onAction(ScreenAction.END_TURN, (Object) null);
                    }
                    map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                    callPanelRefreshers();
                }
            }
            case ScreenAction.LOAD_DATA -> {
                // Launch new game window
                stopGameLoop();
                endTurn.set(true);
                gameWorld.setCurrentPlayer(gameWorld.getPlayers().getFirst());

                for(List<GameEntity> cell : map.getGrid().values()) {
                    cell.removeIf(Objects::nonNull);
                }

                for(game.characters.PlayerCharacter player : gameWorld.getPlayers()) {
                    map.addEntity(player);
                }
                for(Enemy enemy : gameWorld.getEnemies()) {
                    map.addEntity(enemy);
                }
                for(GameItem gameItem : gameWorld.getItems()) {
                    map.addEntity(gameItem);
                }

                gameMapGUI.dispose();
                gameMapGUI = new GameMapGUI(this, map, this);
                // Loading settings of GameMapGUI
                GameSettings gameSettings = gameWorld.getGameSettings();
                SoundManager.setMusicVolume(gameSettings.getMusicVolume());
                SoundManager.setSFXVolume(gameSettings.getSFXVolume());
                gameMapGUI.setShowHPBar(gameSettings.getShowHPBar());
                gameMapGUI.toggleSidePanels(gameSettings.getShowPlayerInformation());
                gameMapGUI.setTileBackgroundTheme(gameSettings.getSelectedTheme());

                endTurn.set(false);
                startGameLoop();

                gameMapGUI.setVisible(true);
                gameMapGUI.revalidate();
                gameMapGUI.repaint();
                restartEnemyScheduler();
                map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
            }
            case ScreenAction.GLOBAL_EVENT -> {
                if(data[0] instanceof MagicWaveEvent) {
                    gameMapGUI.magicWaveAnimation();
                }
                else if(data[0] instanceof SandstormEvent) {
                    gameMapGUI.sandstormAnimation();
                    map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                }
                for(game.characters.PlayerCharacter player : gameWorld.getPlayers()) {
                    if(player.isDead()) {
                        player.defeat();
                    }
                }
                for(Enemy enemy : gameWorld.getEnemies()) {
                    if(enemy.isDead()) {
                        enemy.defeat();
                        gameWorld.removeEnemy(enemy);
                        checkIfNewEnemySpawning();
                    }
                }
                if(gameWorld.getCurrentPlayer().isDead()) {
                    onAction(ScreenAction.END_TURN, (Object) null);
                }
                callPanelRefreshers();
                SwingUtilities.invokeLater(() -> gameMapGUI.repaint());
            }
            case ScreenAction.END_TURN -> {
                endTurn.set(true);
                map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
            }
            case ScreenAction.EXIT_GAME -> {
                boolean gameIsOver = true;
                boolean gameIsWon = false;
                for(game.characters.PlayerCharacter player : gameWorld.getPlayers()) {
                    if(!player.isDead()) {
                        gameIsOver = false;
                        break;
                    }
                }

                if(gameWorld.getEnemies().isEmpty())
                    gameIsWon = true;

                if(gameIsOver) {
                    GameLogger.getInstance().log("Game Over!");
                    for(game.characters.PlayerCharacter p : gameWorld.getPlayers())
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
                    for(game.characters.PlayerCharacter p : gameWorld.getPlayers())
                        GameLogger.getInstance().log(p.getName() + " - Treasure Points: " + p.getTreasurePoints());
                    shutDownThreads();
                    CongratulationsGUI congratulationsGUI = new CongratulationsGUI(gameMapGUI, gameWorld.getPlayers());
                    congratulationsGUI.showDialog();
                    congratulationsGUI.setVisible(true);
                    scanner.close();
                    System.exit(0);
                }
            }
            case ScreenAction.REFRESH_GUI -> {
                if(data[0] instanceof PlayerDecorator decorator) {
                    java.util.Timer timer = new java.util.Timer();
                    if(decorator instanceof RegenerationPlayerDecorator regenerationDecorator) {
                        final int maxTicks = regenerationDecorator.getTotalDuration() / regenerationDecorator.getInterval();
                        final int[] ticks = {0};

                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                callPanelRefreshers();
                                ticks[0]++;

                                if (ticks[0] >= maxTicks) {
                                    timer.cancel();
                                }
                            }
                        }, 0, regenerationDecorator.getInterval());
                        callPanelRefreshers();
                    }
                    else {
                        callPanelRefreshers();
                        // Refreshing the panels again after the ability is over
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                callPanelRefreshers();
                            }
                        }, decorator.abilityTimeInMilliseconds() + 10);
                    }
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
