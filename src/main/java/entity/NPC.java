package entity;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import util.TileConstants;
import world.World;

public class NPC {
    private static final int FRAME_COUNT = 5;
    private static final double FRAME_DURATION = 0.2; // сек на кадр
    private static final double NPC_SCALE = 3.0;
    private final double npcWidth  = TileConstants.TILE_SIZE * NPC_SCALE;
    private final double npcHeight = TileConstants.TILE_SIZE * NPC_SCALE;

    private final Image[] frames = new Image[FRAME_COUNT];
    private int frameIndex = 0;
    private double frameTimer = 0;

    private final double x, y;
    private final String[] dialog;
    private int dialogLine = 0;
    private boolean inDialog = false;

    public NPC(double x, double y, String[] dialog) {
        this.x = x;
        this.y = y;
        this.dialog = dialog;

        // загрузим 5 кадров BroMonkeyStand1..5.png
        for (int i = 0; i < FRAME_COUNT; i++) {
            String path = String.format("/animation/BroMonkeyStand%d.png", i + 1);
            var is = getClass().getResourceAsStream(path);
            if (is == null) throw new RuntimeException("NPC frame not found: " + path);
            frames[i] = new Image(is);
        }
    }

    /** Вызывать из GameEngine.update(dt) */
    public void update(double dt, World world) {
        // простая «стоячая» анимация
        frameTimer += dt;
        if (frameTimer >= FRAME_DURATION) {
            frameTimer -= FRAME_DURATION;
            frameIndex = (frameIndex + 1) % FRAME_COUNT;
        }
    }

    /** Вызывать при нажатии E */
    public void interact(double playerX, double playerY) {
        // проверим, близко ли подошёл
        double dx = playerX - x;
        double dy = playerY - y;
        double dist = Math.hypot(dx, dy);
        if (dist < TileConstants.TILE_SIZE * 1.5) {
            if (!inDialog) {
                inDialog = true;
                dialogLine = 0;
            } else {
                dialogLine++;
                if (dialogLine >= dialog.length) {
                    inDialog = false;  // закончился диалог
                }
            }
        }
    }

    /** true, если сейчас показываем окно диалога */
    public boolean isInDialog() {
        return inDialog;
    }

    /** возвращает текущую строку, не выходя за предел */
    public String currentDialogLine() {
        if (!inDialog) return "";
        return dialog[Math.min(dialogLine, dialog.length - 1)];
    }

    /** Отрисовать NPC */
    public void render(GraphicsContext gc, Camera cam) {
        double sx = x - cam.getWorldX();
        double sy = y - cam.getWorldY();
        // если хотим, чтобы он «рос вверх» от земли, сдвинем py на разницу высот:
        double dyOffset = TileConstants.TILE_SIZE - npcHeight;
        gc.drawImage(frames[frameIndex],
                sx, sy + dyOffset,
                npcWidth, npcHeight);
    }
}
