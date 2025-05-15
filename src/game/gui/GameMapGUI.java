package game.gui;

import game.characters.*;
import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MagicElement;
import game.combat.PhysicalAttacker;
import game.core.GameEntity;
import game.engine.GameController;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.engine.RandomUtil;
import game.items.GameItem;
import game.items.Interactable;
import game.logging.GameLogger;
import game.map.GameMap;
import game.map.Position;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.List;

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
    private FloatingTextPopupGUI floatingTextPopupGameMapGUI;

    private final InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    private final ActionMap actionMap = getRootPane().getActionMap();

    private InventoryPanelGUI inventoryPanelGUI;

    private TileColorBackgroundTheme currentColorTheme = TileColorBackgroundTheme.CLEAR;
    private Color temporaryOverrideColor = null;

    private boolean showHPBar = true;

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
        setAppIcon();

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

    private void setAppIcon() {
        // TaskBar is for multiplatform support
        try {
            BufferedImage icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/logo.png")));
            if (icon == null) {
                GameLogger.getInstance().log("Icon image loaded is null. Check resource path.");
                return;
            }
            this.setIconImage(icon);
            GameLogger.getInstance().log("Application window icon set successfully.");
            if (!Taskbar.isTaskbarSupported()) {
                GameLogger.getInstance().log("Taskbar API not supported on this platform. Skipping icon set.");
                return;
            }
            Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(icon);
            GameLogger.getInstance().log("Application taskbar icon set successfully.");
        }
        catch (IOException e) {
            GameLogger.getInstance().log("Failed to load icon image: " + e.getMessage());
        }
        catch (UnsupportedOperationException e) {
            GameLogger.getInstance().log("Setting taskbar icon not supported on this platform: " + e.getMessage());
        }
        catch (SecurityException e) {
            GameLogger.getInstance().log("Security manager prevents setting taskbar icon: " + e.getMessage());
        }
        catch (Exception e) {
            GameLogger.getInstance().log("Unexpected error while setting app icon: " + e.getMessage());
        }
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
            case ScreenAction.RECEIVED_DAMAGE -> {
                if (data[0] instanceof Position position && data[1] instanceof Color color) {
                    Position pos = new Position(position);
                    TileCell tileCell = getCellAtPosition(pos);
                    if (tileCell != null) {
                        tileCell.blink(color);
                    }
                    return true;
                }
            }
            case ScreenAction.RECEIVE_DAMAGE_TEXT_ANIMATION -> {
                if(data[0] == null && data[1] instanceof Position position && data[2] instanceof Integer amount) {
                    Position pos = new Position(position);
                    TileCell tileCell = getCellAtPosition(pos);
                    if (tileCell != null) {
                        tileCell.showDamagePopup(amount, new Color(255, 0, 0)); // Red
                        return true;
                    }
                }
                else if(data[0] instanceof Combatant character && data[1] instanceof Position position && data[2] instanceof Integer amount) {
                    Position pos = new Position(position);
                    TileCell tileCell = getCellAtPosition(pos);
                    if (tileCell != null) {
                        if(character instanceof PhysicalAttacker && character.getPositionModifier().distanceTo(pos) <= 1)
                            tileCell.showDamagePopup(amount, new Color(220, 220, 220)); // Gainsboro
                        else if(character instanceof PhysicalAttacker && !(character instanceof MagicAttacker))
                            tileCell.showDamagePopup(amount, new Color(220, 220, 220)); // Gainsboro
                        else if(character instanceof MagicAttacker) {
                            if(character.getElementType() == MagicElement.ACID)
                                tileCell.showDamagePopup(amount, new Color(173, 255, 47)); // Green-yellow (like chartreuse/lime)
                            else if(character.getElementType() == MagicElement.ICE)
                                tileCell.showDamagePopup(amount, new Color(135, 206, 250)); // Light sky blue
                            else if(character.getElementType() == MagicElement.FIRE)
                                tileCell.showDamagePopup(amount, new Color(255, 85, 0)); // Bright orange with a red tint
                            else if(character.getElementType() == MagicElement.LIGHTNING)
                                tileCell.showDamagePopup(amount, new Color(138, 43, 226)); // BlueViolet (rich purple tone)
                            else
                                tileCell.showDamagePopup(amount, new Color(220, 220, 220)); // Gainsboro
                        }
                    }
                    return true;
                }
                return false;
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
    private TileCell getCellAtPosition(Position pos) {
        for (Component comp : gridPanel.getComponents()) {
            if (comp instanceof TileCell tileCell) {
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
     * Sets the current background color theme used for all tiles.
     * Triggers a repaint to reflect the change visually.
     *
     * @param colorTheme the new tile color theme to apply
     */
    public void setTileBackgroundTheme(TileColorBackgroundTheme colorTheme) {
        this.currentColorTheme = colorTheme;
        repaint();
    }

    /**
     * Sets if Combatants always have there HP shown on the map
     * Triggers a repaint to reflect the change visually.
     *
     * @param showHPBar the new choice chosen to apply
     */
    public void setShowHPBar(boolean showHPBar) {
        this.showHPBar = showHPBar;
        repaint();
    }

    private Color getEffectiveTileBackgroundColor() {
        return (temporaryOverrideColor != null) ? temporaryOverrideColor :
                (currentColorTheme != null ? currentColorTheme.getColor() : null);
    }


    public void magicWaveAnimation() {
        Set<Position> allPositions = map.getAllPositions();
        flashBackgroundEffect(new Color(138, 43, 226), 400);
        rumbleWindow(5, 400);

        for (Position pos : allPositions) {
            List<GameEntity> entitiesAtPos = map.getEntitiesAt(pos);

            for (GameEntity entity : entitiesAtPos) {
                if (entity instanceof AbstractCharacter character && character.isVisible()) {
                    int damage = 2;
                    character.receiveDamage(damage, null);
                    onAction(ScreenAction.RECEIVE_DAMAGE_TEXT_ANIMATION, null, pos, damage);
                    break; // only one character per tile gets the effect
                }
            }
        }
        // Showing up on frame which event plays out
        Timer delayTimer = new Timer(400, e -> {
            floatingTextPopupGameMapGUI = new FloatingTextPopupGUI("*Magic Wave*", new Color(138, 43, 226), 40);
            floatingTextPopupGameMapGUI.showFloatingTextPopup(this);
        });
        delayTimer.setRepeats(false); // Run only once
        delayTimer.start();
    }

    public void sandstormAnimation() {
        Set<Position> allPositions = map.getAllPositions();
        flashBackgroundEffect(new Color(194, 178, 128), 500);
        rumbleWindow(6, 500);

        for (Position pos : allPositions) {
            List<GameEntity> entitiesAtPos = map.getEntitiesAt(pos);

            for (GameEntity entity : entitiesAtPos) {
                if (entity instanceof AbstractCharacter character && character.isVisible()) {
                    int damage = 1;
                    character.receiveDamage(damage, null);
                    onAction(ScreenAction.RECEIVE_DAMAGE_TEXT_ANIMATION, null, pos, damage);
                    break; // only one character per tile gets the effect
                }
            }
        }
        // Showing up on frame which event plays out
        Timer delayTimer = new Timer(500, e -> {
            floatingTextPopupGameMapGUI = new FloatingTextPopupGUI("*Sandstorm*", new Color(194, 178, 128), 40);
            floatingTextPopupGameMapGUI.showFloatingTextPopup(this);
        });
        delayTimer.setRepeats(false); // Run only once
        delayTimer.start();
    }

    private void flashBackgroundEffect(Color flashColor, int durationMillis) {
        this.temporaryOverrideColor = flashColor;
        repaint();

        new Timer(durationMillis, e -> {
            this.temporaryOverrideColor = null;
            repaint();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    private void rumbleWindow(int intensity, int durationMillis) {
        Point originalLocation = this.getLocation();

        Thread rumbleThread = new Thread(() -> {
            long endTime = System.currentTimeMillis() + durationMillis;
            while (System.currentTimeMillis() < endTime) {
                int xOffset = RandomUtil.getRandomInt(intensity * 2 + 1) - intensity;
                int yOffset = RandomUtil.getRandomInt(intensity * 2 + 1) - intensity;

                SwingUtilities.invokeLater(() ->
                        setLocation(originalLocation.x + xOffset, originalLocation.y + yOffset)
                );

                try {
                    Thread.sleep(20); // Control rumble speed
                } catch (InterruptedException ignored) {}
            }

            // Restore original position
            SwingUtilities.invokeLater(() -> setLocation(originalLocation));
        });

        rumbleThread.start();
    }

    /**
     * Represents a single tile cell on the game map.
     * Handles mouse input for interaction and renders game entities.
     */
    private class TileCell extends JPanel implements BlinkingAnimation {

        // Data Members
        private final Position position;
        private final int tileSize = 64;
        private FloatingTextPopupGUI floatingTextPopup;

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
         * Displays a floating popup above the tile showing the amount of damage taken.
         * The popup is colored based on the type of damage (e.g., physical or magical according to the corresponding element).
         *
         * @param amount the amount of damage to display
         * @param color  the color to use for the damage popup text
         */
        public void showDamagePopup(int amount, Color color) {
            floatingTextPopup = new FloatingTextPopupGUI("-" + amount, color, 20);
            floatingTextPopup.showFloatingTextPopup(this);
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
                    controllerListener.onAction(ScreenAction.PLAYER_ATTACK, topEntity);
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
         * Draws a modern, rounded health bar for the given entity directly onto the tile's graphics context.
         * The bar fades with the entity's alpha and uses color to indicate health state:
         * - Green for high health (>= 70%)
         * - Yellow for mid-health (30% - 69%)
         * - Red for low health (< 30%)
         *
         * @param g2d   The graphics context to draw on.
         * @param entity The entity whose health bar should be drawn. Must be an AbstractCharacter and Combatant.
         * @param alpha  The transparency level (0.0 - 1.0) to apply when rendering the bar.
         */
        private void drawHealthBar(Graphics2D g2d, GameEntity entity, float alpha) {
            /*
             * Note: We use a custom-painted health bar instead of Swing components like JProgressBar
             * because each tile cell must be rendered manually and efficiently inside paintComponent().
             * Embedding real Swing components (like JProgressBar) in each tile would:
             * - Greatly reduce performance (many lightweight components per frame)
             * - Not respect the tile's alpha/transparency animation (no fade in/out)
             * - Introduce layout and rendering issues, especially in animations or scrolling
             *
             * Instead, we draw the health bar directly onto the Graphics2D context.
             * This allows:
             * - Full control over appearance
             * - Seamless integration with tile rendering logic
             * - Platform-independent modern visuals
             */
            if (!(entity instanceof AbstractCharacter character)) return;

            int current = character.getHealth();
            int max = character.getMaxHealth();
            if (max <= 0) return;

            float healthRatio = (float) current / max;

            // Dimensions
            int barWidth = getWidth() - 6;
            int barHeight = 8;
            int arc = 8; // corner roundness
            int x = 3;
            int y = getHeight() - barHeight - 4;

            // Enable anti-aliasing for smoother shapes
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // Background bar (track)
            g2d.setColor(new Color(60, 60, 60, 200));
            g2d.fillRoundRect(x, y, barWidth, barHeight, arc, arc);

            // Determine foreground color
            Color fgColor;
            if (healthRatio < 0.3f) fgColor = new Color(220, 50, 47); // red
            else if (healthRatio < 0.7f) fgColor = new Color(255, 204, 0); // yellow
            else fgColor = new Color(76, 175, 80); // green

            // Optional gradient for gloss effect
            GradientPaint gradient = new GradientPaint(
                    x, y,
                    fgColor.brighter(),
                    x, y + barHeight,
                    fgColor.darker()
            );

            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, y, (int) (barWidth * healthRatio), barHeight, arc, arc);

            // Border
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.drawRoundRect(x, y, barWidth, barHeight, arc, arc);

            // Draw health text centered inside the bar
            String healthText = current + "/" + max;
            Font font = g2d.getFont().deriveFont(Font.BOLD, 10f);
            g2d.setFont(font);

            // Measure the text width and height
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(healthText);
            int textHeight = fm.getAscent();

            int textX = x + (barWidth - textWidth) / 2;
            int textY = y + (barHeight + textHeight) / 2 - 1; // Adjust vertical centering

            // Choose a text color that stands out over the bar, e.g., black or white with shadow
            g2d.setColor(Color.BLACK);
            g2d.drawString(healthText, textX + 1, textY + 1); // shadow for readability

            g2d.setColor(Color.WHITE);
            g2d.drawString(healthText, textX, textY);
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

            Color bgColor = getEffectiveTileBackgroundColor();
            if (bgColor != null) {
                g2d.setColor(bgColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

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
                    if(showHPBar) {
                        if(entity instanceof AbstractCharacter character) {
                            drawHealthBar(g2d, entity, entityAlpha);
                        }
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