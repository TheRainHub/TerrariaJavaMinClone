package engine;

import world.TileType;
import world.World;
import entity.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static world.TileType.AIR;
import static world.TileType.DIRT;

public class GameEngine {
    private final GraphicsContext gc;
    private final int width;
    private final int height;
    private final World world;
    private final Player player;
    private final Camera camera;
    private AnimationTimer gameLoop;
    private boolean isRunning = false;

    private Image backgroundImage;

    public GameEngine(GraphicsContext gc, int width, int height) {
        this.gc = gc;
        this.width = width;
        this.height = height;

        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/Forest_background_9.png"));
        } catch (Exception e) {
            System.err.println("Image not found: " + e.getMessage());
        }

        this.world = new World("/level1.txt");
        int spawnTileX = world.getWidth() / 2;
        int spawnTileY = world.getSurfaceY(spawnTileX) - 1;
        int tileSize   = 16;

        this.player = new Player(spawnTileX * tileSize,
                spawnTileY * tileSize);
        this.camera = new Camera(0, 0, width, height);

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                update(deltaTime);
                render();
            }
        };
    }

    public void start() {
        if (!isRunning) {
            gameLoop.start();
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            gameLoop.stop();
            isRunning = false;
        }
    }

    private void update(double deltaTime) {
        player.update(deltaTime, world);
        camera.centerOn(player.getX(), player.getY());
    }

    private void render() {
        gc.clearRect(0, 0, width, height);

        if (backgroundImage != null) {
            double parallaxFactor = 0.01;
            double bgX = -camera.getWorldX() * parallaxFactor;
            double bgY = -camera.getWorldY() * parallaxFactor;
            gc.drawImage(backgroundImage, bgX, bgY, width, height);
        } else {
            gc.setFill(Color.SKYBLUE);
            gc.fillRect(0, 0, width, height);
        }

        world.render(gc, camera);
        player.render(gc, camera);

        renderUI();
    }

    private void renderUI() {

    }

    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case W:
            case SPACE:
                player.jump();
                break;
            case A:
            case LEFT:
                player.moveLeft();
                break;
            case D:
            case RIGHT:
                player.moveRight();
                break;
        }
    }
    public void handleKeyRelease(KeyEvent event) {
        switch (event.getCode()) {
            case A:
            case LEFT:
                player.stopMovingLeft();
                break;
            case D:
            case RIGHT:
                player.stopMovingRight();
                break;
        }
    }

    public void handleMousePress(MouseEvent event) {
        int worldX = camera.screenToWorldX(event.getX());
        int worldY = camera.screenToWorldY(event.getY());

        if (event.isPrimaryButtonDown()) {
            world.mineTile(worldX, worldY);
        } else if (event.isSecondaryButtonDown()) {
            world.placeTile(worldX, worldY, DIRT);
        }
    }
    public void handleMouseRelease(MouseEvent event) {}
    public void handleMouseMove(MouseEvent event) {}
}
