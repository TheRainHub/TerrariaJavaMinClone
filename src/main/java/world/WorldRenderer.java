package world;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import util.TileConstants;

import java.util.Map;

public class WorldRenderer {
    private final Map<TileType, Image> textures;

    public WorldRenderer(Map<TileType, Image> textures) {
        this.textures = textures;
    }

    private int neighbourMask(int x, int y, TileType[][] tiles) {
        int m = 0;
        if (y > 0                  && tiles[y-1][x] == TileType.AIR) m |= 1; // N
        if (x < tiles[0].length-1 && tiles[y][x+1] == TileType.AIR) m |= 2; // E
        if (y < tiles.length-1     && tiles[y+1][x] == TileType.AIR) m |= 4; // S
        if (x > 0                  && tiles[y][x-1] == TileType.AIR) m |= 8; // W
        return m;
    }

    public void render(GraphicsContext gc, Camera camera, TileType[][] tiles) {
        int ts = TileConstants.TILE_SIZE;
        double worldX = camera.getWorldX(), worldY = camera.getWorldY();
        int startX = (int)(worldX / ts), startY = (int)(worldY / ts);
        int endX   = (int)((worldX + camera.getViewWidth())  / ts) + 1;
        int endY   = (int)((worldY + camera.getViewHeight()) / ts) + 1;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if (y<0||y>=tiles.length||x<0||x>=tiles[0].length) continue;

                TileType t = tiles[y][x];
                // только под DIRT или базовой GRASS делаем маску
                if (t == TileType.DIRT || t == TileType.GRASS_TOP) {
                    int mask = neighbourMask(x, y, tiles);
                    // priority: East (2) **before** North (1)
                    if      ((mask & 2) != 0) t = TileType.GRASS_RIGHT;   // East empty → right‐edge
                    else if ((mask & 8) != 0) t = TileType.GRASS_LEFT;    // West empty → left‐edge
                    else if ((mask & 1) != 0) t = TileType.GRASS_TOP;     // North empty → top
                    else if ((mask & 4) != 0) t = TileType.GRASS_BOTTOM;  // South empty → bottom
                    else                      t = TileType.DIRT;         // fully buried
                }
                Image tex = textures.get(t);
                double dx = x*ts - worldX, dy = y*ts - worldY;
                if (tex != null) gc.drawImage(tex, dx, dy, ts, ts);
                else if (t != TileType.AIR) {
                    // fallback: цветная заливка
                    switch (t) {
                        case GRASS_TOP, GRASS_LEFT, GRASS_RIGHT, GRASS_BOTTOM -> gc.setFill(Color.LIMEGREEN);
                        case DIRT      -> gc.setFill(Color.SIENNA);
                        case STONE     -> gc.setFill(Color.DARKGRAY);
                        default        -> gc.setFill(Color.TRANSPARENT);
                    }
                    gc.fillRect(dx, dy, ts, ts);
                }
            }
        }
    }
}
