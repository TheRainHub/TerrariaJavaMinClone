package world;

/**
 * Represents the 2D tile-based world, providing access to tiles,
 * modification (mining/placing), and utility methods for collision and surface queries.
 */
public class World {
    private final TileType[][] tiles;
    private final int width, height;

    /**
     * Constructs a World from a pre-built 2D array of TileType.
     *
     * @param tiles a non‐empty 2D array [row=y][col=x] of tiles
     */
    public World(TileType[][] tiles) {
        this.tiles  = tiles;
        this.height = tiles.length;
        this.width  = tiles[0].length;
    }

    /**
     * Returns the tile at (x,y), or AIR if (x,y) is outside the map bounds.
     *
     * @param x tile X coordinate
     * @param y tile Y coordinate
     * @return the TileType at that position, or TileType.AIR if out of bounds
     */
    public TileType getTile(int x, int y) {
        if (x < 0 || y < 0 || y >= height || x >= width) return TileType.AIR;
        return tiles[y][x];
    }

    /**
     * Sets the tile at (x,y) to the given type, if within bounds.
     *
     * @param x    tile X coordinate
     * @param y    tile Y coordinate
     * @param type the TileType to place
     */
    public void setTile(int x, int y, TileType type) {
        if (x < 0 || y < 0 || y >= height || x >= width) return;
        tiles[y][x] = type;
    }

    /**
     * “Mines” (destroys) the tile at (x,y).
     * <ul>
     *   <li>If it’s not part of a tree, simply replaces it with AIR.</li>
     *   <li>If it’s a tree tile, finds the TREE_MAIN stump in that column,
     *       removes the trunk and leaves around it, leaving only the stump.</li>
     * </ul>
     *
     * @param x tile X coordinate
     * @param y tile Y coordinate
     */
    public void mineTile(int x, int y) {
        TileType t = getTile(x, y);
        // if not a tree, just clear
        if (t != TileType.TREE_MAIN &&
                t != TileType.TREE_TRUNK &&
                t != TileType.TREE_LEAVES) {
            setTile(x, y, TileType.AIR);
            return;
        }

        // find stump (TREE_MAIN) downward
        int baseY = y;
        while (baseY < height && getTile(x, baseY) != TileType.TREE_MAIN) {
            baseY++;
        }
        if (baseY >= height) {
            // no stump found, just clear this tile
            setTile(x, y, TileType.AIR);
            return;
        }

        int trunkHeight = 4, leafRadius = 3;
        // remove trunk above stump
        for (int dy = 1; dy <= trunkHeight; dy++) {
            int ty = baseY - dy;
            if (getTile(x, ty) == TileType.TREE_TRUNK) {
                setTile(x, ty, TileType.AIR);
            }
        }
        // remove leaves in radius around top of trunk
        int cy = baseY - trunkHeight;
        for (int dx = -leafRadius; dx <= leafRadius; dx++) {
            for (int dy = -leafRadius; dy <= leafRadius; dy++) {
                int tx = x + dx, ty = cy + dy;
                if (getTile(tx, ty) == TileType.TREE_LEAVES) {
                    setTile(tx, ty, TileType.AIR);
                }
            }
        }

        // leave only the stump
        setTile(x, baseY, TileType.TREE_MAIN);
    }

    /**
     * Places a tile of the given type at (x,y).
     *
     * @param x    tile X coordinate
     * @param y    tile Y coordinate
     * @param type the TileType to place
     */
    public void placeTile(int x, int y, TileType type) {
        setTile(x, y, type);
    }

    /**
     * Returns whether the tile at (x,y) is solid for collision.
     * <ul>
     *   <li>Out of vertical bounds (above/below) counts as solid;</li>
     *   <li>Out of horizontal bounds counts as empty (allows exit);</li>
     *   <li>Otherwise, delegates to TileType.isSolid().</li>
     * </ul>
     *
     * @param x tile X coordinate
     * @param y tile Y coordinate
     * @return true if this position blocks movement
     */
    public boolean isSolid(int x, int y) {
        if (y < 0 || y >= height) return true;
        if (x < 0 || x >= width) return false;
        return tiles[y][x].isSolid();
    }

    /** @return map width in tiles */
    public int getWidth()  { return width; }
    /** @return map height in tiles */
    public int getHeight() { return height; }

    /**
     * Finds the first non-AIR tile in column x, scanning from the top (y=0).
     * If the entire column is AIR, returns the bottommost row.
     *
     * @param x tile X coordinate
     * @return the Y coordinate of the surface
     */
    public int getSurfaceY(int x) {
        for (int y = 0; y < height; y++) {
            if (tiles[y][x] != TileType.AIR) return y;
        }
        return height - 1;
    }

    /**
     * Returns the raw 2D tile array for iteration or export.
     *
     * @return the tiles[y][x] array
     */
    public TileType[][] getTiles() {
        return tiles;
    }
}
