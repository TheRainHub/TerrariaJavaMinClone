package world;

import java.util.List;

/** Хранит тайлы и позиции для спавна предметов */
public class Level {
    private final TileType[][] tiles;
    private final List<ItemSpawn> itemSpawns;

    public Level(TileType[][] tiles, List<ItemSpawn> itemSpawns) {
        this.tiles       = tiles;
        this.itemSpawns  = itemSpawns;
    }

    public TileType[][] getTiles() {
        return tiles;
    }
    public List<ItemSpawn> getItemSpawns() {
        return itemSpawns;
    }

    /** Описывает один спавн: id предмета + координаты в тайлах */
    public static class ItemSpawn {
        public final ItemType type;
        public final int      tileX, tileY;

        public ItemSpawn(ItemType type, int tileX, int tileY) {
            this.type  = type;
            this.tileX = tileX;
            this.tileY = tileY;
        }
    }
}
