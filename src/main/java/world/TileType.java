package world;

public enum TileType {
    GRASS_TOP(true),
    GRASS_LEFT(true),
    GRASS_RIGHT(true),
    GRASS_BOTTOM(true),
    DIRT(true),
    STONE(true),
    TREE_TRUNK(true),
    TREE_LEAVES(false),
    AIR(false);

    private final boolean solid;
    TileType(boolean s){ solid=s; }
    public boolean isSolid(){ return solid; }
}
