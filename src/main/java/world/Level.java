package world;

import java.util.List;

/**
 * Represents a game level, including the tile map and entity spawn data.
 * <p>
 * Contains the 2D array of tiles defining the world, as well as lists of
 * item and NPC spawn points to initialize entities when the level is loaded.
 * </p>
 */
public class Level {
    private final TileType[][] tiles;
    private final List<ItemSpawn> itemSpawns;
    private final List<NPCSpawn>  npcSpawns;

    /**
     * Constructs a new Level with the specified tile layout and spawn points.
     *
     * @param tiles      2D array of TileType defining the map layout
     * @param itemSpawns list of item spawn definitions for this level
     * @param npcSpawns  list of NPC spawn definitions for this level
     */
    public Level(TileType[][] tiles,
                 List<ItemSpawn> itemSpawns,
                 List<NPCSpawn>  npcSpawns) {
        this.tiles      = tiles;
        this.itemSpawns = itemSpawns;
        this.npcSpawns  = npcSpawns;
    }

    /**
     * Returns the tile map for this level.
     *
     * @return a 2D array of TileType representing the level layout
     */
    public TileType[][] getTiles() {
        return tiles;
    }

    /**
     * Returns the list of item spawn points.
     *
     * @return list of ItemSpawn instances specifying where items appear
     */
    public List<ItemSpawn> getItemSpawns() {
        return itemSpawns;
    }

    /**
     * Returns the list of NPC spawn points.
     *
     * @return list of NPCSpawn instances specifying where NPCs appear
     */
    public List<NPCSpawn> getNpcSpawns() {
        return npcSpawns;
    }

    /**
     * Defines a spawn point for an item within the level grid.
     */
    public static class ItemSpawn {
        /** ID of the item to spawn */
        public final ItemType itemType;
        /** X-coordinate (in tile units) where the item should spawn */
        public final int      tileX;
        /** Y-coordinate (in tile units) where the item should spawn */
        public final int      tileY;

        /**
         * Constructs a new ItemSpawn definition.
         *
         * @param itemType the type of item to spawn
         * @param tileX    the x-coordinate in tile space
         * @param tileY    the y-coordinate in tile space
         */
        public ItemSpawn(ItemType itemType, int tileX, int tileY) {
            this.itemType = itemType;
            this.tileX    = tileX;
            this.tileY    = tileY;
        }
    }

    /**
     * Defines a spawn point for an NPC within the level grid.
     */
    public static class NPCSpawn {
        /** Identifier of the NPC to spawn (e.g., character ID) */
        public final String npcId;
        /** X-coordinate (in tile units) where the NPC should spawn */
        public final int    tileX;
        /** Y-coordinate (in tile units) where the NPC should spawn */
        public final int    tileY;

        /**
         * Constructs a new NPCSpawn definition.
         *
         * @param npcId identifier of the NPC to spawn
         * @param tileX the x-coordinate in tile space
         * @param tileY the y-coordinate in tile space
         */
        public NPCSpawn(String npcId, int tileX, int tileY) {
            this.npcId = npcId;
            this.tileX = tileX;
            this.tileY = tileY;
        }
    }
}
