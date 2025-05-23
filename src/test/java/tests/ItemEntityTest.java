// src/test/java/tests/ItemEntityTest.java
package tests;

import entity.ItemEntity;
import entity.Player;
import util.Inventory;
import world.World;
import world.TileType;
import util.TileConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemEntityTest {

    private static World emptyWorld;

    @BeforeAll
    static void initWorld() {
        // stub world with no solid tiles
        TileType[][] tiles = new TileType[1][1];
        tiles[0][0] = TileType.AIR;
        emptyWorld = new World(tiles);
    }

    @Test
    void testPickupWhenColliding() {
        Inventory inv = new Inventory();
        ItemEntity item = new ItemEntity(world.ItemType.BATON, inv, 10, 10);

        Player player = new Player(10, 10);

        assertEquals(0, inv.getItems().getOrDefault("baton", 0));

        boolean picked = item.update(player, emptyWorld);

        assertTrue(picked, "update() should return true when picked up");
        assertEquals(1, inv.getItems().get("baton"),
                "Inventory should contain one 'baton' after pickup");
    }

    @Test
    void testNoPickupWhenFar() {
        Inventory inv = new Inventory();
        ItemEntity item = new ItemEntity(world.ItemType.BATON, inv, 100, 100);

        // player is far from the item
        Player player = new Player(0, 0);

        boolean picked = item.update(player, emptyWorld);

        assertFalse(picked, "update() should return false when too far");
        assertFalse(inv.getItems().containsKey("baton"),
                "Inventory should still not contain the baton");
    }
}
