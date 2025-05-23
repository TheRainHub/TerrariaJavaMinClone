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
 * Отвечает за сохранение и загрузку состояния игры:
 * - Инвентарь
 * - Текущий уровень и позиция игрока
 */
public class SaveLoadManager {
    private final Inventory inventory;
    private final Player player;
    private final LevelManager lvlMgr;

    private static final String INVENTORY_FILE = "src/main/resources/inventory.txt";
    private static final String SAVEGAME_FILE   = "savegame.txt";

    public SaveLoadManager(Inventory inventory,
                           Player player,
                           LevelManager lvlMgr) {
        this.inventory = inventory;
        this.player    = player;
        this.lvlMgr    = lvlMgr;
    }

    /** Сохранить только инвентарь. */
    public void saveInventory() {
        try {
            inventory.saveToFile(INVENTORY_FILE);
        } catch (IOException e) {
            System.err.println("Cannot save inventory: " + e.getMessage());
        }
    }
    public void clearAll() {
        try {
            Files.deleteIfExists(Path.of("src/main/resources/inventory.txt"));
            Files.deleteIfExists(Path.of("savegame.txt"));
            System.out.println("SaveLoadManager: all save files deleted");
        } catch (IOException e) {
            System.err.println("SaveLoadManager: failed to clear saves: " + e.getMessage());
        }
    }

    /** Загрузить только инвентарь. */
    public void loadInventory() {
        try {
            inventory.loadFromFile(INVENTORY_FILE);
        } catch (IOException e) {
            System.err.println("Cannot load inventory: " + e.getMessage());
        }
    }

    /** Сохранить состояние игры: уровень, позицию игрока и инвентарь. */
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

    /** Загрузить состояние игры и установить уровень и позицию игрока. */
    public boolean loadAll() {
        // inventory инициализируется первым
        loadInventory();

        Path f = Path.of(SAVEGAME_FILE);
        if (!Files.exists(f)) return false;

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
