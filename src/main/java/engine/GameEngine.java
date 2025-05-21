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

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import util.TileConstants;

public class GameEngine {

    private final GraphicsContext gc;
    private final int width, height;
    private final World world;
    private final Player player;
    private final Camera camera;

    private Map<TileType, Image> tileTextures;
    private Image backgroundImage;
    private AnimationTimer loop;
    private boolean running = false;


    public GameEngine(GraphicsContext gc, int width, int height) {
        this.gc = gc;
        this.width = width;
        this.height = height;

        loadTextures();

        try { backgroundImage = new Image(getClass().getResourceAsStream("/Forest_background_9.png")); }
        catch (Exception e) { System.err.println("No bg image: "+e.getMessage()); }

        Path lvl = Path.of("src/main/resources/level1.txt");
        try {
            if (Files.notExists(lvl))
                util.TerrainGenerator.generatePerlinLike(lvl.toString(), 200, 60, System.currentTimeMillis());
        } catch (Exception e) { throw new RuntimeException(e); }

        this.world = new World("/level1.txt");
        this.world.setTextures(tileTextures);


        
        int spawnX  = world.getWidth() / 2;
        int spawnY  = world.getSurfaceY(spawnX) - 1;
        this.player = new Player(spawnX * TileConstants.TILE_SIZE, spawnY * TileConstants.TILE_SIZE);

        /* 4. камера и цикл ----------------------------- */
        this.camera = new Camera(0, 0, width, height);

        loop = new AnimationTimer() {
            private long last = 0;
            @Override public void handle(long now) {
                if (last == 0) { last = now; return; }
                double dt = (now - last) / 1_000_000_000.0;
                last = now;
                update(dt);
                render();
            }
        };
    }

    /* === системные методы === */
    public void start() { if (!running) { loop.start(); running = true; } }
    public void stop()  { if (running)  { loop.stop();  running = false; } }

    /* === логика === */
    private void update(double dt) {
        player.update(dt, world);
        camera.centerOn(player.getX(), player.getY());
    }

    private void render() {
        gc.clearRect(0,0,width,height);

        if (backgroundImage != null) {
            double parallax = 0.01;
            gc.drawImage(backgroundImage,
                    -camera.getWorldX()*parallax,
                    -camera.getWorldY()*parallax,
                    width, height);
        } else {
            gc.setFill(Color.CORNFLOWERBLUE);
            gc.fillRect(0,0,width,height);
        }

        world.render(gc, camera);
        player.render(gc, camera);
    }

    private void loadTextures() {
        tileTextures = new HashMap<>();

        tileTextures.put(TileType.AIR, null);

        java.util.function.BiConsumer<TileType,String> add = (type,path) -> {
            var url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Texture not found: " + path);
            } else {
                tileTextures.put(type, new Image(url.toExternalForm()));
            }
        };

        add.accept(TileType.GRASS_TOP,   "/tiles/grass_top.png");
        add.accept(TileType.DIRT,        "/tiles/dirt.png");
        add.accept(TileType.STONE,       "/tiles/stone.png");
        add.accept(TileType.TREE_TRUNK,  "/tiles/trunk.png");
        add.accept(TileType.TREE_LEAVES, "/tiles/leaves.png");
    }


    public void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT  -> player.moveLeft();
            case D, RIGHT -> player.moveRight();
            case W, SPACE, UP -> player.jump();
        }
    }
    public void handleKeyRelease(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT  -> player.stopMovingLeft();
            case D, RIGHT -> player.stopMovingRight();
        }
    }

    public void handleMousePress(MouseEvent e) {
        int tx = (int) ((camera.getWorldX() + e.getX()) / 16);
        int ty = (int) ((camera.getWorldY() + e.getY()) / 16);

        if (e.isPrimaryButtonDown())     world.mineTile(tx, ty);
        else if (e.isSecondaryButtonDown()) world.placeTile(tx, ty, TileType.DIRT);
    }

    public void handleMouseRelease(MouseEvent e) { }
    public void handleMouseMove(MouseEvent e)    { }
}
