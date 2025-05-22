package world;

/**
 * Defines all types of tiles in the game world, each with properties for
 * solidity, breakability, and an optional texture key for rendering.
 * <p>
 * The order of declaration influences rendering/logic where necessary.
 * </p>
 */
public enum TileType {
    /** Empty space; non-solid, non-breakable, no texture. */
    AIR(false, false, null),

    /** Dirt block; solid, breakable, texture "dirt". */
    DIRT(true, true, "dirt"),

    /** Stone block; solid, breakable, texture "stone". */
    STONE(true, true, "stone"),

    /** Grass block on top; solid, breakable, texture "grass_top". */
    GRASS_TOP(true, true, "grass_top"),

    /** Grass block on left edge; solid, breakable, texture "grass_left". */
    GRASS_LEFT(true, true, "grass_left"),

    /** Grass block on right edge; solid, breakable, texture "grass_right". */
    GRASS_RIGHT(true, true, "grass_right"),

    /** Grass block on bottom edge; solid, breakable, texture "grass_bottom". */
    GRASS_BOTTOM(true, true, "grass_bottom"),

    /** Main (stump) part of a tree; non-solid, breakable, texture "tree_main". */
    TREE_MAIN(false, true, "tree_main"),

    /** Tree trunk segment; non-solid, breakable, texture "trunk". */
    TREE_TRUNK(false, true, "trunk"),

    /** Tree leaves; non-solid, breakable, texture "leaves". */
    TREE_LEAVES(false, true, "leaves");

    /** Whether this tile blocks movement and physics. */
    private final boolean solid;
    /** Whether this tile can be destroyed by the player. */
    private final boolean breakable;
    /** Key used to load the tile's PNG texture, or null if none. */
    private final String textureKey;

    /**
     * Constructs a TileType with the given properties.
     *
     * @param solid      true if entities cannot pass through this tile
     * @param breakable  true if the player can destroy this tile
     * @param textureKey the base name of the texture file (without path/extension), or null
     */
    TileType(boolean solid, boolean breakable, String textureKey) {
        this.solid = solid;
        this.breakable = breakable;
        this.textureKey = textureKey;
    }

    /**
     * @return true if this tile blocks movement/physics (cannot be walked through)
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * @return true if the player can break or mine this tile
     */
    public boolean isBreakable() {
        return breakable;
    }

    /**
     * @return the key for loading the tile's texture, e.g. "dirt", or null if no texture
     */
    public String getTextureKey() {
        return textureKey;
    }

    /**
     * @return true if this tile represents empty space (AIR)
     */
    public boolean isAir() {
        return this == AIR;
    }
}
