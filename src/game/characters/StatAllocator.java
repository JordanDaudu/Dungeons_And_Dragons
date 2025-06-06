package game.characters;

import java.util.HashMap;
import java.util.Map;

public class StatAllocator {
    private final Map<String, Integer> baseStats;
    private final Map<String, Integer> currentStats;
    private final int maxUp = 3;
    private final int maxDown = -2;

    public StatAllocator(Map<String, Integer> baseStats) {
        this.baseStats = baseStats;
        this.currentStats = new HashMap<>(baseStats);
    }

    public boolean set(String stat, int newValue) {
        if (!baseStats.containsKey(stat)) return false;

        int base = baseStats.get(stat);
        int delta = newValue - base;

        if (delta > maxUp || delta < maxDown) return false;

        int totalShift = getTotalShiftWith(stat, newValue);
        if (totalShift != 0) return false; // must be balanced (e.g., +1, -1)

        currentStats.put(stat, newValue);
        return true;
    }

    private int getTotalShiftWith(String stat, int newValue) {
        int total = 0;
        for (String key : currentStats.keySet()) {
            int base = baseStats.get(key);
            int val = key.equals(stat) ? newValue : currentStats.get(key);
            total += (val - base);
        }
        return total;
    }

    public int get(String stat) {
        return currentStats.getOrDefault(stat, 0);
    }

    public boolean isValid() {
        int totalShift = 0;
        for (String key : currentStats.keySet()) {
            int base = baseStats.get(key);
            int delta = currentStats.get(key) - base;
            if (delta > maxUp || delta < maxDown) return false;
            totalShift += delta;
        }
        return totalShift == 0;
    }
}
