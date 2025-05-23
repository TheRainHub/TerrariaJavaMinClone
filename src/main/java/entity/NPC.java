package entity;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import util.TileConstants;
import world.World;

/**
 * Represents a non-player character (NPC) in the game world.
 * <p>
 * NPCs display a simple idle animation and can engage in dialogue
 * when the player interacts within range.
 * </p>
 */
public class NPC {
    private static final int FRAME_COUNT = 5;
    private static final double FRAME_DURATION = 0.3; // seconds per frame
    private static final double NPC_SCALE = 3.0;

    private final double npcWidth  = TileConstants.TILE_SIZE * NPC_SCALE;
    private final double npcHeight = TileConstants.TILE_SIZE * NPC_SCALE;

    private final Image[] frames = new Image[FRAME_COUNT];
    private int frameIndex = 0;
    private double frameTimer = 0;

    private final double x;
    private final double y;
    private final String[] dialog;
    private int dialogLine = 0;
    private boolean inDialog = false;

    /**
     * Loads NPC frames and initializes position and dialogue.
     *
     * @param x      the world x-coordinate of the NPC's origin
     * @param y      the world y-coordinate of the NPC's origin
     * @param dialog array of dialogue lines to display on interaction
     * @throws RuntimeException if an animation frame resource is missing
     */
    public NPC(double x, double y, String[] dialog) {
        this.x = x;
        this.y = y;
        this.dialog = dialog;

        // Load animation frames named BroMonkeyStand1..5.png
        for (int i = 0; i < FRAME_COUNT; i++) {
            String path = String.format("/animation/BroMonkeyStand%d.png", i + 1);
            var is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("NPC frame not found: " + path);
            }
            frames[i] = new Image(is);
        }
    }

    /**
     * Advances the NPC animation based on elapsed time.
     * <p>
     * Should be called each frame from the game loop.
     * </p>
     *
     * @param dt    time elapsed since last update in seconds
     * @param world the world context (unused in current implementation)
     */
    public void update(double dt, World world) {
        frameTimer += dt;
        if (frameTimer >= FRAME_DURATION) {
            frameTimer -= FRAME_DURATION;
            frameIndex = (frameIndex + 1) % FRAME_COUNT;
        }
    }

    /**
     * Handles player interaction, toggling or advancing dialogue if within range.
     * <p>
     * If the player is close enough, opens dialogue or shows next line.
     * </p>
     *
     * @param playerX the player's world x-coordinate (center)
     * @param playerY the player's world y-coordinate (center)
     */
    public void interact(double playerX, double playerY) {
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
                    inDialog = false;
                }
            }
        }
    }

    /**
     * Indicates whether the NPC is currently in dialogue mode.
     *
     * @return true if a dialogue box should be displayed, false otherwise
     */
    public boolean isInDialog() {
        return inDialog;
    }

    /**
     * Retrieves the current line of dialogue.
     * <p>
     * Returns an empty string if not in dialogue.
     * </p>
     *
     * @return the current dialogue line or empty string
     */
    public String currentDialogLine() {
        if (!inDialog) {
            return "";
        }
        return dialog[Math.min(dialogLine, dialog.length - 1)];
    }

    /**
     * Renders the NPC to the screen with current animation frame.
     * <p>
     * Applies a vertical offset so NPC sprite appears grounded.
     * </p>
     *
     * @param gc  the graphics context for drawing
     * @param cam the camera providing world-to-screen translation
     */
    public void render(GraphicsContext gc, Camera cam) {
        double sx = x - cam.getWorldX();
        double sy = y - cam.getWorldY();
        double dyOffset = TileConstants.TILE_SIZE - npcHeight;
        gc.drawImage(frames[frameIndex],
                sx, sy + dyOffset,
                npcWidth, npcHeight);
    }
}
