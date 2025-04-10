package game.engine;

import game.characters.Archer;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.items.GameItem;
import game.map.GameMap;
import game.map.Position;

import java.util.ArrayList;
import java.util.List;

public class GameWorld {

    public static void main(String[] args) {
        ArrayList<PlayerCharacter> players = new ArrayList<>();
        players.add(new Archer("Jordan"));
        System.out.println(players.getFirst());
        GameWorld game = new GameWorld(players, 10, 10);
        game.placePlayersRandomly();
        game.map.printDebugGrid();
    }

    private List<PlayerCharacter> players;
    private List<Enemy> enemies;
    private List<GameItem> items;
    private GameMap map;

    public GameWorld(List<PlayerCharacter> players, int row, int col) {
        this.players = players;
        this.enemies = enemies;
        this.items = new ArrayList<>();
        this.map = GameMap.getInstance();
        this.map.init(row, col);
    }

    public void gameLoop() {
        for (PlayerCharacter player : players) {

        }
    }

    private void placePlayersRandomly() {
        for (PlayerCharacter player : players) {
            Position pos;
            do {
                int row = RandomUtil.getRandomInt(map.getRows());
                int col = RandomUtil.getRandomInt(map.getCols());
                pos = new Position(row, col);
            }
            while (map.isOccupied(pos));

            System.out.println("Added " + player.getName() + "to place: " + pos);
            player.setPosition(pos);
            map.addEntity(player);
        }
    }
}
