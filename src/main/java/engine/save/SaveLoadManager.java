package engine.save;

import entity.Player;
import util.Inventory;
import engine.level.LevelManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Manages saving and loading of game state, including inventory,
 * level index, and player position.
 */
public class SaveLoadManager {
    private final Inventory inventory;
    private final Player player;
    private final LevelManager lvlMgr;

    private static final String INVENTORY_FILE = "src/main/resources/inventory.txt";
    private static final String SAVEGAME_FILE   = "savegame.txt";

    /**
     * Constructs a SaveLoadManager with required game components.
     *
     * @param inventory the player's inventory to save/load
     * @param player    the player entity whose position is saved/loaded
     * @param lvlMgr    the level manager for saving/loading level index
     */
    public SaveLoadManager(Inventory inventory,
                           Player player,
                           LevelManager lvlMgr) {
        this.inventory = inventory;
        this.player    = player;
        this.lvlMgr    = lvlMgr;
    }

    /**
     * Saves the player's inventory to disk.
     * <p>
     * Errors are logged to stderr on failure.
     * </p>
     */
    public void saveInventory() {
        try {
            inventory.saveToFile(INVENTORY_FILE);
        } catch (IOException e) {
            System.err.println("Cannot save inventory: " + e.getMessage());
        }
    }

    /**
     * Deletes all save files (inventory and savegame).
     * <p>
     * Logs success or failure to standard output or stderr.
     * </p>
     */
    public void clearAll() {
        try {
            Files.deleteIfExists(Path.of(INVENTORY_FILE));
            Files.deleteIfExists(Path.of(SAVEGAME_FILE));
            System.out.println("SaveLoadManager: all save files deleted");
        } catch (IOException e) {
            System.err.println("SaveLoadManager: failed to clear saves: " + e.getMessage());
        }
    }

    /**
     * Loads the player's inventory from disk.
     * <p>
     * Errors are logged to stderr on failure.
     * </p>
     */
    public void loadInventory() {
        try {
            inventory.loadFromFile(INVENTORY_FILE);
        } catch (IOException e) {
            System.err.println("Cannot load inventory: " + e.getMessage());
        }
    }

    /**
     * Saves the full game state: inventory, current level, and player position.
     * <p>
     * Inventory is saved first, followed by writing level and coordinates
     * to a properties file.
     * Errors are logged to stderr on failure.
     * </p>
     */
    public void saveAll() {
        saveInventory();
        try (PrintWriter pw = new PrintWriter(SAVEGAME_FILE)) {
            pw.println("level=" + lvlMgr.getCurrentLevel());
            pw.println("playerX=" + player.getX());
            pw.println("playerY=" + player.getY());
        } catch (IOException e) {
            System.err.println("Cannot save game state: " + e.getMessage());
        }
    }

    /**
     * Loads the full game state: inventory, level, and player position.
     * <p>
     * Returns true if the save file existed and was loaded successfully;
     * false otherwise.
     * </p>
     *
     * @return true if game state loaded successfully, false if no save exists or an error occurred
     */
    public boolean loadAll() {
        // Load inventory first
        loadInventory();

        Path f = Path.of(SAVEGAME_FILE);
        if (!Files.exists(f)) {
            return false;
        }

        try (FileInputStream fis = new FileInputStream(SAVEGAME_FILE)) {
            Properties props = new Properties();
            props.load(fis);

            int level = Integer.parseInt(props.getProperty("level"));
            double x   = Double.parseDouble(props.getProperty("playerX"));
            double y   = Double.parseDouble(props.getProperty("playerY"));

            lvlMgr.loadLevel(level);
            player.setPosition(x, y);
            return true;
        } catch (Exception e) {
            System.err.println("Cannot load game state: " + e.getMessage());
            return false;
        }
    }
}
