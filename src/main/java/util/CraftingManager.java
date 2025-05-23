package util;

import java.util.Map;

/**
 * Utility class providing crafting functionality based on defined recipes and player inventory.
 * <p>
 * Offers methods to check if a recipe can be crafted given the current inventory,
 * and to perform crafting by consuming ingredients and adding the crafted item.
 * </p>
 */
public class CraftingManager {

    /**
     * Determines whether the given recipe can be crafted with the provided inventory.
     * <p>
     * Checks that the inventory contains at least the required amount of each ingredient.
     * </p>
     *
     * @param r   the Recipe defining required ingredients and output
     * @param inv the Inventory to check against
     * @return true if all ingredients are available in sufficient quantity, false otherwise
     */
    public static boolean canCraft(Recipe r, Inventory inv) {
        for (Map.Entry<String, Integer> e : r.ingredients().entrySet()) {
            if (inv.getItems().getOrDefault(e.getKey(), 0) < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs crafting by consuming the required ingredients from the inventory
     * and adding the crafted item to the inventory.
     * <p>
     * Assumes canCraft(r, inv) has returned true; does not recheck ingredient availability.
     * </p>
     *
     * @param r   the Recipe defining which ingredients to consume and which item to produce
     * @param inv the Inventory from which ingredients are removed and to which the output is added
     */
    public static void craft(Recipe r, Inventory inv) {
        for (Map.Entry<String, Integer> e : r.ingredients().entrySet()) {
            inv.removeItem(e.getKey(), e.getValue());
        }
        inv.addItem(r.output(), 1);
    }
}
