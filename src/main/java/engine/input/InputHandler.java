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
 * Обрабатывает ввод пользователя: клавиши, мышь.
 */
public class InputHandler {
    private final Player player;
    private final LevelManager lvlMgr;
    private final UIManager uiMgr;
    private final List<NPC> npcs;

    public InputHandler(Player player,
                        LevelManager lvlMgr,
                        UIManager uiMgr,
                        List<NPC> npcs) {
        this.player = player;
        this.lvlMgr  = lvlMgr;
        this.uiMgr   = uiMgr;
        this.npcs    = npcs;
    }

    /** Обработка нажатия клавиш. */
    public void handleKeyPress(KeyEvent e) {
        // 1) Крафт-меню
        if (uiMgr.isCraftingOpen()) {
            uiMgr.handleCraftingInput(e);
            return;
        }
        // 2) Пауза
        if (uiMgr.isPaused()) {
            uiMgr.handlePauseInput(e);
            return;
        }


        // 3) Общие клавиши
        switch (e.getCode()) {
            case C -> {
                uiMgr.toggleCrafting();
            }

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

            case A, LEFT -> player.moveLeft();
            case D, RIGHT -> player.moveRight();
            case W, UP, SPACE -> player.jump();

            case P -> uiMgr.togglePause();
            default -> { /*noop*/ }
        }
    }

    /** Обработка отпускания клавиш. */
    public void handleKeyRelease(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT  -> player.stopMovingLeft();
            case D, RIGHT -> player.stopMovingRight();
            default -> { /*noop*/ }
        }
    }

    /** Обработка нажатия мыши: майнинг/плейсинг. */
    public void handleMousePress(MouseEvent e) {
        int tx = (int)((lvlMgr.getCamera().getWorldX() + e.getX()) / lvlMgr.getTileSize());
        int ty = (int)((lvlMgr.getCamera().getWorldY() + e.getY()) / lvlMgr.getTileSize());

        if (e.isPrimaryButtonDown()) {
            lvlMgr.mineTile(tx, ty);
        } else if (e.isSecondaryButtonDown()) {
            lvlMgr.placeTile(tx, ty, TileType.DIRT);
        }
    }

    /** Обработка отпускания кнопок мыши (опционально). */
    public void handleMouseRelease(MouseEvent e) {
        // Не используется
    }
}
