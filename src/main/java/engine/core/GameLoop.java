package engine.core;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import engine.input.InputHandler;
import engine.level.LevelManager;
import engine.ui.UIManager;
import entity.Player;
import entity.NPC;
import entity.ItemEntity;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {
    private long lastTime = 0;

    private final GraphicsContext gc;
    private final Scene scene;
    private final LevelManager lvlMgr;
    private final UIManager uiMgr;
    private final InputHandler input;
    private final Player player;
    private final List<NPC> npcs;
    private final List<ItemEntity> items;

    // Добавляем эти поля:
    private final int width;
    private final int height;
    private final Image backgroundImage;
    private boolean gameWon = false;
    /**
     * Теперь в конструктор передаём ширину/высоту холста и фон.
     */
    public GameLoop(GraphicsContext gc,
                    Scene scene,
                    int width,
                    int height,
                    LevelManager lvlMgr,
                    UIManager uiMgr,
                    InputHandler input,
                    Player player,
                    Image backgroundImage) {
        this.gc              = gc;
        this.scene           = scene;
        this.width           = width;
        this.height          = height;
        this.backgroundImage = backgroundImage;

        this.lvlMgr  = lvlMgr;
        this.uiMgr   = uiMgr;
        this.input   = input;
        this.player  = player;
        this.npcs    = lvlMgr.getNpcs();
        this.items   = lvlMgr.getItems();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                uiMgr.togglePause();
            } else if (uiMgr.isPaused()) {
                uiMgr.handlePauseInput(e);
            } else {
                input.handleKeyPress(e);
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, input::handleKeyRelease);
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, input::handleMousePress);
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, input::handleMouseRelease);
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }
        double dt = (now - lastTime) / 1e9;
        lastTime = now;

        update(dt);
        render();
    }

    private void update(double dt) {
        if (gameWon) return;

        if (uiMgr.isPaused() || uiMgr.isCraftingOpen()) {
            return;
        }
        player.update(dt, lvlMgr.getWorld());
        lvlMgr.getCamera().centerOn(player.getX(), player.getY());

        Iterator<ItemEntity> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().update(player, lvlMgr.getWorld())) {
                it.remove();
            }
        }
        for (NPC npc : npcs) {
            npc.update(dt, lvlMgr.getWorld());
        }
        lvlMgr.checkTransitions(player);
    }

    public Scene getScene() {
        return scene;
    }

    private void render() {
        // 1) Очищаем холст
        gc.setGlobalAlpha(1.0);
        gc.clearRect(0, 0, width, height);

        if (gameWon) {
            gc.setGlobalAlpha(1.0);
            // полупрозрачный фон
            gc.setFill(Color.rgb(0, 0, 0, 0.75));
            gc.fillRect(0, 0, width, height);

            // крупный золотой текст
            String msg = "YOU WIN!";
            Font winFont = Font.font("Consolas", FontWeight.BOLD, 72);
            gc.setFont(winFont);
            gc.setFill(Color.GOLD);

            Text helper = new Text(msg);
            helper.setFont(winFont);
            double textWidth = helper.getLayoutBounds().getWidth();

            double x = (width - textWidth) / 2;
            double y = height / 2;
            gc.fillText(msg, x, y);

            return;

        }
        // 2) Рисуем фон/parallax
        if (backgroundImage != null) {
            double p = 0.2;
            gc.drawImage(
                    backgroundImage,
                    -lvlMgr.getCamera().getWorldX() * p,
                    -lvlMgr.getCamera().getWorldY() * p,
                    width, height
            );
        } else {
            gc.setFill(Color.CORNFLOWERBLUE);
            gc.fillRect(0, 0, width, height);
        }

        // 3) Рисуем мир, предметы, NPC, игрока
        lvlMgr.renderWorld(gc);
        for (ItemEntity item : items) item.render(gc, lvlMgr.getCamera());
        for (NPC npc : npcs)         npc.render(gc, lvlMgr.getCamera());
        player.render(gc, lvlMgr.getCamera());

        // 4) Рисуем UI поверх
        uiMgr.renderUI(gc);
    }
}
