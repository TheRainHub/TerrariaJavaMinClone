package world;

import java.util.List;

/**
 * Represents a game level, containing the tile map and item spawn definitions.
 * <p>
 * A Level consists of:
 * <ul>
 *   <li>a 2D array of {@link TileType} defining the layout of solid/empty tiles;</li>
 *   <li>a list of {@link ItemSpawn} entries, each specifying an {@link ItemType}
 *       and the (x,y) coordinates in tile units where the item should appear.</li>
 * </ul>
 * </p>
 */
public class Level {
    /** 2D grid of tiles: first index = Y (row), second = X (column). */
    private final TileType[][] tiles;
    /** List of item spawn definitions for this level. */
    private final List<ItemSpawn> itemSpawns;

    /**
     * Constructs a Level with the given tile grid and item spawns.
     *
     * @param tiles      the tile map as a 2D array of {@link TileType}
     * @param itemSpawns list of {@link ItemSpawn} entries for this level
     */
    public Level(TileType[][] tiles, List<ItemSpawn> itemSpawns) {
        this.tiles      = tiles;
        this.itemSpawns = itemSpawns;
    }

    /**
     * Returns the tile grid for this level.
     *
     * @return 2D array of {@link TileType}, indexed [row][column]
     */
    public TileType[][] getTiles() {
        return tiles;
    }

    /**
     * Returns the list of item spawns in this level.
     *
     * @return list of {@link ItemSpawn} objects
     */
    public List<ItemSpawn> getItemSpawns() {
        return itemSpawns;
    }

    /**
     * Describes a single item spawn point in the level.
     * <p>
     * Contains the {@link ItemType} to spawn and the tile coordinates
     * where it should appear (tileX, tileY).
     * </p>
     */
    public static class ItemSpawn {
        /** The type of item to spawn. */
        public final ItemType type;
        /** X coordinate in tile units. */
        public final int tileX;
        /** Y coordinate in tile units. */
        public final int tileY;

        /**
         * Constructs an ItemSpawn with the given item type and tile position.
         *
         * @param type  the {@link ItemType} to spawn
         * @param tileX the X coordinate (in tiles)
         * @param tileY the Y coordinate (in tiles)
         */
        public ItemSpawn(ItemType type, int tileX, int tileY) {
            this.type  = type;
            this.tileX = tileX;
            this.tileY = tileY;
        }
    }
}
