package engine.ui;

import entity.NPC;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import util.CraftingManager;
import util.Inventory;
import util.Recipe;
import engine.save.SaveLoadManager;
import java.util.List;

/**
 * Отвечает за отрисовку и обработку UI:
 * - инвентарь
 * - диалоги
 * - меню паузы
 * - окно крафта
 */
public class UIManager {
    private final Inventory inventory;
    private final List<Recipe> recipes;
    private final List<NPC> npcs;

    private boolean paused = false;
    private boolean craftingOpen = false;
    private int pauseIndex = 0;
    private int craftIndex = 0;
    private final SaveLoadManager saveMgr;
    private boolean gameWon = false;
    private final List<String> pauseOptions = List.of("Resume","Save", "Save & Quit", "Exit");

    public UIManager(Inventory inventory,
                     List<Recipe> recipes,
                     List<NPC> npcs,
                     SaveLoadManager saveMgr) {
        this.inventory  = inventory;
        this.recipes    = recipes;
        this.npcs       = npcs;
        this.saveMgr    = saveMgr;
    }

    /** Основной метод отрисовки UI, вызывается после рендера мира. */
    public void renderUI(GraphicsContext gc) {
        if (gameWon) {
            renderWinScreen(gc);
            return;
        }

        if (paused) {
            renderPauseMenu(gc);
            return;
        }
        renderInventory(gc);
        renderDialog(gc);
        if (craftingOpen) renderCraftingMenu(gc);
        if (paused)      renderPauseMenu(gc);
    }

    /** Рисует текущее содержимое инвентаря в левом верхнем углу. */
    private void renderInventory(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        int y = 20;
        for (var e : inventory.getItems().entrySet()) {
            gc.fillText(e.getKey() + " x" + e.getValue(), 10, y);
            y += 20;
        }
    }

    /** Если какой-то NPC в диалоге, рисует диалоговое окно. */
    private void renderDialog(GraphicsContext gc) {
        for (NPC npc : npcs) {
            if (npc.isInDialog()) {
                String text = npc.currentDialogLine();
                double boxW = 0.8 * gc.getCanvas().getWidth();
                double boxH = 80;
                double x = (gc.getCanvas().getWidth() - boxW) / 2;
                double y = gc.getCanvas().getHeight() - boxH - 20;

                gc.setGlobalAlpha(0.75);
                gc.setFill(Color.BLACK);
                gc.fillRoundRect(x, y, boxW, boxH, 10, 10);
                gc.setGlobalAlpha(1.0);

                gc.setStroke(Color.WHITE);
                gc.strokeRoundRect(x, y, boxW, boxH, 10, 10);

                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
                gc.fillText(text, x + 20, y + 30);
                break;
            }
        }
    }

    /** Отрисовка полупрозрачного меню паузы. */
    private void renderPauseMenu(GraphicsContext gc) {

        double w = gc.getCanvas().getWidth();
        double h = gc.getCanvas().getHeight();

        // фон
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, w, h);

        double boxW = 200;
        double boxH = pauseOptions.size() * 30 + 20;
        double bx = (w - boxW) / 2;
        double by = (h - boxH) / 2;

        gc.setFill(Color.rgb(20, 20, 20, 0.8));
        gc.fillRoundRect(bx, by, boxW, boxH, 10, 10);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(bx, by, boxW, boxH, 10, 10);

        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        for (int i = 0; i < pauseOptions.size(); i++) {
            gc.setFill(i == pauseIndex ? Color.YELLOW : Color.WHITE);
            gc.fillText(pauseOptions.get(i), bx + 20, by + 30 + i * 30);
        }
    }

    /** Отрисовка окна крафта. */
    private void renderCraftingMenu(GraphicsContext gc) {
        double w = gc.getCanvas().getWidth();
        double bx = 10;
        double by = 100;
        double bw = 250;
        double bh = recipes.size() * 22 + 20;

        gc.setFill(Color.rgb(30, 30, 30, 0.8));
        gc.fillRoundRect(bx, by, bw, bh, 8, 8);
        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(bx, by, bw, bh, 8, 8);

        gc.setFont(Font.font("Consolas", 14));
        for (int i = 0; i < recipes.size(); i++) {
            var r = recipes.get(i);
            gc.setFill(i == craftIndex ? Color.YELLOW : Color.WHITE);
            gc.fillText(r.output(), bx + 10, by + 20 + i * 22);
        }

        // ингредиенты выбранного рецепта
        var cur = recipes.get(craftIndex);
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("Need:", bx + bw + 10, by + 20);
        int yy = (int)(by + 40);
        for (var ingr : cur.ingredients().entrySet()) {
            gc.fillText(ingr.getKey() + " x" + ingr.getValue(), bx + bw + 10, yy);
            yy += 20;
        }
    }

    // --- Методы обработки ввода для меню паузы и крафта ---
    public void handlePauseInput(KeyEvent e) {
        switch (e.getCode()) {
            case UP:
            case W:
                pauseIndex = (pauseIndex + pauseOptions.size() - 1) % pauseOptions.size();
                break;
            case DOWN:
            case S:
                pauseIndex = (pauseIndex + 1) % pauseOptions.size();
                break;
            case ENTER:
                Recipe rec = recipes.get(craftIndex);
                if (CraftingManager.canCraft(rec, inventory)) {
                    CraftingManager.craft(rec, inventory);
                    // если собрал корону — победа
                    if (rec.output().equalsIgnoreCase("crown")) {
                        gameWon = true;
                    }
                }
                String opt = pauseOptions.get(pauseIndex);
                switch (opt) {
                    case "Resume" -> {
                        paused = false;
                    }
                    case "Save" ->{
                        paused = true;
                        saveMgr.saveAll();
                        System.out.println("Game saved");
                    }
                    case "Save & Quit" -> {
                        saveMgr.saveAll();
                        System.out.println("Game saved, exiting…");
                        Platform.exit();
                    }
                    case "Exit" -> {
                        Platform.exit();
                    }
                    default -> { /* ничего */ }
                }
                break;
            default:
                break;
        }
    }

    public void handleCraftingInput(KeyEvent e) {
        switch (e.getCode()) {
            case UP, W ->
                    craftIndex = (craftIndex + recipes.size() - 1) % recipes.size();
            case DOWN, S ->
                    craftIndex = (craftIndex + 1) % recipes.size();
            case ENTER -> {
                var rec = recipes.get(craftIndex);
                if (CraftingManager.canCraft(rec, inventory)) {
                    CraftingManager.craft(rec, inventory);
                    if (rec.output().equalsIgnoreCase("crown")) {
                        onWin();
                    }
                }
            }
            case C -> craftingOpen = false;
            default -> {}
        }
    }

    private void onWin() {
        gameWon = true;
        // через 5 секунд очистить файлы и закрыть
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(evt -> {
            saveMgr.clearAll();
            Platform.exit();
        });
        delay.play();
    }

    private void renderWinScreen(GraphicsContext gc) {
        double width  = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();

        // затемняем фон
        gc.setFill(Color.rgb(0,0,0,0.75));
        gc.fillRect(0,0,width,height);

        String msg = "YOU WON!";
        Font font = Font.font("Consolas", FontWeight.BOLD, 72);
        gc.setFont(font);
        gc.setFill(Color.GOLD);

        // центрируем текст
        Text t = new Text(msg);
        t.setFont(font);
        double tw = t.getLayoutBounds().getWidth();
        double tx = (width - tw) / 2;
        double ty = height / 2;

        gc.fillText(msg, tx, ty);
    }

    // Тоггл чтобы включить/выключить меню
    public void togglePause()    { paused        = !paused; }
    public void toggleCrafting() { craftingOpen  = !craftingOpen; }

    // Геттеры
    public boolean isPaused()        { return paused; }
    public boolean isCraftingOpen()  { return craftingOpen; }
    public Inventory getInventory()  { return inventory; }
}
