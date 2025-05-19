package world;

public enum TileType {
    AIR(false),
    GRASS(true),
    DIRT(true),
    STONE(true),
    TREE_TRUNK(false),
    TREE_LEAVES(false);

    private final boolean solid;
    TileType(boolean solid) { this.solid = solid; }
    public boolean isSolid() { return solid; }
}
