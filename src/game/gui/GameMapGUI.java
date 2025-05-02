package game.gui;

import game.characters.*;
import game.combat.MagicAttacker;
import game.core.GameEntity;
import game.engine.GameController;
import game.engine.ScreenAction;
import game.engine.ScreenListener;
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

public class GameMapGUI extends JFrame implements ScreenListener{
    private final GameMap map;
    private final GameController gameController;
    private final ScreenListener controllerListener;
    private JPanel gridPanel;

    InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = getRootPane().getActionMap();

    private InventoryPanel inventoryPanel;

    private static final int ANIMATION_DELAY = 30; // ms between animation steps
    private static final float FADE_STEP = 0.1f;   // alpha step per tick

    // Fields for fade animations
    private final Map<GameEntity, Float> entityAlphaMap = new HashMap<>();
    private final Timer animationTimer; // this runs periodically for smooth fade in / out animations

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
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("W"));
            }
        });

        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("S"));
            }
        });

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("A"));
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePlayerTo(getNewPositionForDirection("D"));
            }
        });

        // Key binding for 'E' to show inventory
        inputMap.put(KeyStroke.getKeyStroke("E"), "showInventory");
        actionMap.put("showInventory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerCharacter currentPlayer = gameController.getCurrentPlayer();
                if (currentPlayer != null) {
                    // Show inventory at center of the panel
                    inventoryPanel = new InventoryPanel(GameMapGUI.this, gameController.getCurrentPlayer(), controllerListener);
                    inventoryPanel.setVisible(true);  // Blocks until closed if modal
                }
            }
        });

        // Key binding for 'Q' to show player status
        inputMap.put(KeyStroke.getKeyStroke("Q"), "showStatus");
        actionMap.put("showStatus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerCharacter currentPlayer = gameController.getCurrentPlayer();
                if (currentPlayer != null) {
                    PlayerStatusDialog statusDialog = new PlayerStatusDialog(GameMapGUI.this, currentPlayer);
                    statusDialog.setVisible(true);  // modal dialog
                }
            }
        });

        // Key binding for "ESC" to show settings menu
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "openSettings");
        actionMap.put("openSettings", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsMenuGUI(GameMapGUI.this).setVisible(true);
            }
        });

        initUI();
    }

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

    private void movePlayerTo(Position position) {
        controllerListener.onAction(ScreenAction.MOVE, position);
        repaint(); // repaint the entire frame
    }

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

    private class TileCell extends JPanel implements BlinkingAnimation {
        private final Position position;
        private final int tileSize = 64;

        public TileCell(Position position) {
            this.position = position;
            setPreferredSize(new Dimension(tileSize, tileSize));  // Tile size
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            addMouseListener(new MouseAdapter() {
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
                        handleMiddleClick(e);
                    }
                }
            });
        }

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

        private Position getPosition() {
            return position;
        }

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

        private void handleRightClick(MouseEvent e, List<GameEntity> entitiesAtPos) {
            if (!entitiesAtPos.isEmpty()) {
                GameEntity topEntity = entitiesAtPos.getFirst();
                // Show the popup at the relative grid position
                JPopupMenu popupMenu = new EntityPopupMenu(topEntity);
                popupMenu.show(TileCell.this, e.getX(), e.getY());
            }
        }

        private void handleMiddleClick(MouseEvent e) {
            PlayerCharacter currentPlayer = gameController.getCurrentPlayer();
            if (currentPlayer != null) {
                inventoryPanel = new InventoryPanel(GameMapGUI.this, currentPlayer, controllerListener);
                inventoryPanel.setVisible(true);  // Blocks until closed if modal
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            List<GameEntity> entitiesAtPos = map.getEntitiesAt(position);

            if (!entitiesAtPos.isEmpty()) {
                GameEntity entity = entitiesAtPos.get(0);
                Float entityAlpha = entityAlphaMap.getOrDefault(entity, 0f);
                // I'm checking if it's alpha is greater than 0 instead of checking isVisible because
                // I want to trigger the fade out animation and not immediately disappear
                if (entityAlpha > 0f) {
                    Graphics2D g2d = (Graphics2D) g.create();
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

    private class EntityPopupMenu extends JPopupMenu {
        public EntityPopupMenu(GameEntity entity) {
            String helperString = "";
            if (entity == null || !entity.isVisible()) {
                addDisabledItem("There is nothing here");
            }
            else if (entity instanceof Enemy enemy) {
                addDisabledItem("Enemy: " + enemy.getClass().getSimpleName());
                addDisabledItem("Health: " + enemy.getHealth());
                addDisabledItem("Type: " + enemy.getType());
                if(enemy instanceof MagicAttacker)
                    addDisabledItem("Element: " + enemy.getElementType());
                addDisabledItem("Description: " + enemy.getDescription());
            }
            else if(entity instanceof PlayerCharacter player) {
                addDisabledItem("Name: " + player.getName());
                addDisabledItem("Type: " + player.getType());
                addDisabledItem("Health: " + player.getHealth());
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

        private void addDisabledItem(String text) {
            JMenuItem item = new JMenuItem(text);
            item.setEnabled(false);
            add(item);
        }
    }
}