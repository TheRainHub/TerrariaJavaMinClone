package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the player's inventory of items, supporting load/save operations
 * and item management (add/remove).
 */
public class Inventory {
    private final Map<String, Integer> items = new LinkedHashMap<>();

    /**
     * Loads inventory data from a text file with entries in key=value format.
     * <p>
     * Clears existing items, then reads each non-empty, non-comment line,
     * parsing item ID and quantity.
     * </p>
     *
     * @param filename path to the inventory file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public void loadFromFile(String filename) throws IOException {
        items.clear();
        Path p = Paths.get(filename);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String id = parts[0].trim();
                int qty = Integer.parseInt(parts[1].trim());
                items.put(id, qty);
            }
        }
    }

    /**
     * Saves the current inventory state to a text file in key=value format.
     * <p>
     * Overwrites any existing file content.
     * </p>
     *
     * @param filename path to the inventory file
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void saveToFile(String filename) throws IOException {
        Path p = Paths.get(filename);
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (var e : items.entrySet()) {
                pw.println(e.getKey() + "=" + e.getValue());
            }
        }
    }

    /**
     * Returns an unmodifiable view of the inventory items and their quantities.
     *
     * @return map of item ID to quantity
     */
    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Adds the specified count of an item to the inventory.
     * <p>
     * Creates a new entry if the item did not exist.
     * </p>
     *
     * @param id    the item identifier
     * @param count the number of items to add (must be positive)
     */
    public void addItem(String id, int count) {
        items.merge(id, count, Integer::sum);
    }

    /**
     * Removes the specified count of an item from the inventory.
     * <p>
     * If the count matches the current quantity, the item is removed entirely.
     * </p>
     *
     * @param id    the item identifier
     * @param count the number of items to remove (must be positive)
     * @return true if removal succeeded, false if insufficient quantity or item absent
     */
    public boolean removeItem(String id, int count) {
        Integer cur = items.get(id);
        if (cur == null || cur < count) return false;
        if (cur.equals(count)) items.remove(id);
        else items.put(id, cur - count);
        return true;
    }
}
