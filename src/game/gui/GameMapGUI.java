package game.gui;

import game.core.GameEntity;
import game.map.GameMap;
import game.map.Position;

import java.util.List;
import javax.swing.*;
import java.awt.*;

public class GameMapGUI extends JFrame {
    private GameMap map;

    public GameMapGUI(GameMap map) {
        this.map = map;
        initUI();
    }

    private void initUI() {
        setTitle("Dungeons & Dragons - like game");
        setSize(800, 800);  // Default size, can be resized
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Adding the panel to draw the map
        add(new MapPanel(map));
    }

    // Custom panel class to draw the GameMap
    private class MapPanel extends JPanel {
        private GameMap map;

        public MapPanel(GameMap map) {
            this.map = map;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Get number of rows and columns
            int rows = map.getRows();
            int cols = map.getCols();

            // Calculate the tile size based on the panel size
            int tileSize = Math.min(getWidth() / cols, getHeight() / rows);  // Scale based on the window size

            // Set background color
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());  // Fill the entire window

            // Draw the entities
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    List<GameEntity> entitiesAtPosition = map.getEntitiesAt(new Position(i, j));

                    if (!entitiesAtPosition.isEmpty()) {
                        GameEntity entity = entitiesAtPosition.get(0);  // Get the first entity

                        // Get the entity image
                        Image entityImage = entity.getDisplayImage();

                        // Check if image is not null
                        if (entityImage != null) {
                            // Scale the image to fit the new tile size
                            g.drawImage(entityImage, j * tileSize, i * tileSize, tileSize, tileSize, this);
                        } else {
                            System.out.println("No image for " + entity.getClass().getSimpleName());
                        }
                    }
                    // Draw the tile border
                    g.setColor(Color.BLACK);
                    g.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
                }
            }
        }
    }
}
