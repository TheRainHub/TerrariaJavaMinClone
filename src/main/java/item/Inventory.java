package item;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple inventory that tracks unique collected items by their IDs.
 * <p>
 * Internally uses a {@link HashSet} to ensure each item ID appears at most once.
 * </p>
 */
public class Inventory {
    /**
     * The set of item IDs that have been collected.
     */
    private Set<String> items = new HashSet<>();

    /**
     * Adds the specified item ID to the inventory.
     * <p>
     * If the item is already present, this method does nothing.
     * </p>
     *
     * @param item the unique identifier of the item to add
     */
    public void add(String item) {
        items.add(item);
    }

    /**
     * Checks if the inventory contains the specified item ID.
     *
     * @param item the unique identifier of the item to check
     * @return {@code true} if the item is in the inventory, {@code false} otherwise
     */
    public boolean has(String item) {
        return items.contains(item);
    }
}
