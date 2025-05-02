package game.engine;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.CombatSystem;
import game.combat.RangedFighter;
import game.gui.GameMapGUI;
import game.gui.GameOverGUI;
import game.items.GameItem;
import game.items.Interactable;
import game.items.Treasure;
import game.map.GameMap;
import game.map.Position;

import java.util.Scanner;

public class GameController implements ScreenListener {

    private GameWorld gameWorld;
    private final GameMap map;
    private final CombatSystem combatSystem = CombatSystem.getInstance();
    private final GameMapGUI gameMapGUI;
    private static final Scanner scanner = new Scanner(System.in);
    private boolean endTurn = false;

    public GameController() {
        if(GameWorld.isInitialized())
            gameWorld = GameWorld.getInstance();
        map = GameMap.getInstance();
        this.gameMapGUI = new GameMapGUI(this, map, this);
    }

    public boolean getEndTurn() {return endTurn;}

    public boolean setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
        return true;
    }

    public CombatSystem getCombatSystem() {return combatSystem;}

    public GameMapGUI getGameMapGUI() {return gameMapGUI;}

    public PlayerCharacter getCurrentPlayer() {
        return gameWorld.getCurrentPlayer();
    }

    public boolean checkDistanceFromPlayer(Position clickedPos) {
        return gameWorld.getCurrentPlayer().getPosition().distanceTo(clickedPos) <= 2;
    }

    // For Mouse support
    public Position attemptMoveTo(Position clickedPos) {
        System.out.println("Entered function");
        if(gameWorld.getCurrentPlayer().getPosition().distanceTo(clickedPos) <= 1 && map.isValidPosition(clickedPos)) {
            if (map.isGameItemBlocking(clickedPos) || map.isEnemyBlocking(clickedPos))
                return null;
            if((gameWorld.getCurrentPlayer().getPosition().getRow() + 1) == clickedPos.getRow())
                return gameWorld.getCurrentPlayer().moveDown();
            else if((gameWorld.getCurrentPlayer().getPosition().getRow() - 1) == clickedPos.getRow())
                return gameWorld.getCurrentPlayer().moveUp();
            else if((gameWorld.getCurrentPlayer().getPosition().getCol() + 1) == clickedPos.getCol())
                return gameWorld.getCurrentPlayer().moveRight();
            else if((gameWorld.getCurrentPlayer().getPosition().getCol() - 1) == clickedPos.getCol())
                return gameWorld.getCurrentPlayer().moveLeft();
        }
        System.out.println("DIDNT MOVE");
        return null;
    }

    // For Keyboard support
    public Position attemptMoveDown(Position inputS) {
        if(gameWorld.getCurrentPlayer().getPosition().distanceTo(inputS) <= 1 && map.isValidPosition(inputS)) {
            if (map.isGameItemBlocking(inputS) || map.isEnemyBlocking(inputS))
                return null;
            if((gameWorld.getCurrentPlayer().getPosition().getRow() + 1) == inputS.getRow())
                return gameWorld.getCurrentPlayer().moveDown();
        }
        return null;
    }

    public Position attemptMoveUp(Position inputW) {
        if(gameWorld.getCurrentPlayer().getPosition().distanceTo(inputW) <= 1 && map.isValidPosition(inputW)) {
            if (map.isGameItemBlocking(inputW) || map.isEnemyBlocking(inputW))
                return null;
            if((gameWorld.getCurrentPlayer().getPosition().getRow() - 1) == inputW.getRow())
                return gameWorld.getCurrentPlayer().moveDown();
        }
        return null;
    }

    public Position attemptMoveRight(Position inputD) {
        if(gameWorld.getCurrentPlayer().getPosition().distanceTo(inputD) <= 1 && map.isValidPosition(inputD)) {
            if (map.isGameItemBlocking(inputD) || map.isEnemyBlocking(inputD))
                return null;
            if((gameWorld.getCurrentPlayer().getPosition().getCol() + 1) == inputD.getCol())
                return gameWorld.getCurrentPlayer().moveRight();
        }
        return null;
    }

    public Position attemptMoveLeft(Position inputA) {
        if(gameWorld.getCurrentPlayer().getPosition().distanceTo(inputA) <= 1 && map.isValidPosition(inputA)) {
            if (map.isGameItemBlocking(inputA) || map.isEnemyBlocking(inputA))
                return null;
            if((gameWorld.getCurrentPlayer().getPosition().getCol() - 1) == inputA.getCol())
                return gameWorld.getCurrentPlayer().moveRight();
        }
        return null;
    }

    @Override
    public boolean onAction(ScreenAction action, Object... data) {
        switch (action) {
            case ScreenAction.MOVE -> {
                System.out.println("PLAYER IS TRYING TO MOVE");
                if(data[0] instanceof Position position && map.isValidPosition(position)) {
                    Position newPosition = attemptMoveTo(position);
                    updateAfterMovingPlayer(newPosition, map);
                    return true;
                }
            }
//            case ScreenAction.MOVEDOWN -> {
//                System.out.println("PLAYER IS TRYING TO MOVE");
//                if(data[0] instanceof Position position && map.isValidPosition(position)) {
//                    Position newPosition = attemptMoveDown(position);
//                    updateAfterMovingPlayer(newPosition, map);
//                }
//            }
//            case ScreenAction.MOVEUP -> {
//                System.out.println("PLAYER IS TRYING TO MOVE");
//                if(data[0] instanceof Position position && map.isValidPosition(position)) {
//                    Position newPosition = attemptMoveUp(position);
//                    updateAfterMovingPlayer(newPosition, map);
//                }
//            }
//            case ScreenAction.MOVERIGHT -> {
//                System.out.println("PLAYER IS TRYING TO MOVE");
//                if(data[0] instanceof Position position && map.isValidPosition(position)) {
//                    Position newPosition = attemptMoveRight(position);
//                    updateAfterMovingPlayer(newPosition, map);
//                }
//            }
//            case ScreenAction.MOVELEFT -> {
//                System.out.println("PLAYER IS TRYING TO MOVE");
//                if(data[0] instanceof Position position && map.isValidPosition(position)) {
//                    Position newPosition = attemptMoveLeft(position);
//                    updateAfterMovingPlayer(newPosition, map);
//                }
//            }
            case ScreenAction.ATTACK -> {
                // Getting player position and attack range
                Enemy target;
                if(data[0] instanceof Enemy enemy)
                    target = enemy;
                else {
                    System.out.println("Tried to fight not an enemy");
                    return false;
                }

                PlayerCharacter player = gameWorld.getCurrentPlayer();
                int range = 1; // default
                if (player instanceof RangedFighter)
                    range = ((RangedFighter) player).getRange();

                if(player.getPosition().distanceTo(enemy.getPosition()) <= range) {
                    // Combat resolution
                    combatSystem.resolveCombat(player, target);
                    // Handle death for map entity
                    if (target.isDead()) {
                        System.out.println("You defeated the " + target.getClass().getSimpleName() + "!");
                        gameWorld.removeEnemy(target);
                    }
                    if(player.isDead()) {
                        gameWorld.removePlayerFromMap(player);
                    }
                    map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
                    this.onAction(ScreenAction.END_TURN, (Object) null);
                    return true; // End turn after successful attack
                }
            }
            case ScreenAction.PICKUP -> {
                Position pos;
                PlayerCharacter player = gameWorld.getCurrentPlayer();
                if(data[0] instanceof Position p)
                    pos = p;
                else
                    return false;
                // ✅ Check for Manhattan distance == 1 (and valid bounds)
                if (!map.isValidPosition(pos)) return false;
                if (player.getPosition().distanceTo(pos) != 1) return false;

                GameItem chosen = map.getEntityGameItemAt(pos);
                if(chosen instanceof Interactable interactable) {
                    gameWorld.removeItem(chosen); // removing from map directly trough game world before changing Position
                    if(interactable instanceof Treasure)
                        interactable.interact(player);
                    else {
                        // changing Position of Interactable to make sure it's usable afterward
                        chosen.setPosition(new Position(-1, -1));
                        player.addToInventory(interactable);
                    }
                    map.updatePlayerView(player.getPosition());
                    this.onAction(ScreenAction.END_TURN, (Object) null);
                    return true;
                }
                return false;
            }
            case ScreenAction.END_TURN -> {
                endTurn = true;
                map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition());
            }
            case ScreenAction.EXIT_GAME -> {
                boolean endGame = true;
                for(PlayerCharacter player : gameWorld.getPlayers()) {
                    if(!player.isDead()) {
                        endGame = false;
                        break;
                    }
                }
                if(gameWorld.getEnemies().isEmpty())
                    endGame = true;

                if(endGame) {
                    GameOverGUI gameOver = new GameOverGUI(gameMapGUI, gameWorld.getPlayers());
                    gameOver.showDialog();
                    gameOver.setVisible(true);  // blocks until closed
                    scanner.close();
                    for(PlayerCharacter p : gameWorld.getPlayers())
                        System.out.println(p.getName() + " - Treasure Points: " + p.getTreasurePoints());
                    System.exit(0);
                }
            }
            default -> System.out.println("Not an option");
        }
        return false;
    }

    private void updateAfterMovingPlayer(Position newPosition, GameMap map) {
        if(newPosition != null) {
            // ✅ Move the player
            System.out.println("PLAYER MOVED SUCCESSFULLY");
            map.removeEntity(gameWorld.getCurrentPlayer()); // Remove from old position
            gameWorld.getCurrentPlayer().setPosition(newPosition);
            map.addEntity(gameWorld.getCurrentPlayer());    // Add to new position
            map.updatePlayerView(gameWorld.getCurrentPlayer().getPosition()); // Updates entities view
            this.onAction(ScreenAction.END_TURN, (Object) null);
        }
    }

    private static int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine();

            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
