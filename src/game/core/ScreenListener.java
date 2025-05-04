package game.core;

public interface ScreenListener {
    boolean onAction(ScreenAction action, Object... data);
}
