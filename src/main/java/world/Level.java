package world;

import java.util.List;

public class Level {
    private final TileType[][] tiles;
    private final List<ItemSpawn> itemSpawns;
    private final List<NPCSpawn>  npcSpawns;   // ← добавили

    public Level(TileType[][] tiles,
                 List<ItemSpawn> itemSpawns,
                 List<NPCSpawn>  npcSpawns) {
        this.tiles      = tiles;
        this.itemSpawns = itemSpawns;
        this.npcSpawns  = npcSpawns;
    }

    public TileType[][] getTiles()             { return tiles; }
    public List<ItemSpawn> getItemSpawns()     { return itemSpawns; }
    public List<NPCSpawn>  getNpcSpawns()      { return npcSpawns; }  // ← новый геттер

    /** Спавн предмета */
    public static class ItemSpawn {
        public final ItemType itemType;  // вместо String теперь ItemType
        public final int      tileX, tileY;

        public ItemSpawn(ItemType itemType, int tileX, int tileY) {
            this.itemType = itemType;
            this.tileX    = tileX;
            this.tileY    = tileY;
        }
    }

    /** Спавн NPC */
    public static class NPCSpawn {
        public final String npcId;
        public final int    tileX, tileY;

        public NPCSpawn(String npcId, int tileX, int tileY) {
            this.npcId = npcId;
            this.tileX = tileX;
            this.tileY = tileY;
        }
    }
}
