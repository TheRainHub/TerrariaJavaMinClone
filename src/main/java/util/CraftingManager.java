package util;

import java.util.Map;

public class CraftingManager {
    /** Проверяет, можно ли скрафтить такой рецепт */
    public static boolean canCraft(Recipe r, Inventory inv) {
        for (var e : r.ingredients().entrySet()) {
            if (inv.getItems().getOrDefault(e.getKey(), 0) < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    /** Списывает ингредиенты и добавляет готовый предмет в инвентарь */
    public static void craft(Recipe r, Inventory inv) {
        for (var e : r.ingredients().entrySet()) {
            inv.removeItem(e.getKey(), e.getValue());
        }
        inv.addItem(r.output(), 1);
    }
}
