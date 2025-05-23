// src/test/java/tests/InventoryTest.java
package tests;

import org.junit.jupiter.api.Test;
import util.Inventory;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    @Test
    void testAddNewItem() {
        Inventory inv = new Inventory();
        assertTrue(inv.getItems().isEmpty(), "Новый инвентарь пуст");
        inv.addItem("apple", 3);
        assertEquals(3, inv.getItems().get("apple"));
    }

    @Test
    void testAddSameItemAccumulates() {
        Inventory inv = new Inventory();
        inv.addItem("stone", 1);
        inv.addItem("stone", 4);
        assertEquals(5, inv.getItems().get("stone"),
                "При повторном добавлении счётчик увеличивается");
    }
}
