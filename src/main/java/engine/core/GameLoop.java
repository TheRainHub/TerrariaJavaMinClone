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

/**
 * Main game loop that drives updating of game state and rendering of each frame.
 * <p>
 * Extends JavaFX's AnimationTimer to provide a continuous loop, handling input,
 * physics/world updates, and drawing to the canvas.
 * </p>
 */
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

    // Dimensions of the rendering canvas
    private final int width;
    private final int height;

    // Background image for parallax effect (may be null)
    private final Image backgroundImage;

    // Flag indicating the player has won the game
    private boolean gameWon = false;

    /**
     * Constructs a new GameLoop with all required subsystems.
     *
     * @param gc              the graphics context used for drawing
     * @param scene           the JavaFX scene to listen for input events
     * @param width           the width of the game canvas in pixels
     * @param height          the height of the game canvas in pixels
     * @param lvlMgr          the level manager controlling world data and rendering
     * @param uiMgr           the UI manager handling pause menus and overlays
     * @param input           the input handler for keyboard and mouse events
     * @param player          the player entity to update and render
     * @param backgroundImage the background image for parallax scrolling (nullable)
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

        // Register keyboard handlers for pause toggle and game input
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                uiMgr.togglePause();
            } else if (uiMgr.isPaused()) {
                uiMgr.handlePauseInput(e);
            } else {
                input.handleKeyPress(e);
            }
        });

        // Register key release and mouse handlers
        scene.addEventHandler(KeyEvent.KEY_RELEASED, input::handleKeyRelease);
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, input::handleMousePress);
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, input::handleMouseRelease);
    }

    /**
     * Called every frame by JavaFX. Computes delta time and delegates to update
     * and render methods.
     *
     * @param now timestamp in nanoseconds for the current frame
     */
    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            // First frame initialization
            lastTime = now;
            return;
        }
        double dt = (now - lastTime) / 1e9;
        lastTime = now;

        update(dt);
        render();
    }

    /**
     * Returns the JavaFX scene associated with this game loop.
     * <p>
     * Note: simple getter, no side effects.
     * </p>
     *
     * @return the Scene used for input and focus
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Updates game state: player, NPCs, items, and transitions, unless paused or game won.
     *
     * @param dt time elapsed since last frame in seconds
     */
    private void update(double dt) {
        if (gameWon) {
            // Stop any further game updates once won
            return;
        }

        if (uiMgr.isPaused() || uiMgr.isCraftingOpen()) {
            // Skip world updates when paused or crafting UI is open
            return;
        }

        // Update player movement and physics
        player.update(dt, lvlMgr.getWorld());

        // Center camera on player
        lvlMgr.getCamera().centerOn(player.getX(), player.getY());

        // Update and remove collected items
        Iterator<ItemEntity> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().update(player, lvlMgr.getWorld())) {
                it.remove();
            }
        }

        // Update all NPC entities
        for (NPC npc : npcs) {
            npc.update(dt, lvlMgr.getWorld());
        }

        // Handle level transitions (e.g., entering new rooms)
        lvlMgr.checkTransitions(player);
    }

    /**
     * Renders the entire game frame to the canvas, including background,
     * world tiles, entities, and UI overlays.
     */
    private void render() {
        // 1) Clear the canvas
        gc.setGlobalAlpha(1.0);
        gc.clearRect(0, 0, width, height);

        // 2) If the player has won, display a victory screen
        if (gameWon) {
            gc.setGlobalAlpha(1.0);
            // Semi-transparent dark overlay
            gc.setFill(Color.rgb(0, 0, 0, 0.75));
            gc.fillRect(0, 0, width, height);

            // Large gold victory text
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

        // 3) Draw background with simple parallax effect if provided
        if (backgroundImage != null) {
            double parallaxFactor = 0.2;
            gc.drawImage(
                    backgroundImage,
                    -lvlMgr.getCamera().getWorldX() * parallaxFactor,
                    -lvlMgr.getCamera().getWorldY() * parallaxFactor,
                    width, height
            );
        } else {
            // Fallback sky color
            gc.setFill(Color.CORNFLOWERBLUE);
            gc.fillRect(0, 0, width, height);
        }

        // 4) Render world tiles and layers
        lvlMgr.renderWorld(gc);

        // 5) Render dynamic entities: items, NPCs, and player
        for (ItemEntity item : items) {
            item.render(gc, lvlMgr.getCamera());
        }
        for (NPC npc : npcs) {
            npc.render(gc, lvlMgr.getCamera());
        }
        player.render(gc, lvlMgr.getCamera());

        // 6) Draw UI elements on top of everything
        uiMgr.renderUI(gc);
    }
}
