package world;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import util.TileConstants;

import java.util.Map;

/**
 * Renders a 2D tile-based world to a JavaFX Canvas.
 * <p>
 * Only the tiles visible within the camera’s viewport are drawn.
 * Supports autotiling of dirt/grass edges by examining neighboring tiles.
 * Falls back to colored rectangles if a texture is unavailable.
 * </p>
 */
public class WorldRenderer {
    /** Preloaded textures for each TileType. */
    private final Map<TileType, Image> textures;

    /**
     * @param textures a mapping from TileType to its Image texture
     */
    public WorldRenderer(Map<TileType, Image> textures) {
        this.textures = textures;
    }

    /**
     * Computes a 4-bit mask indicating which of the four cardinal neighbors of (x,y)
     * are AIR.  Bits: 1=N, 2=E, 4=S, 8=W.
     *
     * @param x     tile X coordinate
     * @param y     tile Y coordinate
     * @param tiles full tile grid
     * @return bitmask of empty (AIR) neighbors
     */
    private int neighbourMask(int x, int y, TileType[][] tiles) {
        int m = 0;
        if (y > 0                  && tiles[y-1][x] == TileType.AIR) m |= 1; // North
        if (x < tiles[0].length-1 && tiles[y][x+1] == TileType.AIR) m |= 2; // East
//        if (y < tiles.length-1     && tiles[y+1][x] == TileType.AIR) m |= 4; // South
        if (x > 0                  && tiles[y][x-1] == TileType.AIR) m |= 8; // West
        return m;
    }

    /**
     * Draws all tiles within the camera’s view onto the provided GraphicsContext.
     * <p>
     * - Computes the tile range from camera world coordinates and tile size.<br>
     * - For DIRT and base GRASS_TOP tiles, applies an edge‐mask to select
     *   the correct grass‐edge variant (LEFT, RIGHT, TOP, BOTTOM).<br>
     * - Attempts to draw the texture; if missing and tile ≠ AIR, draws a colored rectangle.
     * </p>
     *
     * @param gc       JavaFX GraphicsContext to draw on
     * @param camera   the Camera defining the viewport
     * @param tiles    full tile grid of the world
     */
    public void render(GraphicsContext gc, Camera camera, TileType[][] tiles) {
        int ts = TileConstants.TILE_SIZE;
        double worldX = camera.getWorldX(), worldY = camera.getWorldY();

        // compute visible tile bounds (plus one extra to cover partial tiles)
        int startX = (int)(worldX / ts), startY = (int)(worldY / ts);
        int endX   = (int)((worldX + camera.getViewWidth())  / ts) + 1;
        int endY   = (int)((worldY + camera.getViewHeight()) / ts) + 1;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                // skip out‐of‐bounds
                if (y < 0 || y >= tiles.length || x < 0 || x >= tiles[0].length) continue;

                TileType t = tiles[y][x];

                // autotile dirt/grass edges
                if (t == TileType.DIRT || t == TileType.GRASS_TOP) {
                    int mask = neighbourMask(x, y, tiles);
                    if      ((mask & 2) != 0) t = TileType.GRASS_RIGHT;
                    else if ((mask & 8) != 0) t = TileType.GRASS_LEFT;
                    else if ((mask & 1) != 0) t = TileType.GRASS_TOP;
                    else if ((mask & 4) != 0) t = TileType.GRASS_BOTTOM;
                    else                      t = TileType.DIRT;
                }

                Image tex = textures.get(t);
                double dx = x * ts - worldX, dy = y * ts - worldY;
                if (tex != null) {
                    gc.drawImage(tex, dx, dy, ts, ts);
                } else if (t != TileType.AIR) {
                    // fallback color fill for missing textures
                    switch (t) {
                        case GRASS_TOP, GRASS_LEFT, GRASS_RIGHT, GRASS_BOTTOM ->
                                gc.setFill(Color.LIMEGREEN);
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
