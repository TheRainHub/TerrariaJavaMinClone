package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Loads crafting recipes from a resource file with lines in the format:
 * <pre>
 *   baton=stick:3
 *   sword=iron:3,stick:1
 *   arrow=stick:1,stone:1
 * </pre>
 */
public class RecipeLoader {

    /**
     * Reads a list of {@link Recipe} objects from the specified resource path.
     * <p>
     * Each non-empty, non-comment line in the file should follow "output=ing1:qty1,ing2:qty2" format.
     * Lines starting with '#' are ignored as comments.
     * </p>
     *
     * @param resourcePath the path to the recipe file in the classpath (e.g., "/recipes.txt")
     * @return a List of Recipe instances parsed from the file
     * @throws RuntimeException if the resource cannot be found or an I/O error occurs during loading
     */
    public static List<Recipe> loadRecipes(String resourcePath) {
        List<Recipe> recipes = new ArrayList<>();
        try (InputStream is = RecipeLoader.class.getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                String output = parts[0].trim();
                Map<String,Integer> ingredients = new LinkedHashMap<>();
                for (String tok : parts[1].split(",")) {
                    String[] kv = tok.split(":", 2);
                    ingredients.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
                }
                recipes.add(new Recipe(output, ingredients));
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Cannot load recipes from " + resourcePath, e);
        }
        return recipes;
    }
}
