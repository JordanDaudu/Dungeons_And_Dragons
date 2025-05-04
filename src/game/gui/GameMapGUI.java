package game.gui;

import game.characters.*;
import game.combat.MagicAttacker;
import game.core.GameEntity;
import game.engine.GameController;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.items.GameItem;
import game.items.Interactable;
import game.map.GameMap;
import game.map.Position;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * The GameMapGUI class represents the main gameplay interface,
 * handling user input (keyboard/mouse), entity rendering, and interaction logic
 * with the map grid for a turn-based game.
 * Features include:
 * - Player movement (WASD keys)
 * - Interaction via mouse (attack, pickup, inspect)
 * - Inventory and status access
 * - Entity visibility animations (fade in/out)
 */
public class GameMapGUI extends JFrame implements ScreenListener{

    // Data Members
    private final GameMap map;
    private final GameController gameController;
    private final ScreenListener controllerListener;
    private JPanel gridPanel;

    private final InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    private final ActionMap actionMap = getRootPane().getActionMap();

    private InventoryPanelGUI inventoryPanelGUI;

    private static final int ANIMATION_DELAY = 30; // ms between animation steps
    private static final float FADE_STEP = 0.1f;   // alpha step per tick

    // Fields for fade animations
    private final Map<GameEntity, Float> entityAlphaMap = new HashMap<>();
    private final Timer animationTimer; // this runs periodically for smooth fade in / out animations

    // Methods
    /**
     * Constructs the GameMapGUI.
     *
     * @param gameController        the game controller handling core logic
     * @param map                   the game map to render and interact with
     * @param controllerListener    listener to handle screen-related events
     */
    public GameMapGUI(GameController gameController, GameMap map, ScreenListener controllerListener) {
        this.gameController = gameController;
        this.map = map;
        this.controllerListener = controllerListener;
        this.gameController.getCombatSystem().setListener(this);
        animationTimer = new Timer(ANIMATION_DELAY, e -> animateAlphaTransitions());
        animationTimer.start();

        inputMap.put(KeyStroke.getKeyStroke("W"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("A"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("S"), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke("D"), "moveRight");

        actionMap.put("moveUp", new AbstractAction() {
            /**
             * Moves the player upwards on the map.
             * This action is triggered when the user presses the 'W' key.
             *
             * @param e the action event triggered by the key press
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("W"));
            }
        });

        actionMap.put("moveDown", new AbstractAction() {
            /**
             * Moves the player downwards on the map.
             * This action is triggered when the user presses the 'S' key.
             *
             * @param e the action event triggered by the key press
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("S"));
            }
        });

        actionMap.put("moveLeft", new AbstractAction() {
            /**
             * Moves the player left on the map.
             * This action is triggered when the user presses the 'A' key.
             *
             * @param e the action event triggered by the key press
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("A"));
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            /**
             * Moves the player right on the map.
             * This action is triggered when the user presses the 'D' key.
             *
             * @param e the action event triggered by the key press
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("D"));
            }
        });

        // Key binding for 'E' to show inventory
        inputMap.put(KeyStroke.getKeyStroke("E"), "showInventory");
        actionMap.put("showInventory", new AbstractAction() {
            /**
             * Displays the player's inventory in a modal dialog.
             * This action is triggered when the user presses the 'E' key.
             *
             * @param e the action event triggered by the key press
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerCharacter currentPlayer = gameController.getCurrentPlayer();
                if (currentPlayer != null) {
                    // Show inventory at center of the panel
                    inventoryPanelGUI = new InventoryPanelGUI(GameMapGUI.this, gameController.getCurrentPlayer(), controllerListener);
                    inventoryPanelGUI.setVisible(true);  // Blocks until closed if modal
                }
            }
        });

        // Key binding for 'Q' to show player status
        inputMap.put(KeyStroke.getKeyStroke("Q"), "showStatus");
        actionMap.put("showStatus", new AbstractAction() {
            /**
             * Displays a modal dialog showing the current player's status.
             * This is triggered when the user presses the 'Q' key.
             *
             * @param e the action event triggered by key binding
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerCharacter currentPlayer = gameController.getCurrentPlayer();
                if (currentPlayer != null) {
                    PlayerStatusDialogGUI statusDialog = new PlayerStatusDialogGUI(GameMapGUI.this, currentPlayer);
                    statusDialog.setVisible(true);  // modal dialog
                }
            }
        });

        // Key binding for "ESC" to show settings menu
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "openSettings");
        actionMap.put("openSettings", new AbstractAction() {
            /**
             * Opens the settings menu as a modal window.
             * Triggered when the user presses the 'Escape' key.
             *
             * @param e the action event triggered by key binding
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsMenuGUI(GameMapGUI.this).setVisible(true);
            }
        });

        initUI();
    }

    /**
     * Initializes the JFrame and populates the map grid with interactive cells.
     */
    private void initUI() {
        setTitle("Dungeons & Dragons - like game");
        setSize(800, 800);  // Default size, can be resized
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (GameEntity entity : map.getAllEntities()) {
            entityAlphaMap.put(entity, entity.isVisible() ? 1f : 0f);
        }

        // Setting up GridLayout for the map
        int rows = map.getRows();
        int cols = map.getCols();
        gridPanel = new JPanel(new GridLayout(rows, cols));

        // Adding grid cells (each corresponding to a tile)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position pos = new Position(i, j);
                TileCell tileCell = new TileCell(pos);
                gridPanel.add(tileCell);
            }
        }

        add(gridPanel);
        this.setVisible(false);
    }

    /**
     * Handles screen actions sent from the game engine or other UI components.
     *
     * @param action the type of screen action to process
     * @param data   optional parameters for the action
     * @return true if the action was handled, false otherwise
     */
    @Override
    public boolean onAction(ScreenAction action, Object... data) {
        switch (action) {
            case ScreenAction.RECEIVEDDAMAGE -> {
                if (data[0] instanceof Integer && data[1] instanceof Integer && data[2] instanceof Color) {
                    Position pos = new Position((int) data[0], (int) data[1]);
                    TileCell tileCell = (TileCell) getCellAtPosition(pos);
                    if (tileCell != null) {
                        tileCell.blink((Color) data[2]);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Attempts to move the player to the specified position.
     *
     * @param position the new position to move the player to
     */
    private void movePlayerTo(Position position) {
        controllerListener.onAction(ScreenAction.MOVE, position);
        repaint(); // repaint the entire frame
    }

    /**
     * Gets the GUI tile component corresponding to a map position.
     *
     * @param pos the position to search for
     * @return the corresponding TileCell, or null if not found
     */
    private Component getCellAtPosition(Position pos) {
        for (Component comp : gridPanel.getComponents()) {
            if (comp instanceof TileCell) {
                TileCell tileCell = (TileCell) comp;
                if (tileCell.getPosition().equals(pos)) {
                    return tileCell;
                }
            }
        }
        return null;
    }

    /**
     * Handles smooth visibility transitions (fade-in/out) for entities.
     */
    private void animateAlphaTransitions() {
        Set<GameEntity> currentlyVisible = map.getVisibleEntities();
        boolean needsRepaint = false;

        for (GameEntity entity : map.getAllEntities()) {
            float currentAlpha = entityAlphaMap.getOrDefault(entity, 0f);
            boolean isVisible = currentlyVisible.contains(entity);

            if (isVisible) {
                if (currentAlpha < 1f) {
                    currentAlpha = Math.min(1f, currentAlpha + FADE_STEP);
                    needsRepaint = true;
                }
            } else {
                if (currentAlpha > 0f) {
                    currentAlpha = Math.max(0f, currentAlpha - FADE_STEP);
                    needsRepaint = true;
                }
            }

            entityAlphaMap.put(entity, currentAlpha);

            if (needsRepaint) {
                Component cell = getCellAtPosition(entity.getPosition());
                if (cell != null) {
                    cell.repaint();
                }
            }
        }
    }

    /**
     * Translates keyboard direction input (WASD) into a new map position.
     *
     * @param direction the key direction (W, A, S, D)
     * @return the new target position for movement
     */
    private Position getNewPositionForDirection(String direction) {
        Position currentPos = gameController.getCurrentPlayer().getPosition();
        int row = currentPos.getRow();
        int col = currentPos.getCol();

        return switch (direction) {
            case "W" -> new Position(row - 1, col);
            case "A" -> new Position(row, col - 1);
            case "S" -> new Position(row + 1, col);
            case "D" -> new Position(row, col + 1);
            default -> currentPos;
        };
    }

    /**
     * Represents a single tile cell on the game map.
     * Handles mouse input for interaction and renders game entities.
     */
    private class TileCell extends JPanel implements BlinkingAnimation {

        // Data Members
        private final Position position;
        private final int tileSize = 64;

        // Methods
        /**
         * Creates a new TileCell at a specified position.
         *
         * @param position the map position this cell represents
         */
        public TileCell(Position position) {
            this.position = position;
            setPreferredSize(new Dimension(tileSize, tileSize));  // Tile size
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            addMouseListener(new MouseAdapter() {
                /**
                 * Handles mouse press events on a tile:
                 * <ul>
                 *     <li><b>Left-click</b>: Moves player or attacks/interacts if entity is present (if in range).</li>
                 *     <li><b>Right-click</b>: Opens context menu with entity details (if in range).</li>
                 *     <li><b>Middle-click</b>: Opens inventory regardless of range.</li>
                 * </ul>
                 *
                 * @param e the mouse event triggered by the user
                 */
                @Override
                public void mousePressed(MouseEvent e) {
                    // Handle click events per tile
                    List<GameEntity> entitiesAtPos = map.getEntitiesAt(position);
                    if(gameController.checkDistanceFromPlayer(position)) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            handleLeftClick(entitiesAtPos);
                        }
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            handleRightClick(e, entitiesAtPos);
                        }
                        repaint();
                    }
                    if(SwingUtilities.isMiddleMouseButton(e)) {
                        handleMiddleClick();
                    }
                }
            });
        }

        /**
         * Causes the tile cell to briefly blink in the specified color.
         *
         * @param color the blink color to use
         */
        @Override
        public void blink(Color color) {
            setBackground(color);
            Timer blinkTimer = new Timer(300, e -> {
                // Reset the background color after blink duration
                setBackground(null);
                repaint();
                ((Timer) e.getSource()).stop();
            });
            blinkTimer.start();
        }

        /**
         * Returns the map position this tile cell represents.
         *
         * @return the position object
         */
        private Position getPosition() {
            return position;
        }

        /**
         * Handles left-click events: move, attack, or interact.
         *
         * @param entitiesAtPos list of entities at this tile position
         */
        private void handleLeftClick(List<GameEntity> entitiesAtPos) {
            if (entitiesAtPos.isEmpty()) {
                // Made a centralized function for keyboard and mouse
                movePlayerTo(position);
            }
            else {
                GameEntity topEntity = entitiesAtPos.getFirst();
                if (topEntity instanceof Enemy) {
                    controllerListener.onAction(ScreenAction.ATTACK, topEntity);
                }
                else if (topEntity instanceof Interactable) {
                    if(controllerListener.onAction(ScreenAction.PICKUP, position))
                        blink(Color.GREEN);
                }
            }
        }

        /**
         * Handles right-click events: show popup information about entity.
         *
         * @param e              the MouseEvent from the click
         * @param entitiesAtPos  list of entities at this tile
         */
        private void handleRightClick(MouseEvent e, List<GameEntity> entitiesAtPos) {
            if (!entitiesAtPos.isEmpty()) {
                GameEntity topEntity = entitiesAtPos.getFirst();
                // Show the popup at the relative grid position
                JPopupMenu popupMenu = new EntityPopupMenu(topEntity);
                popupMenu.show(TileCell.this, e.getX(), e.getY());
            }
        }

        /**
         * Handles middle-click: opens the inventory panel.
         */
        private void handleMiddleClick() {
            PlayerCharacter currentPlayer = gameController.getCurrentPlayer();
            if (currentPlayer != null) {
                inventoryPanelGUI = new InventoryPanelGUI(GameMapGUI.this, currentPlayer, controllerListener);
                inventoryPanelGUI.setVisible(true);  // Blocks until closed if modal
            }
        }

        /**
         * Renders the cell, including entity sprite with alpha fading.
         *
         * @param g the Graphics context
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create(); // Copy graphics context
            g2d.setComposite(AlphaComposite.Clear); // Enable transparent clearing
            g2d.fillRect(0, 0, getWidth(), getHeight()); // Clear old pixels
            g2d.setComposite(AlphaComposite.SrcOver); // Restore normal drawing mode

            super.paintComponent(g);
            List<GameEntity> entitiesAtPos = map.getEntitiesAt(position);

            if (!entitiesAtPos.isEmpty()) {
                GameEntity entity = entitiesAtPos.getFirst();
                Float entityAlpha = entityAlphaMap.getOrDefault(entity, 0f);
                // I'm checking if it's alpha is greater than 0 instead of checking isVisible because
                // I want to trigger the fade out animation and not immediately disappear
                if (entityAlpha > 0f) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, entityAlpha));
                    Image entityImage = entity.getDisplayImage();

                    if (entityImage != null) {
                        g2d.drawImage(entityImage, 0, 0, getWidth(), getHeight(), this);
                    }
                    else {
                        g2d.setColor(Color.GRAY);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                    }
                    g2d.dispose();
                }
            }
        }
    }

    /**
     * A context menu that displays information about the entity at a tile.
     */
    private class EntityPopupMenu extends JPopupMenu {

        // Methods
        /**
         * Constructs a popup menu for the given entity with description.
         *
         * @param entity the game entity to describe
         */
        public EntityPopupMenu(GameEntity entity) {
            if (entity == null || !entity.isVisible()) {
                addDisabledItem("There is nothing here");
            }
            else if (entity instanceof Enemy enemy) {
                addEnemyHealthBar(enemy);
                addDisabledItem("Enemy: " + enemy.getClass().getSimpleName());
                addDisabledItem("Type: " + enemy.getType());

                if(enemy instanceof MagicAttacker)
                    addDisabledItem("Element: " + enemy.getElementType());
                addDisabledItem("Description: " + enemy.getDescription());
            }
            else if(entity instanceof PlayerCharacter player) {
                addPlayerHealthBar(player);
                addDisabledItem("Name: " + player.getName());
                addDisabledItem("Type: " + player.getType());
                addDisabledItem("Power: " + player.getPower());
                if(player instanceof MagicAttacker)
                    addDisabledItem("Element: " + player.getElementType());
                addDisabledItem("Treasure Points: " + player.getTreasurePoints());
            }
            else if (entity instanceof GameItem item) {
                addDisabledItem(item.getDescription());
            }
            else {
                addDisabledItem("There is something there");
            }
        }

        /**
         * Adds a disabled menu item with the given text.
         *
         * @param text the text to display in the menu item
         */
        private void addDisabledItem(String text) {
            JMenuItem item = new JMenuItem(text);
            item.setEnabled(false);

            item.setFont(new Font("Arial", Font.BOLD, 14)); // Set bold and slightly larger font
            item.setForeground(new Color(0, 128, 0)); // Dark green for better contrast
            item.setBackground(Color.WHITE); // Light background color to make text pop more

            add(item);
        }

        /**
         * Adds a visual health bar for a player character to the popup menu.
         *
         * @param player the player character whose health will be displayed
         */
        private void addPlayerHealthBar(PlayerCharacter player) {
            JPanel healthPanel = new JPanel();
            healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.X_AXIS));
            healthPanel.add(new JLabel("Health:"));
            healthPanel.add(new HealthBarPanelGUI(player.getHealth(), player.getMaxHealth()));
            add(healthPanel);
        }

        /**
         * Adds a visual health bar for an enemy to the popup menu.
         *
         * @param enemy the enemy whose health will be displayed
         */
        private void addEnemyHealthBar(Enemy enemy) {
            JPanel healthPanel = new JPanel();
            healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.X_AXIS));
            healthPanel.add(new JLabel("Health:"));
            healthPanel.add(new HealthBarPanelGUI(enemy.getHealth(), enemy.getMaxHealth()));
            add(healthPanel);
        }
    }
}