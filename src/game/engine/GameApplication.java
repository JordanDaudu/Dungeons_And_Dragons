package game.engine;

import game.characters.PlayerCharacter;
import game.gui.PlayerCreationPanel;
import game.gui.StartingScreenGUI;
import game.map.GameMap;

import javax.swing.*;

/**
 * Entry point and core application manager for the game GUI.
 * Manages screen transitions, game initialization, and the main game loop.
 */
public class GameApplication implements ScreenListener{

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

        GameApplication gameApplication = new GameApplication();
        GameWorld game = GameWorld.getInstance();

        SoundManager.playMusic("preparations", true);
        int numberOfPlayers = -1;
        do {
            numberOfPlayers = StartingScreenGUI.askForPlayers();
        }
        while (numberOfPlayers == -1);

        for(int i = 0; i < numberOfPlayers; i++) {
            PlayerCreationPanel startScreen = new PlayerCreationPanel(gameApplication);
            startScreen.showModal();  // This will block until the user clicks "Start Game"
        }

        game.collectPlayersFromMap();
        game.getMap().populateRandomEntities();

        game.collectEnemiesFromMap();
        game.collectItemsFromMap();

        // After creating the character, move to the map screen:
        SoundManager.crossfadeTo("battle1", true);

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
    public GameApplication() {
        GameWorld.initialize(10, 10);
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
                if (!foundAlive) {
                    controller.onAction(ScreenAction.EXIT_GAME, (Object) null);
                }
            }
        });
        turnTimer.start();
    }

}
