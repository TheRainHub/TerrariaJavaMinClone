package engine.level;

import javafx.scene.canvas.GraphicsContext;
import world.*;
import util.TileConstants;
import engine.Camera;
import world.WorldRenderer;
import entity.Player;
import entity.ItemEntity;
import entity.NPC;
import util.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Управляет загрузкой уровней, переходами между ними,
 * а также операциями с тайлами (добыча/постройка).
 */
public class LevelManager {
    private final Player player;
    private final Camera camera;
    private final Inventory inventory;
    private final List<String> levelFiles;
    private int currentLevel = 0;

    private World world;
    private WorldRenderer renderer;
    private final List<ItemEntity> items = new ArrayList<>();
    private final List<NPC> npcs    = new ArrayList<>();

    public LevelManager(Player player,
                         Camera camera,
                         Inventory inventory,
                         List<String> levelFiles) {
        this.player     = player;
        this.camera     = camera;
        this.inventory  = inventory;
        this.levelFiles = levelFiles;
    }

    /** Инициализация: загрузить первый уровень. */
    public void init() {
        loadLevel(0);
    }

    /**
     * Загрузить уровень по индексу:
     * - зарегистрировать тайлы
     * - разобрать файл карты
     * - создать World и WorldRenderer
     * - заспавнить предметы и NPC
     * - установить позицию игрока на поверхности
     */
    public void loadLevel(int index) {
        currentLevel = index;

        // 1) Зарегистрировать тайлы
        TileRegistry registry = new TileRegistry();

        // 2) Загрузить уровень, передав TileRegistry
        Level lvl = WorldLoader.loadLevel(levelFiles.get(index), registry);

        // 3) Создать мир и рендерер (текстуры берутся из registry)
        this.world    = new World(lvl.getTiles());
        this.renderer = new WorldRenderer(registry.getAllTextures());

        // 4) Спавн предметов
        items.clear();
        for (var spawn : lvl.getItemSpawns()) {
            double px = spawn.tileX * TileConstants.TILE_SIZE;
            double py = spawn.tileY * TileConstants.TILE_SIZE;
            items.add(new ItemEntity(
                    spawn.itemType,
                    inventory,
                    px,
                    py
            ));
        }

        // 5) Спавн NPC
        npcs.clear();
        for (var spawn : lvl.getNpcSpawns()) {
            String[] dialog;
            if ("bro".equals(spawn.npcId)) {
                dialog = new String[]{
                        "Hello Bro!",
                        "To become monkey king, you should have a lot of bananas!",
                        "You can find a lot of bananas in level 2 and 3.",
                        "Just go to the right and when you find 20, you will win."
                };
            } else {
                dialog = new String[]{"...(unknown NPC)..."};
            }
            double nx = spawn.tileX * TileConstants.TILE_SIZE;
            double ny = spawn.tileY * TileConstants.TILE_SIZE;
            npcs.add(new NPC(
                    nx,
                    ny,
                    dialog
            ));
        }

        // 6) Позиционирование игрока и центрирование камеры
        int sx = world.getWidth() / 2;
        int sy = world.getSurfaceY(sx) - 1;
        player.setPosition(
                sx * TileConstants.TILE_SIZE,
                sy * TileConstants.TILE_SIZE
        );
        camera.centerOn(player.getX(), player.getY());
    }

    /** Рендер тайлового мира. */
    public void renderWorld(GraphicsContext gc) {
        renderer.render(gc, camera, world.getTiles());
    }

    /** Проверка выхода за границы уровня и переходы. */
    public void checkTransitions(Player player) {
        int ts = TileConstants.TILE_SIZE;
        int tx = (int)(player.getX() / ts);
        if (tx < 0 && currentLevel > 0) {
            loadLevel(currentLevel - 1);
            player.setPosition(
                    world.getWidth() * ts - Player.PLAYER_WIDTH - 1,
                    player.getY()
            );
        } else if (tx >= world.getWidth() && currentLevel < levelFiles.size() - 1) {
            loadLevel(currentLevel + 1);
            player.setPosition(1, player.getY());
        }
    }

    /** Добыть тайл (левая кнопка мыши). */
    public void mineTile(int tx, int ty) {
        world.mineTile(tx, ty);
    }

    /** Поставить тайл (правая кнопка мыши). */
    public void placeTile(int tx, int ty, TileType type) {
        world.placeTile(tx, ty, type);
    }

    // --- Геттеры ---
    public World getWorld()           { return world; }
    public WorldRenderer getRenderer() { return renderer; }
    public Camera getCamera()         { return camera; }
    public int getTileSize()          { return TileConstants.TILE_SIZE; }
    public List<ItemEntity> getItems() { return items; }
    public List<NPC> getNpcs()        { return npcs; }
    public int getCurrentLevel()      { return currentLevel; }
}
