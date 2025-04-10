package game.core;

import game.items.GameItem;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<GameItem> items;

    public Inventory() {

        items = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().getSimpleName()).append("{");

        if (items.isEmpty()) {
            result.append("no items");
        } else {
            result.append("items = ").append("[");
            for (int i = 0; i < items.size(); i++) {
                result.append(items.get(i).toString());
                if (i < items.size() - 1) {
                    result.append(", ");
                }
            }
            result.append("]");
        }

        result.append("}");
        return result.toString();
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Inventory inventory = (Inventory) obj;
        return items.equals(inventory.items);
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
