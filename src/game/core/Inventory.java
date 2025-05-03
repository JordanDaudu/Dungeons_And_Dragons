package game.core;

import game.items.Interactable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the inventory of a player character, containing various game items.
 */
public class Inventory {

    // Data Members
    private final List<Interactable> items;

    // Methods
    /**
     * Constructs an empty inventory.
     */
    public Inventory() {

        items = new ArrayList<>();
    }

    /**
     * Returns a string representation of the inventory, including all items.
     *
     * @return a formatted string of the inventory's contents.
     */
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

    /**
     * Checks whether this inventory is equal to another object based on the items list.
     *
     * @param obj the object to compare to.
     * @return true if the inventories contain the same items; false otherwise.
     */
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

    /**
     * Adds an item to the inventory.
     *
     * @param item the item to be added.
     * @return true if the item was successfully added.
     */
    public boolean addItem(Interactable item) {

        return items.add(item);
    }

    /**
     * Removes an item from the inventory.
     *
     * @param item the item to be removed.
     * @return true if the item was successfully removed.
     */
    public boolean removeItem(Interactable item) {

        return items.remove(item);
    }

    /**
     * Gets the list of items currently in the inventory.
     *
     * @return a list of items in the inventory.
     */
    public List<Interactable> getItems() {
        return items;
    }

    /**
     * Prints a summarized view of the inventory,
     * showing the count of each type of item.
     */
    public void printInventory() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        Map<String, Integer> itemCounts = new HashMap<>();

        for (Interactable item : items) {
            String itemName = item.getClass().getSimpleName();
            itemCounts.put(itemName, itemCounts.getOrDefault(itemName, 0) + 1);
        }

        System.out.println("Inventory:");
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet())
            System.out.println("- " + entry.getKey() + " x" + entry.getValue());
    }
}
