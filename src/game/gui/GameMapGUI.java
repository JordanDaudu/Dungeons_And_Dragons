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

public class GameMapGUI extends JFrame implements ScreenListener{
    private GameMap map;
    private GameController gameController;
    private ScreenListener controllerListener;
    private MapPanel mapPanel;

    public GameMapGUI(GameController gameController ,GameMap map, ScreenListener controllerListener) {
        this.gameController = gameController;
        this.map = map;
        this.controllerListener = controllerListener;
        this.gameController.getCombatSystem().setListener(this);
        initUI();
    }

    private void initUI() {
        setTitle("Dungeons & Dragons - like game");
        setSize(800, 800);  // Default size, can be resized
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Adding the panel to draw the map
        mapPanel = new MapPanel(map);  // ✅ Changed to keep a reference
        add(mapPanel);
        this.setVisible(true);
    }

    @Override
    public boolean onAction(ScreenAction action, Object... data) {
        switch (action) {
            case ScreenAction.RECEIVEDDAMAGE -> {
                if(data[0] instanceof Integer && data[1] instanceof Integer && data[2] instanceof Color) {
                    mapPanel.blink((int) data[0], (int) data[1], (Color) data[2]);
                    return true;
                }
            }
        }
        return false;
    }

    // ✅ Custom panel to draw map and handle mouse events
    private class MapPanel extends JPanel implements BlinkingAnimation {
        private final GameMap map;
        private int tileSize;
        InventoryPanel inventoryPanel;
        private final int BLINK_DURATION_MS = 300;
        private final Map<Position, Long> redBlinkPositions = new HashMap<>(); // hashmap of red blinking tiles
        private final Map<Position, Long> blinkStartTimes = new HashMap<>();
        private final Map<Position, Color> blinkColors = new HashMap<>();

        public MapPanel(GameMap map) {
            this.map = map;

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int row = e.getY() / tileSize;
                    int col = e.getX() / tileSize;
                    Position clickedPos = new Position(row, col);
                    List<GameEntity> entities = map.getEntitiesAt(clickedPos);

                    // Making sure is in distance of player
                    if (gameController.checkDistanceFromPlayer(clickedPos)) {
                        GameEntity topEntity = null;
                        if(!entities.isEmpty())
                            topEntity = entities.get(0);

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            // ✅ Left-click interaction (customize logic if needed)
                            if (entities == null || entities.isEmpty() || !entities.get(0).isVisible()) {
                                controllerListener.onAction(ScreenAction.MOVE, clickedPos);
                            }
                            else if(topEntity instanceof Enemy) {
                                controllerListener.onAction(ScreenAction.ATTACK, topEntity);

                            }
                            else if(topEntity instanceof Interactable) {
                                if(controllerListener.onAction(ScreenAction.PICKUP, clickedPos))
                                    blink(clickedPos.getRow(), clickedPos.getCol(), new Color(0,255,0,120));
                            }
                            // Player can move there
                            else if(topEntity == null) {
                                controllerListener.onAction(ScreenAction.MOVE, clickedPos);
                            }
                            repaint();
                        } else if (SwingUtilities.isRightMouseButton(e) && topEntity != null) {
                            // ✅ Right-click shows description in popup
                            JPopupMenu popupMenu = new EntityPopupMenu(topEntity);
                            popupMenu.show(MapPanel.this, e.getX(), e.getY());
                        }
                    }
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        // ✅ Middle-click shows the player inventory

                        PlayerCharacter currentPlayer = gameController.getCurrentPlayer();
                        if (currentPlayer != null) {
                            inventoryPanel = new InventoryPanel(GameMapGUI.this, gameController.getCurrentPlayer(), controllerListener);
                            inventoryPanel.setVisible(true);  // Blocks until closed if modal
                            //showInventoryPopup(currentPlayer, MapPanel.this, e.getX(), e.getY());
                        }
                        return; // Skip the rest of the logic
                    }
                }
            });



            // WASD key bindings for directional movement
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "moveUp");
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "moveLeft");
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "moveDown");
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "moveRight");

            getActionMap().put("moveUp", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Position target = getNewPositionForDirection("W");
                    controllerListener.onAction(ScreenAction.MOVE, target);
                    repaint();
                }
            });

            getActionMap().put("moveDown", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Position target = getNewPositionForDirection("S");
                    controllerListener.onAction(ScreenAction.MOVE, target);
                    repaint();
                }
            });

            getActionMap().put("moveLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Position target = getNewPositionForDirection("A");
                    controllerListener.onAction(ScreenAction.MOVE, target);
                    repaint();
                }
            });

            getActionMap().put("moveRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Position target = getNewPositionForDirection("D");
                    controllerListener.onAction(ScreenAction.MOVE, target);
                    repaint();
                }
            });


            // Key binding for 'E' to show inventory
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "showInventory");
            getActionMap().put("showInventory", new AbstractAction() {
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


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int rows = map.getRows();
            int cols = map.getCols();
            tileSize = Math.min(getWidth() / cols, getHeight() / rows);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Position pos = new Position(i, j);
                    List<GameEntity> entitiesAtPosition = map.getEntitiesAt(pos);

                    if (!entitiesAtPosition.isEmpty()) {
                        GameEntity entity = entitiesAtPosition.get(0);

                        if (entity.isVisible()) {  // ✅ Fog-of-war visibility check
                            Image entityImage = entity.getDisplayImage();
                            if (entityImage != null) {
                                g.drawImage(entityImage, j * tileSize, i * tileSize, tileSize, tileSize, this);
                            } else {
                                g.setColor(Color.GRAY);
                                g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                            }
                        } else {
                            // ✅ Hidden tile rendering
                            g.setColor(Color.WHITE);
                            g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                        }
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    }
                    if (blinkStartTimes.containsKey(pos)) {
                        long elapsed = System.currentTimeMillis() - blinkStartTimes.get(pos);
                        if (elapsed <= BLINK_DURATION_MS) {
                            Color blinkColor = blinkColors.get(pos);
                            if (blinkColor != null) {
                                g.setColor(blinkColor);
                                g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                            }
                        }
                    }
                    // Draw grid border
                    g.setColor(Color.BLACK);
                    g.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
                }
            }
        }

        public void blink(int row, int col, Color color) {
            Position pos = new Position(row, col);
            blinkStartTimes.put(pos, System.currentTimeMillis());
            blinkColors.put(pos, color);

            Timer timer = new Timer(50, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                    Long startTime = blinkStartTimes.get(pos);
                    if (startTime == null || System.currentTimeMillis() - startTime > BLINK_DURATION_MS) {
                        blinkStartTimes.remove(pos);
                        blinkColors.remove(pos);
                        ((Timer) e.getSource()).stop();
                    }
                }
            });
            timer.start();
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