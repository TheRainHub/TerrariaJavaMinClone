package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Загружает рецепты из текстового файла вида:
 *   baton=stick:3
 *   sword=iron:3,stick:1
 *   arrow=stick:1,stone:1
 */
public class RecipeLoader {
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
        } catch (IOException|NullPointerException e) {
            throw new RuntimeException("Cannot load recipes from " + resourcePath, e);
        }
        return recipes;
    }
}
