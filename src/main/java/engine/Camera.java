package engine;

import util.TileConstants;

public class Camera {
    private double worldX, worldY;
    public final int viewWidth;
    public final int viewHeight;

    public Camera(double x, double y, int viewWidth, int viewHeight) {
        this.worldX = x;
        this.worldY = y;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public void centerOn(double targetX, double targetY) {
        worldX = targetX - viewWidth / 2.0;
        worldY = targetY - viewHeight / 2.0;
        if (worldX < 0) worldX = 0;
        if (worldY < 0) worldY = 0;

    }

    public double getWorldX() { return worldX; }
    public double getWorldY() { return worldY; }

    public int screenToWorldTileX(double screenX) {
        return (int) ((worldX + screenX) / TileConstants.TILE_SIZE);
    }
    public int screenToWorldTileY(double screenY) {
        return (int) ((worldY + screenY) / TileConstants.TILE_SIZE);
    }

}
