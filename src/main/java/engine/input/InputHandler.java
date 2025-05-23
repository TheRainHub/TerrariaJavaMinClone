package engine.input;

import engine.level.LevelManager;
import engine.save.SaveLoadManager;
import engine.ui.UIManager;
import entity.Player;
import entity.NPC;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import world.ItemType;
import world.TileType;

import java.util.List;

/**
 * Handles user input events: keyboard and mouse interactions.
 * <p>
 * Delegates input to the UI when craft or pause menus are active,
 * otherwise translates events into game actions such as movement,
 * interaction, mining, and placing tiles.
 * </p>
 */
public class InputHandler {
    private final Player player;
    private final LevelManager lvlMgr;
    private final UIManager uiMgr;
    private final List<NPC> npcs;

    /**
     * Constructs an InputHandler to process user events.
     *
     * @param player the player entity to control
     * @param lvlMgr the level manager for world interactions
     * @param uiMgr  the UI manager to handle menu states
     * @param npcs   the list of NPCs for interaction events
     */
    public InputHandler(Player player,
                        LevelManager lvlMgr,
                        UIManager uiMgr,
                        List<NPC> npcs) {
        this.player = player;
        this.lvlMgr  = lvlMgr;
        this.uiMgr   = uiMgr;
        this.npcs    = npcs;
    }

    /**
     * Processes key press events.
     * <p>
     * If the crafting menu is open, forwards the event to the crafting UI.
     * If the game is paused, forwards the event to the pause UI.
     * Otherwise handles movement, actions, and UI toggles.
     * </p>
     *
     * @param e the KeyEvent representing the key press
     */
    public void handleKeyPress(KeyEvent e) {
        // 1) Crafting menu input
        if (uiMgr.isCraftingOpen()) {
            uiMgr.handleCraftingInput(e);
            return;
        }
        // 2) Pause menu input
        if (uiMgr.isPaused()) {
            uiMgr.handlePauseInput(e);
            return;
        }

        // 3) General key actions
        switch (e.getCode()) {
            case C -> uiMgr.toggleCrafting();

            case DIGIT1 -> {
                int count = uiMgr.getInventory().getItems().getOrDefault("baton", 0);
                if (count > 0) {
                    player.setEquippedItem(ItemType.BATON);
                }
            }

            case E -> {
                double cx = player.getX() + Player.PLAYER_WIDTH  / 2.0;
                double cy = player.getY() + Player.PLAYER_HEIGHT / 2.0;
                npcs.forEach(n -> n.interact(cx, cy));
            }

            case A, LEFT  -> player.moveLeft();
            case D, RIGHT -> player.moveRight();
            case W, UP, SPACE -> player.jump();

            case P -> uiMgr.togglePause();
            default -> {
                // No operation for other keys
            }
        }
    }

    /**
     * Processes key release events for movement keys.
     * <p>
     * Stops player movement when left or right keys are released.
     * </p>
     *
     * @param e the KeyEvent representing the key release
     */
    public void handleKeyRelease(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT  -> player.stopMovingLeft();
            case D, RIGHT -> player.stopMovingRight();
            default -> {
                // No operation for other keys
            }
        }
    }

    /**
     * Processes mouse press events for mining and placing tiles.
     * <p>
     * Left-click mines a tile at the cursor position.
     * Right-click places a dirt tile at the cursor position.
     * Coordinates are translated from screen to world space.
     * </p>
     *
     * @param e the MouseEvent representing the mouse button press
     */
    public void handleMousePress(MouseEvent e) {
        int tx = (int) ((lvlMgr.getCamera().getWorldX() + e.getX()) / lvlMgr.getTileSize());
        int ty = (int) ((lvlMgr.getCamera().getWorldY() + e.getY()) / lvlMgr.getTileSize());

        if (e.isPrimaryButtonDown()) {
            lvlMgr.mineTile(tx, ty);
        } else if (e.isSecondaryButtonDown()) {
            lvlMgr.placeTile(tx, ty, TileType.DIRT);
        }
    }

    /**
     * Processes mouse release events.
     * <p>
     * Currently not used, but provided for completeness and future extensions.
     * </p>
     *
     * @param e the MouseEvent representing the mouse button release
     */
    public void handleMouseRelease(MouseEvent e) {
        // Not used
    }
}
