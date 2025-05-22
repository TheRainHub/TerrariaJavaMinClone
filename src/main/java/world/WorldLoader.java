package world;

import util.ResourceLoader;
import java.util.*;

/**
 * Utility class for loading level data from a text resource into a {@link Level} object.
 * <p>
 * The level file consists of:
 * <ol>
 *   <li>a rectangular block of characters where each character maps to a {@link TileType};</li>
 *   <li>optional lines afterwards beginning with "ITEM" describing item spawns in the format
 *       {@code ITEM <id> <x> <y>}.</li>
 * </ol>
 * </p>
 */
public class WorldLoader {

    /**
     * Loads a level from the given classpath resource.
     *
     * <p>Steps:
     * <ol>
     *   <li>Reads all lines via {@link ResourceLoader#readResourceLines(String)}.</li>
     *   <li>Determines the width as the length of the first line, and the height as the number
     *       of consecutive lines matching that width.</li>
     *   <li>Constructs a {@code TileType[height][width]} array by mapping each character
     *       in the rectangular block to a {@link TileType} using the provided {@link TileRegistry}.</li>
     *   <li>Parses any subsequent lines beginning with "ITEM", splitting on whitespace,
     *       looking up the {@link ItemType} via {@link ItemType#fromId(String)},
     *       and collecting {@link Level.ItemSpawn} entries.</li>
     *   <li>Returns a new {@link Level} containing the tile grid and item spawn list.</li>
     * </ol>
     * </p>
     *
     * @param resourcePath the classpath resource path (e.g. "/map1.txt") to load
     * @param registry     the TileRegistry mapping characters to TileType
     * @return a fully populated {@link Level} instance
     * @throws RuntimeException if the file cannot be read or is empty
     */
    public static Level loadLevel(String resourcePath, TileRegistry registry) {
        List<String> lines = ResourceLoader.readResourceLines(resourcePath);
        if (lines == null || lines.isEmpty()) {
            throw new RuntimeException("Failed to load level data from '" + resourcePath + "'");
        }

        // 1) Determine rectangular map dimensions
        int width  = lines.get(0).length();
        int height = 0;
        while (height < lines.size() && lines.get(height).length() == width) {
            height++;
        }

        // 2) Parse tile grid
        TileType[][] tiles = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                tiles[y][x] = registry.fromChar(row.charAt(x));
            }
        }

        // 3) Parse item spawns after the grid
        List<Level.ItemSpawn> itemSpawns = new ArrayList<>();
        for (int i = height; i < lines.size(); i++) {
            String l = lines.get(i).trim();
            if (!l.startsWith("ITEM")) continue;
            String[] p = l.split("\\s+");
            // format: ITEM <id> <x> <y>
            ItemType t = ItemType.fromId(p[1]);
            if (t != null) {
                int tx = Integer.parseInt(p[2]);
                int ty = Integer.parseInt(p[3]);
                itemSpawns.add(new Level.ItemSpawn(t, tx, ty));
            }
        }

        // 4) Construct and return the Level
        return new Level(tiles, itemSpawns);
    }
}
