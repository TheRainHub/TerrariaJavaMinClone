package util;

import java.util.Map;

/**
 * Описывает один рецепт крафта: что и из каких ингредиентов делается.
 */
public class Recipe {
    private final String output;              // id результирующего предмета
    private final Map<String,Integer> ingredients;  // map<id, количество>

    public Recipe(String output, Map<String,Integer> ingredients) {
        this.output = output;
        this.ingredients = ingredients;
    }

    /** Идентификатор (название) получаемого предмета */
    public String output() {
        return output;
    }

    /** Ингредиенты и их количества */
    public Map<String,Integer> ingredients() {
        return ingredients;
    }

    /** Для отображения в UI, например "sword (iron x3, stick x1)" */
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
