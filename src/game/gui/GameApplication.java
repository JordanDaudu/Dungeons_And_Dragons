package game.gui;

import game.engine.GameWorld;
import game.engine.ScreenAction;
import game.engine.ScreenListener;
import game.engine.SoundManager;

public class GameApplication implements ScreenListener{

    private GameWorld game;  // Add a GameWorld field

    public static void main(String[] args) {
        SoundManager.loadMusic();
        GameApplication gameApplication = new GameApplication();
        GameWorld game = GameWorld.getInstance();
        // game.getPlayers().add(game.createCharacter());

        SoundManager.playMusic("preparations", true);
        StartScreen startScreen = new StartScreen(gameApplication);
        startScreen.showModal();  // This will block until the user clicks "Start Game"

        game.collectPlayersFromMap();
//        for(PlayerCharacter player : game.getPlayers())
//            game.getMap().placePlayerRandomly(player);
        game.getMap().populateRandomEntities();

        game.collectEnemiesFromMap();
        game.collectItemsFromMap();

        // After creating the character, move to the map screen:
        GameMapGUI mapGUI = new GameMapGUI(game.getMap());
        mapGUI.setVisible(true);
        // Ensure the map is fully drawn and refreshed
        mapGUI.revalidate();
        mapGUI.repaint();
    }

    public GameApplication() {
        GameWorld.initialize(10, 10);
        game = GameWorld.getInstance();
    }

    @Override
    public void onAction(ScreenAction action, Object data) {
        if (action == ScreenAction.START_GAME) {
            // After creating the character, move to the map screen:
            SoundManager.crossfadeTo("battle1", true);
        }
    }
}
