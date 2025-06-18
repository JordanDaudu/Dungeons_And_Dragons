package game.engine;

import game.characters.EnemyTask;
import game.characters.PlayerCharacter;
import game.characters.PlayerFactory;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.gui.PlayerCreationPanelGUI;
import game.gui.PlayerCustomizationGUI;
import game.gui.StartingScreenGUI;
import game.map.GameMap;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Entry point and core application manager for the game GUI.
 * Handles screen transitions, gathers player settings, initializes game state,
 * and starts the main game loop with GUI updates and enemy threading.
 */
public class GameApplication implements ScreenListener {

    // Data Members
    private final GameWorld game;
    private final GameMap map;
    private final GameController controller;

    // Methods
    /**
     * Main method to launch the game.
     * <p>
     * This method loads resources, prompts the user for game settings via GUI,
     * initializes the game world and map, creates player characters, populates the map with enemies and items,
     * and then starts the game loop and global events in a GUI environment.
     * </p>
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

        GameApplication gameApplication = new GameApplication(gameSettings);
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
        ScheduledExecutorService enemyScheduler = Executors.newScheduledThreadPool(EnemyTask.calculateStartingEnemyThreadPoolSize(mapRows * mapCols));
        game.attachGameControllerToEnemies(gameApplication.controller, enemyScheduler, gameApplication.controller.getEnemyRunningFlag());
        gameApplication.controller.setEnemyScheduler(enemyScheduler); // Add to the controller the thread pool of enemies

        // After creating the character, move to the map screen:
        SoundManager.crossfadeTo("battle1", true);

        // Starting the global events in the map
        gameApplication.controller.startManagerEvent();

        // Setting up first player correctly to start game
        game.setCurrentPlayer(game.getPlayers().getFirst());
        GameMap.getInstance().updatePlayerView(game.getCurrentPlayer().getPosition());
        gameApplication.controller.getGameMapGUI().toggleSidePanels(true);

        // Ensure the map is fully drawn and refreshed
        gameApplication.controller.getGameMapGUI().setVisible(true);
        gameApplication.controller.getGameMapGUI().revalidate();
        gameApplication.controller.getGameMapGUI().repaint();

        // Start the turn loop
        gameApplication.controller.startGameLoop();
    }

    /**
     * Constructs the main game application.
     * <p>
     * Initializes the singleton game world with the specified map dimensions,
     * and sets up the map and game controller.
     * </p>
     *
     * @param gameSettings settings of the game
     */
    public GameApplication(GameSettings gameSettings) {
        GameWorld.initialize(gameSettings);
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
            if(data[0] instanceof String name && data[1] instanceof String selectedClass && data[2] instanceof PlayerCustomizationGUI customization && data[3] instanceof List<?> rawList) {

                List<String> abilities = (List<String>) rawList;

                PlayerCharacter player = switch (selectedClass) {
                    case "Warrior" -> {
                        PlayerFactory.WarriorBuilder builder = PlayerFactory.getBuilder("Warrior"); // Explicit cast
                        builder.setDefence(customization.getDefenseMod())
                                .setName(name)
                                .setPower(customization.getPowerMod())
                                .setHealth(customization.getHealthMod());

                        abilities.forEach(builder::addAbility); // Apply abilities
                        yield builder.build();
                    }
                    case "Archer" -> {
                        PlayerFactory.ArcherBuilder builder = PlayerFactory.getBuilder("Archer");
                        builder.setAccuracy(customization.getAccuracyMod())
                                .setName(name)
                                .setPower(customization.getPowerMod())
                                .setHealth(customization.getHealthMod());

                        abilities.forEach(builder::addAbility);
                        yield builder.build();
                    }
                    case "Mage" -> {
                        PlayerFactory.MageBuilder builder = PlayerFactory.getBuilder("Mage");
                        builder.setMagicElement(customization.getSelectedElement())
                                .setName(name)
                                .setPower(customization.getPowerMod())
                                .setHealth(customization.getHealthMod());

                        abilities.forEach(builder::addAbility);
                        yield builder.build();
                    }
                    default -> throw new IllegalArgumentException("Unknown class: " + selectedClass);
                };

                // Check if every element in the list is a String
                boolean allStrings = rawList.stream().allMatch(element -> element instanceof String);
                if (allStrings) {
                    @SuppressWarnings("unchecked")
                    List<String> abilityNames = (List<String>) rawList;
                }
                else {
                    System.err.println("No ability for " + player.getName());
                }

                map.addCharacter(player);
                return true;
            }
            return false;
        }
        return false;
    }
}