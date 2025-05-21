package world;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static world.TileType.GRASS_TOP;

public class World {
    private final int width;
    private final int height;
    private TileType[][] tiles;
    private java.util.Map<TileType, javafx.scene.image.Image> textures;


    public World(String resourceName) {
        List<String> lines = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(resourceName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            if (is == null) throw new RuntimeException(resourceName + " not found in resources!");
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        height = lines.size();
        width = lines.get(0).length();
        tiles = new TileType[height][width];

        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                switch (c) {
                    case 'G' -> tiles[y][x] = GRASS_TOP;
                    case '#' -> tiles[y][x] = TileType.DIRT;
                    case 'S' -> tiles[y][x] = TileType.STONE;
                    case 'T' -> tiles[y][x] = TileType.TREE_TRUNK;
                    case 'L' -> tiles[y][x] = TileType.TREE_LEAVES;
                    default  -> tiles[y][x] = TileType.AIR;
                }
            }
        }
    }

    public void setTextures(java.util.Map<TileType, javafx.scene.image.Image> tex) {
        this.textures = tex;
    }

    public void render(GraphicsContext gc, Camera camera) {
        final int tileSize = 16;

        int firstTileX = (int)(camera.getWorldX() / tileSize);
        int firstTileY = (int)(camera.getWorldY() / tileSize);
        int visibleTilesX = (int)Math.ceil(camera.viewWidth / (double)tileSize) + 2;
        int visibleTilesY = (int)Math.ceil(camera.viewHeight / (double)tileSize) + 2;

        for (int tileY = 0; tileY < visibleTilesY; tileY++) {
            for (int tileX = 0; tileX < visibleTilesX; tileX++) {

                int worldX = firstTileX + tileX;
                int worldY = firstTileY + tileY;

                if (worldX >= width || worldY >= height) continue;

                TileType tile = tiles[worldY][worldX];

                // Автозамена верхнего слоя земли
                boolean isTopEmpty = isTopEmpty(worldX, worldY);
                if (tile == TileType.DIRT && isTopEmpty) tile = TileType.GRASS_TOP;
                if (tile == TileType.GRASS_TOP && !isTopEmpty) tile = TileType.DIRT;

                // Координаты на экране
                double screenX = tileX * tileSize - (camera.getWorldX() % tileSize);
                double screenY = tileY * tileSize - (camera.getWorldY() % tileSize);


                Image texture = (textures != null) ? textures.get(tile) : null;

                if (texture != null) {
                    gc.drawImage(texture, screenX, screenY, tileSize, tileSize);
                } else {
                    gc.setFill(getTileColor(tile));
                    if (tile != TileType.AIR) {
                        gc.fillRect(screenX, screenY, tileSize, tileSize);
                    }
                }
            }
        }
    }

    private Paint getTileColor(TileType tile) {
        return switch (tile) {
            case GRASS_TOP -> Color.LIMEGREEN;
            case DIRT      -> Color.SIENNA;
            case STONE     -> Color.DARKGRAY;
            default        -> Color.TRANSPARENT;
        };
    }

    public int getSurfaceY(int x) {
        for (int y = 0; y < height; y++) {
            if (tiles[y][x] == TileType.AIR) {
                return y;
            }
        }
        return 0;
    }

    public void mineTile(int x, int y) {
        if (inBounds(x, y) && tiles[y][x] != TileType.AIR) tiles[y][x] = TileType.AIR;
    }
    public void placeTile(int x, int y, TileType type) {
        if (inBounds(x, y) && tiles[y][x] == TileType.AIR) tiles[y][x] = type;
    }
    private boolean inBounds(int x, int y) { return x >= 0 && x < width && y >= 0 && y < height; }

    private boolean isTopEmpty(int x, int y) {
        return !isSolid(x, y-1);
    }

    public boolean isSolid(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return true;
        return tiles[y][x].isSolid();
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
