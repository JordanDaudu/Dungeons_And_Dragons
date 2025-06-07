package game.memento;

import game.characters.*;
import game.core.GameEntity;
import game.items.*;
import game.map.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorldMemento {

    private final List<PlayerCharacter> savedPlayers = new ArrayList<>();
    private final List<Enemy> savedEnemies = new ArrayList<>();
    private final List<GameItem> savedItems = new ArrayList<>();
    private Map<Position , List<GameEntity>> gridCopy = new HashMap<>();


    public GameWorldMemento(List<PlayerCharacter> players,List<Enemy>enemies,List<GameItem> items,Map<Position , List<GameEntity>> grid) throws CloneNotSupportedException {
    for(PlayerCharacter player : players) {
        Object object = player.callClone();
        if(object instanceof PlayerCharacter p){
            savedPlayers.add(p);
        }
    }
    for (Enemy enemy: enemies) {
        Object object = enemy.callClone();
        if (object instanceof Enemy e)
            savedEnemies.add(e);
    }
    for (GameItem item: items) {
        Object object = item.callClone();
        if (object instanceof GameItem i){
            savedItems.add(i);
        }
    }
    this.gridCopy = deepCopyGrid(grid);
}

    public List<PlayerCharacter> getSavedPlayers(){return savedPlayers;}

    public List<Enemy> getSavedEnemies(){return savedEnemies;}

    public List<GameItem> getSavedItems(){return savedItems;}

    public Map<Position, List<GameEntity>> getSavedMap(){return gridCopy;}


    public Map<Position, List<GameEntity>> deepCopyGrid(Map<Position, List<GameEntity>> originalGrid) throws CloneNotSupportedException {
        Map<Position, List<GameEntity>> newGrid = new HashMap<>();
        for (Map.Entry<Position, List<GameEntity>> entry : originalGrid.entrySet()) {
            List<GameEntity> clonedEntities = new ArrayList<>();
            for (GameEntity gameEntity : entry.getValue()) {
                clonedEntities.add((GameEntity)gameEntity.callClone());
            }
            newGrid.put(new Position(entry.getKey()), clonedEntities);
        }
        return newGrid;
    }
}

