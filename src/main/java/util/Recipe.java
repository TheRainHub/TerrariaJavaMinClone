package util;

import java.util.Map;

/**
 * Represents a crafting recipe, defining the output item and required ingredients.
 * <p>
 * Each recipe maps ingredient IDs to quantities needed to produce the output.
 * </p>
 */
public class Recipe {
    private final String output;                    // ID of the resulting item
    private final Map<String, Integer> ingredients; // Map of ingredient ID to quantity

    /**
     * Constructs a new Recipe with specified output and ingredients.
     *
     * @param output      the item ID produced by this recipe
     * @param ingredients map of item IDs to required quantities
     */
    public Recipe(String output, Map<String, Integer> ingredients) {
        this.output = output;
        this.ingredients = Map.copyOf(ingredients);
    }

    /**
     * Returns the ID of the item produced by this recipe.
     *
     * @return output item ID
     */
    public String output() {
        return output;
    }

    /**
     * Returns the required ingredients and their quantities.
     *
     * @return unmodifiable map of ingredient IDs to quantities
     */
    public Map<String, Integer> ingredients() {
        return ingredients;
    }

    /**
     * Returns a string representation for UI display,
     * e.g. "sword (iron x3, stick x1)".
     *
     * @return formatted recipe string
     */
    @Override
    public String toString() {
        var sb = new StringBuilder(output).append(" (");
        boolean first = true;
        for (var e : ingredients.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(e.getKey()).append(" x").append(e.getValue());
            first = false;
        }
        return sb.append(")").toString();
    }
}
