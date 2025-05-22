package engine;

import entity.ItemEntity;
import entity.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import util.Inventory;
import util.ResourceLoader;
import util.TileConstants;
import world.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEngine {
    private final GraphicsContext gc;
    private final int width, height;

    private final World world;
    private final WorldRenderer renderer;
    private final Player player;
    private final List<ItemEntity> items;
    private final Inventory inventory;
    private final Camera camera;

    private Image backgroundImage;
    private AnimationTimer loop;
    private boolean running = false;

    private static final String INVENTORY_FILE = "src/main/resources/inventory.txt";

    public GameEngine(GraphicsContext gc, int width, int height) {
        this.gc     = gc;
        this.width  = width;
        this.height = height;

        // 1) Инвентарь
        inventory = new Inventory();
        try {
            inventory.loadFromFile(INVENTORY_FILE);
        } catch (IOException e) {
            System.err.println("Failed to load inventory: " + e.getMessage());
        }

        // 2) TileRegistry и Level
        TileRegistry registry = new TileRegistry();
        Level levelData = WorldLoader.loadLevel("map.txt", registry);


        // 3) Создаём мир и рендерер
        world    = new World(levelData.getTiles());
        renderer = new WorldRenderer(registry.getAllTextures());

        // 4) Спавн предметов
        items = new ArrayList<>();
        for (Level.ItemSpawn spawn : levelData.getItemSpawns()) {
            double px = spawn.tileX * TileConstants.TILE_SIZE;
            double py = spawn.tileY * TileConstants.TILE_SIZE;
            items.add(new ItemEntity(
                    spawn.type,    // ItemType
                    inventory,     // Inventory
                    px, py
            ));
        }

        // 5) Фон
        try {
            backgroundImage = new Image(getClass().getResourceAsStream("/Forest_background_9.png"));
        } catch (Exception e) {
            System.err.println("No bg image: " + e.getMessage());
        }

        // 6) Генерация карты, если нужно (необязательно)
        Path lvl = Path.of("src/main/resources/map.txt");
        if (Files.notExists(lvl)) {
            try {
                util.TerrainGenerator.generatePerlinLike(
                        lvl.toString(), 200, 60, System.currentTimeMillis()
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        // 7) Создаём игрока над поверхностью
        int spawnTileX = world.getWidth() / 2;
        int spawnTileY = world.getSurfaceY(spawnTileX) - 1;
        player = new Player(
                spawnTileX * TileConstants.TILE_SIZE,
                spawnTileY * TileConstants.TILE_SIZE
        );

        // 8) Камера и игровой цикл
        camera = new Camera(0, 0, width, height);
        loop = new AnimationTimer() {
            private long last = 0;
            @Override public void handle(long now) {
                if (last == 0) { last = now; return; }
                double dt = (now - last) / 1_000_000_000.0;
                last = now;
                update(dt);
                render();
                renderUI();
            }
        };
    }

    public void start() {
        if (!running) {
            loop.start();
            running = true;
        }
    }

    public void stop() {
        if (running) {
            loop.stop();
            running = false;
            saveInventory();
        }
    }

    private void update(double dt) {
        // 1) Физика героя
        player.update(dt, world);
        camera.centerOn(player.getX(), player.getY());

        // 2) Обновляем и удаляем предметы
        //    итерация через Iterator, чтобы точно удалить правильно
        Iterator<ItemEntity> it = items.iterator();
        while (it.hasNext()) {
            ItemEntity item = it.next();
            // если update вернул true — предмет собран, удаляем его
            if (item.update(player, world)) {
                it.remove();
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, width, height);

        if (backgroundImage != null) {
            double p = 0.01;
            gc.drawImage(
                    backgroundImage,
                    -camera.getWorldX() * p,
                    -camera.getWorldY() * p,
                    width, height
            );
        } else {
            gc.setFill(Color.CORNFLOWERBLUE);
            gc.fillRect(0, 0, width, height);
        }

        renderer.render(gc, camera, world.getTiles());
        items.forEach(i -> i.render(gc, camera));
        player.render(gc, camera);
    }

    private void renderUI() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        int y = 20;
        for (var e : inventory.getItems().entrySet()) {
            gc.fillText(e.getKey() + " x" + e.getValue(), 10, y);
            y += 20;
        }
    }

    private void saveInventory() {
        try {
            inventory.saveToFile(INVENTORY_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save inventory: " + e.getMessage());
        }
    }

    // ==== Управление ====
    public void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT         -> player.moveLeft();
            case D, RIGHT        -> player.moveRight();
            case W, SPACE, UP    -> player.jump();
        }
    }

    public void handleKeyRelease(KeyEvent e) {
        switch (e.getCode()) {
            case A, LEFT  -> player.stopMovingLeft();
            case D, RIGHT -> player.stopMovingRight();
        }
    }

    public void handleMousePress(MouseEvent e) {
        int tx = (int)((camera.getWorldX() + e.getX()) / TileConstants.TILE_SIZE);
        int ty = (int)((camera.getWorldY() + e.getY()) / TileConstants.TILE_SIZE);
        if (e.isPrimaryButtonDown())
            world.mineTile(tx, ty);
        else if (e.isSecondaryButtonDown())
            world.placeTile(tx, ty, TileType.DIRT);
    }

    public void handleMouseRelease(MouseEvent e) { }
    public void handleMouseMove(MouseEvent e)    { }
}
