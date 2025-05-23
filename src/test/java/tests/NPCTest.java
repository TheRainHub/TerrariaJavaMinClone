// src/test/java/tests/NPCTest.java
package tests;

import entity.NPC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class NPCTest {

    @Test
    void testInteractStartsDialog() {
        String[] dialog = { "Hello", "World" };
        NPC npc = new NPC(50, 50, dialog);

        // dialog should be inactive before interaction
        assertFalse(npc.isInDialog());

        // player is nearby (passing coordinates close to the NPC)
        npc.interact(52, 48);
        assertTrue(npc.isInDialog(), "Dialog should start after interact()");
        assertEquals("Hello", npc.currentDialogLine());
    }

    @Test
    void testDialogAdvancesOnInteract() {
        String[] dialog = { "Line1", "Line2" };
        NPC npc = new NPC(0, 0, dialog);

        npc.interact(1, 1); // start dialog
        assertEquals("Line1", npc.currentDialogLine());

        // pressing E again — advance to next line
        npc.interact(1, 1);
        assertEquals("Line2", npc.currentDialogLine());

        // once more — dialog should end
        npc.interact(1, 1);
        assertFalse(npc.isInDialog(), "Dialog should end after the last line");
    }
}
