package game.engine;

import game.characters.PlayerCharacter;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.gui.PlayerCreationPanelGUI;
import game.gui.StartingScreenGUI;
import game.map.GameMap;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Entry point and core application manager for the game GUI.
 * Manages screen transitions, game initialization, and the main game loop.
 */
public class GameApplication implements ScreenListener {

    // Data Members
    private final GameWorld game;
    private final GameMap map;
    private final GameController controller;

    // Methods
    /**
     * Main method to launch the game.
     * Loads sounds, gathers player input, initializes characters and entities,
     * and starts the turn-based loop.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SoundManager.loadMusic();
        SoundManager.loadSoundEffects();

        SoundManager.playMusic("preparations", true);
        GameSettings gameSettings;
        do {
            gameSettings = StartingScreenGUI.askForSettings();
        }
        while (!GameSettings.checkSettings(gameSettings));

        int numberOfPlayers = gameSettings.getPlayers();
        int mapRows = gameSettings.getRows();
        int mapCols = gameSettings.getCols();

        GameApplication gameApplication = new GameApplication(mapRows, mapCols);
        GameWorld game = GameWorld.getInstance();

        for(int i = 0; i < numberOfPlayers; i++) {
            PlayerCreationPanelGUI startScreen = new PlayerCreationPanelGUI(gameApplication);
            startScreen.showModal();  // This will block until the user clicks "Start Game"
        }

        // Collecting and populating the map
        game.collectPlayersFromMap();
        game.getMap().populateRandomEntities();
        game.collectEnemiesFromMap();
        game.collectItemsFromMap();

        // Adding enemies to a thread pool so they can move in game
        ScheduledExecutorService enemyScheduler = Executors.newScheduledThreadPool(3);
        game.attachGameControllerToEnemies(gameApplication.controller, enemyScheduler, gameApplication.controller.getEnemyRunningFlag());
        gameApplication.controller.setEnemyScheduler(enemyScheduler); // Add to the controller the thread pool of enemies

        // After creating the character, move to the map screen:
        SoundManager.crossfadeTo("battle1", true);

        // Starting the global events in the map
        gameApplication.controller.startManagerEvent();

        // Ensure the map is fully drawn and refreshed
        gameApplication.controller.getGameMapGUI().setVisible(true);
        gameApplication.controller.getGameMapGUI().revalidate();
        gameApplication.controller.getGameMapGUI().repaint();
        // Start the turn loop (not blocking the GUI thread, it's for better performance on the cpu)
        gameApplication.startGameLoop();  // Start the turn-based game loop

        // Set initial player to start game
        game.setCurrentPlayer(game.getPlayers().getFirst());
        GameMap.getInstance().updatePlayerView(game.getCurrentPlayer().getPosition());
    }

    /**
     * Constructs the main game application.
     * Initializes the game world, map, and controller.
     */
    public GameApplication(int rows, int cols) {
        GameWorld.initialize(rows, cols);
        game = GameWorld.getInstance();
        map = game.getMap();
        controller = new GameController();
    }

    /**
     * Responds to screen actions, such as starting the game after character creation.
     *
     * @param action the action that occurred on the screen
     * @param data   optional additional data, e.g., player name and class
     * @return true if action was handled successfully, false otherwise
     */
    @Override
    public boolean onAction(ScreenAction action, Object... data) {
        if (action == ScreenAction.START_GAME) {
            // Create character from data given
            if(data[0] instanceof String && data[1] instanceof String) {
                String name = (String) data[0];
                String selectedClass = (String) data[1];
                map.createCharacter(name, selectedClass);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Starts the main turn-based game loop using a timer.
     * Each iteration checks if a player's turn has ended and advances to the next alive player.
     */
    private void startGameLoop() {
        // checks if the turn has ended, if it did switches to the next current player
        Timer turnTimer = new Timer(200, e -> {
            if (controller.getEndTurn()) {
                PlayerCharacter currentPlayer = game.getCurrentPlayer();
                System.out.println(currentPlayer.getName() + "'s turn ended.");

                controller.setEndTurn(false); // Reset for next turn

                // Advance to next player
                int currentIndex = game.getPlayers().indexOf(currentPlayer);
                int size = game.getPlayers().size();
                boolean foundAlive = false;

                for (int i = 0; i < size; i++) {
                    int nextIndex = (currentIndex + 1 + i) % size;
                    PlayerCharacter nextPlayer = game.getPlayers().get(nextIndex);
                    if (!nextPlayer.isDead()) {
                        game.setCurrentPlayer(nextPlayer);
                        GameMap.getInstance().updatePlayerView(nextPlayer.getPosition());
                        foundAlive = true;
                        break;
                    }
                }
                if (!foundAlive || game.getEnemies().isEmpty()) {
                    controller.onAction(ScreenAction.EXIT_GAME, (Object) null);
                }
            }
        });
        turnTimer.start();
    }
}
