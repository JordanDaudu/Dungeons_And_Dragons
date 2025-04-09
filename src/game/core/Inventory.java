package game.core;

import game.items.GameItem;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<GameItem> items;

    public Inventory() {

        items = new ArrayList<>();
    }

    public boolean addItem(GameItem item) {

        return items.add(item);
    }

    public boolean removeItem(GameItem item) {

        return items.remove(item);
    }

    public List<GameItem> getItems() {
        return items;
    }
}
