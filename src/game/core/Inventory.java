package game.core;

import game.items.GameItem;

import java.util.ArrayList;

public class Inventory {

    private ArrayList<GameItem> items;

    public Inventory() {

        items = new ArrayList<>();
    }

    public boolean addItem(GameItem item) {

        return items.add(item);
    }

    public boolean removeItem(GameItem item) {

        return items.remove(item);
    }
}
