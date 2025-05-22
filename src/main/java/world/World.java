package world;

public class World {
    private final TileType[][] tiles;
    private final int width, height;

    public World(TileType[][] tiles) {
        this.tiles  = tiles;
        this.height = tiles.length;
        this.width  = tiles[0].length;
    }

    /** Новая утилита — возвращает тайл или AIR, если вышли за границы */
    public TileType getTile(int x, int y) {
        if (x < 0 || y < 0 || y >= height || x >= width) return TileType.AIR;
        return tiles[y][x];
    }

    /** Ставит тайл, если в пределах карты */
    public void setTile(int x, int y, TileType type) {
        if (x < 0 || y < 0 || y >= height || x >= width) return;
        tiles[y][x] = type;
    }

    public void mineTile(int x, int y) {
        TileType t = getTile(x, y);
        // если не дерево — просто ломаем
        if (t != TileType.TREE_MAIN &&
                t != TileType.TREE_TRUNK &&
                t != TileType.TREE_LEAVES) {
            setTile(x, y, TileType.AIR);
            return;
        }

        // 1) найдем основание (TREE_MAIN) в колонке
        int baseY = y;
        while (baseY < height && getTile(x, baseY) != TileType.TREE_MAIN) {
            baseY++;
        }
        if (baseY >= height) {
            // не нашли — просто очистим этот блок
            setTile(x, y, TileType.AIR);
            return;
        }

        // 2) Убираем весь ствол над пенёчком и крону
        int trunkHeight = 4, leafRadius = 3;

        // 2a) ствол
        for (int dy = 1; dy <= trunkHeight; dy++) {
            int ty = baseY - dy;
            if (getTile(x, ty) == TileType.TREE_TRUNK) {
                setTile(x, ty, TileType.AIR);
            }
        }
        // 2b) листья
        int cy = baseY - trunkHeight;
        for (int dx = -leafRadius; dx <= leafRadius; dx++) {
            for (int dy = -leafRadius; dy <= leafRadius; dy++) {
                int tx = x + dx, ty = cy + dy;
                if (getTile(tx, ty) == TileType.TREE_LEAVES) {
                    setTile(tx, ty, TileType.AIR);
                }
            }
        }

        // 3) Оставляем только пенёк
        setTile(x, baseY, TileType.TREE_MAIN);
    }

    public void placeTile(int x, int y, TileType type) {
        setTile(x, y, type);
    }

    public boolean isSolid(int x, int y) {
        if (x < 0 || y < 0 || y >= height || x >= width) return true;
        return tiles[y][x].isSolid();
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public int getSurfaceY(int x) {
        for (int y = 0; y < height; y++) {
            if (tiles[y][x] != TileType.AIR) return y;
        }
        return height - 1;
    }

    /** Если вам нужно вернуть сразу весь массив */
    public TileType[][] getTiles() {
        return tiles;
    }
}
