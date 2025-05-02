package game.gui;

import game.characters.PlayerCharacter;
import game.engine.*;
import game.map.GameMap;

import javax.swing.*;

public class GameApplication implements ScreenListener{

    private GameWorld game;
    private GameMap map;
    private GameController controller;

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

    public GameApplication() {
        GameWorld.initialize(10, 10);
        game = GameWorld.getInstance();
        map = game.getMap();
        controller = new GameController();
    }

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
