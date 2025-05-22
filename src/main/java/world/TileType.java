package world;

public enum TileType {
    AIR(false, false, null),
    DIRT(true, true, "dirt"),
    STONE(true, true, "stone"),
    GRASS_TOP(true, true, "grass_top"),
    GRASS_LEFT(true, true, "grass_left"),
    GRASS_RIGHT(true, true, "grass_right"),
    GRASS_BOTTOM(true, true, "grass_bottom"),
    TREE_MAIN(false,true, "tree_main"),
    TREE_TRUNK(false, true, "trunk"),
    TREE_LEAVES(false, true, "leaves");


    private final boolean solid;
    private final boolean breakable;
    private final String textureKey;

    TileType(boolean solid, boolean breakable, String textureKey) {
        this.solid = solid;
        this.breakable = breakable;
        this.textureKey = textureKey;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public String getTextureKey() {
        return textureKey;
    }

    public boolean isAir() {
        return this == AIR;
    }
}
