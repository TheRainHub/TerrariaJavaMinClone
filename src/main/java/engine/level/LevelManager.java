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
 * Manages level loading, transitions between levels,
 * and tile operations such as mining and placing.
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

    /**
     * Constructs a LevelManager with required game components.
     *
     * @param player     the player entity
     * @param camera     the camera for rendering view
     * @param inventory  the player's inventory to spawn items into
     * @param levelFiles list of file paths for level definitions
     */
    public LevelManager(Player player,
                        Camera camera,
                        Inventory inventory,
                        List<String> levelFiles) {
        this.player     = player;
        this.camera     = camera;
        this.inventory  = inventory;
        this.levelFiles = levelFiles;
    }

    /**
     * Initializes the level manager by loading the first level (index 0).
     */
    public void init() {
        loadLevel(0);
    }

    /**
     * Loads the level at the specified index.
     * <ol>
     *   <li>Registers tile types</li>
     *   <li>Parses the map file</li>
     *   <li>Creates World and WorldRenderer</li>
     *   <li>Spawns items and NPCs</li>
     *   <li>Positions the player and centers the camera</li>
     * </ol>
     *
     * @param index the index of the level to load
     */
    public void loadLevel(int index) {
        currentLevel = index;

        // 1) Register tiles
        TileRegistry registry = new TileRegistry();

        // 2) Load level data
        Level lvl = WorldLoader.loadLevel(levelFiles.get(index), registry);

        // 3) Create world and renderer
        this.world    = new World(lvl.getTiles());
        this.renderer = new WorldRenderer(registry.getAllTextures());

        // 4) Spawn items at tile locations
        items.clear();
        for (var spawn : lvl.getItemSpawns()) {
            double px = spawn.tileX * TileConstants.TILE_SIZE;
            double py = spawn.tileY * TileConstants.TILE_SIZE;
            items.add(new ItemEntity(spawn.itemType, inventory, px, py));
        }

        // 5) Spawn NPCs with dialogues
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
            npcs.add(new NPC(nx, ny, dialog));
        }

        // 6) Position player at surface and center camera
        int sx = world.getWidth() / 2;
        int sy = world.getSurfaceY(sx) - 1;
        player.setPosition(
                sx * TileConstants.TILE_SIZE,
                sy * TileConstants.TILE_SIZE
        );
        camera.centerOn(player.getX(), player.getY());
    }

    /**
     * Renders the tile-based world using the provided graphics context.
     *
     * @param gc the GraphicsContext used for drawing
     */
    public void renderWorld(GraphicsContext gc) {
        renderer.render(gc, camera, world.getTiles());
    }

    /**
     * Checks if the player has moved beyond level boundaries and
     * transitions to the previous or next level if available.
     *
     * @param player the player entity to check position for
     */
    public void checkTransitions(Player player) {
        int ts = TileConstants.TILE_SIZE;
        int tx = (int)(player.getX() / ts);
        if (tx < 0 && currentLevel > 0) {
            // Move to previous level
            loadLevel(currentLevel - 1);
            player.setPosition(
                    world.getWidth() * ts - Player.PLAYER_WIDTH - 1,
                    player.getY()
            );
        } else if (tx >= world.getWidth() && currentLevel < levelFiles.size() - 1) {
            // Move to next level
            loadLevel(currentLevel + 1);
            player.setPosition(1, player.getY());
        }
    }

    /**
     * Mines the tile at the specified tile coordinates.
     *
     * @param tx the x-coordinate of the tile
     * @param ty the y-coordinate of the tile
     */
    public void mineTile(int tx, int ty) {
        world.mineTile(tx, ty);
    }

    /**
     * Places a tile of the given type at the specified tile coordinates.
     *
     * @param tx   the x-coordinate of the tile
     * @param ty   the y-coordinate of the tile
     * @param type the type of tile to place
     */
    public void placeTile(int tx, int ty, TileType type) {
        world.placeTile(tx, ty, type);
    }

    // --- Getters ---

    /**
     * @return the current world instance
     */
    public World getWorld() { return world; }

    /**
     * @return the world renderer used for drawing tiles
     */
    public WorldRenderer getRenderer() { return renderer; }

    /**
     * @return the camera tracking the player
     */
    public Camera getCamera() { return camera; }

    /**
     * @return the size of one tile in pixels
     */
    public int getTileSize() { return TileConstants.TILE_SIZE; }

    /**
     * @return list of active item entities in the level
     */
    public List<ItemEntity> getItems() { return items; }

    /**
     * @return list of active NPC entities in the level
     */
    public List<NPC> getNpcs() { return npcs; }

    /**
     * @return the index of the currently loaded level
     */
    public int getCurrentLevel() { return currentLevel; }
}
