package world;

public class World {
    private final TileType[][] tiles;
    private final int width, height;

    public World(TileType[][] tiles) {
        this.tiles = tiles;
        this.height = tiles.length;
        this.width = tiles[0].length;
    }

    public void mineTile(int x, int y) {
        setTile(x, y, TileType.AIR);
    }

    public void placeTile(int x, int y, TileType type) {
        setTile(x, y, type);
    }

    public void setTile(int x, int y, TileType type) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        tiles[y][x] = type;
    }
    public TileType[][] getTiles() {
        return tiles;
    }
    public boolean isSolid(int x, int y) {
        if (x < 0 || y < 0 || y >= tiles.length || x >= tiles[0].length) return true;
        return tiles[y][x].isSolid();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getSurfaceY(int x) {
        for (int y = 0; y < height; y++) {
            if (tiles[y][x] != TileType.AIR)
                return y;
        }
        return height - 1;
    }
}
