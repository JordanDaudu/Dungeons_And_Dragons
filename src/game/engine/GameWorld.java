package game.engine;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.items.GameItem;
import game.map.GameMap;

import java.util.List;

public class GameWorld {

    private List<PlayerCharacter> players;
    private List<Enemy> enemies;
    private List<GameItem> items;
    private GameMap map;

    public GameWorld(List<PlayerCharacter> players, List<Enemy> enemies, List<GameItem> items, GameMap map) {
        this.players = players;
        this.enemies = enemies;
        this.items = items;
        this.map = map;
    }
}
