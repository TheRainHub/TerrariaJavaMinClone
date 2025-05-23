package world;

import util.ResourceLoader;
import java.util.*;

/**
 * Utility class for loading level data from resource files.
 * <p>
 * Parses map layout from text-based level files and constructs
 * {@link Level} instances including tile data, item spawns, and NPC spawns.
 * </p>
 */
public class WorldLoader {

    /**
     * Loads a level definition from the specified resource path.
     * <p>
     * The level file should begin with a rectangular block of characters
     * representing tile types, followed by lines prefixed with "ITEM" or "NPC"
     * indicating entity spawn points.
     * </p>
     * <ol>
     *   <li>Determines map width and height from the first block of lines of equal length.</li>
     *   <li>Uses {@link TileRegistry#fromChar(char)} to translate characters to tiles.</li>
     *   <li>Parses subsequent lines: "ITEM id x y" for items, "NPC id x y" for NPCs.</li>
     *   <li>Returns a {@link Level} containing the tile map and spawn lists.</li>
     * </ol>
     *
     * @param resourcePath path to the level file in classpath (e.g., "/map1.txt")
     * @param registry     the TileRegistry used to convert characters to {@link TileType}
     * @return a fully populated Level instance
     * @throws RuntimeException if the level file is empty or cannot be read
     */
    public static Level loadLevel(String resourcePath, TileRegistry registry) {
        List<String> lines = ResourceLoader.readResourceLines(resourcePath);
        if (lines.isEmpty()) {
            throw new RuntimeException("Level file is empty: " + resourcePath);
        }

        // 1) Determine map dimensions
        int width  = lines.get(0).length();
        int height = 0;
        while (height < lines.size() && lines.get(height).length() == width) {
            height++;
        }

        // 2) Populate tile array
        TileType[][] tiles = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                tiles[y][x] = registry.fromChar(row.charAt(x));
            }
        }

        // 3) Parse item and NPC spawn lines
        List<Level.ItemSpawn> itemSpawns = new ArrayList<>();
        List<Level.NPCSpawn>  npcSpawns  = new ArrayList<>();

        for (int i = height; i < lines.size(); i++) {
            String[] parts = lines.get(i).trim().split("\\s+");
            if (parts[0].equals("ITEM")) {
                ItemType t = ItemType.fromId(parts[1]);
                if (t != null) {
                    itemSpawns.add(new Level.ItemSpawn(
                            t,
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3])
                    ));
                }
            } else if (parts[0].equals("NPC")) {
                npcSpawns.add(new Level.NPCSpawn(
                        parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])
                ));
            }
        }

        // 4) Return the populated Level
        return new Level(tiles, itemSpawns, npcSpawns);
    }
}