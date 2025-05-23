package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.CraftingManager;
import util.Inventory;
import util.Recipe;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CraftingManagerTest {

    private static Inventory inventory;

    @BeforeAll
    static void setup() {
        inventory = new Inventory();
        // assuming Inventory has an addItem method
        inventory.addItem("wood", 5);
        inventory.addItem("stone", 3);
    }

    @Test
    void testCanCraftTrue() {
        // recipe: 3 wood + 2 stone -> pickaxe
        Recipe rec = new Recipe("pickaxe", Map.of("wood", 3, "stone", 2));
        assertTrue(CraftingManager.canCraft(rec, inventory),
                "The inventory has enough items, canCraft should return true");
    }

    @Test
    void testCanCraftFalse() {
        // recipe too expensive
        Recipe rec = new Recipe("castle", Map.of("wood", 10, "stone", 10));
        assertFalse(CraftingManager.canCraft(rec, inventory),
                "The inventory is insufficient, canCraft should return false");
    }

    @Test
    void testCraftReducesMaterialsAndAddsOutput() {
        Recipe rec = new Recipe("ladder", Map.of("wood", 2));
        int beforeWood = inventory.getItems().getOrDefault("wood", 0);
        assertTrue(CraftingManager.canCraft(rec, inventory));

        CraftingManager.craft(rec, inventory);

        int afterWood = inventory.getItems().getOrDefault("wood", 0);
        int ladders = inventory.getItems().getOrDefault("ladder", 0);

        assertEquals(beforeWood - 2, afterWood,
                "Wood should decrease by 2 after crafting");
        assertEquals(1, ladders,
                "There should be 1 ladder after crafting");
    }
}
