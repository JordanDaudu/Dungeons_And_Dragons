package game.engine;

public interface ScreenListener {
    boolean onAction(ScreenAction action, Object... data);
}
