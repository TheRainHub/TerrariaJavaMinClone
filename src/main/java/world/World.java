package world;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class World {
    private final int width;
    private final int height;
    private TileType[][] tiles;

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
                tiles[y][x] = (c == '#') ? TileType.DIRT : TileType.AIR;
            }
        }
    }


    public void render(GraphicsContext gc, Camera camera) {
        int tileSize = 32;
        int startX = (int)(camera.getWorldX() / tileSize);
        int startY = (int)(camera.getWorldY() / tileSize);
        int tilesX = (int)Math.ceil(camera.viewWidth / (double)tileSize) + 2;
        int tilesY = (int)Math.ceil(camera.viewHeight / (double)tileSize) + 2;

        for (int y = 0; y < tilesY; y++) {
            int wy = startY + y;
            if (wy >= height) continue;
            for (int x = 0; x < tilesX; x++) {
                int wx = startX + x;
                if (wx >= width) continue;
                TileType tile = tiles[wy][wx];
                double sx = x * tileSize - (camera.getWorldX() % tileSize);
                double sy = y * tileSize - (camera.getWorldY() % tileSize);
                switch (tile) {
                    case DIRT:
                        gc.setFill(Color.SIENNA); break;
                    case AIR:
                        gc.setFill(Color.TRANSPARENT); break;
                }
                if (tile != TileType.AIR)
                    gc.fillRect(sx, sy, tileSize, tileSize);
            }
        }
    }

    public void mineTile(int x, int y) {
        if (inBounds(x, y) && tiles[y][x] != TileType.AIR) tiles[y][x] = TileType.AIR;
    }
    public void placeTile(int x, int y, TileType type) {
        if (inBounds(x, y) && tiles[y][x] == TileType.AIR) tiles[y][x] = type;
    }
    private boolean inBounds(int x, int y) { return x >= 0 && x < width && y >= 0 && y < height; }

    public boolean isSolid(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return true; // За границами — всегда solid
        return tiles[y][x].isSolid(); // Используем метод TileType
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
