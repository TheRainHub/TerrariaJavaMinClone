package engine;

import util.TileConstants;

/**
 * Represents a 2D camera that defines a viewport into a larger world space.
 * The camera can be positioned anywhere in the world and can center on a target position.
 *
 * It provides methods to convert screen coordinates to world tile coordinates.
 */
public class Camera {

    /** Top-left X coordinate of the camera in the world (in pixels). */
    private double worldX;

    /** Top-left Y coordinate of the camera in the world (in pixels). */
    private double worldY;

    /** Width of the camera view (in pixels). */
    public final int viewWidth;

    /** Height of the camera view (in pixels). */
    public final int viewHeight;

    /**
     * Constructs a camera at the specified world coordinates with a given view size.
     *
     * @param x Initial X position in the world (top-left corner of the viewport).
     * @param y Initial Y position in the world (top-left corner of the viewport).
     * @param viewWidth Width of the camera viewport in pixels.
     * @param viewHeight Height of the camera viewport in pixels.
     */
    public Camera(double x, double y, int viewWidth, int viewHeight) {
        this.worldX = x;
        this.worldY = y;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    /**
     * Centers the camera on a given world position (targetX, targetY).
     * Prevents the camera from moving into negative coordinates.
     *
     * @param targetX World X coordinate to center on.
     * @param targetY World Y coordinate to center on.
     */
    public void centerOn(double targetX, double targetY) {
        worldX = targetX - viewWidth / 2.0;
        worldY = targetY - viewHeight / 2.0;
        if (worldX < 0) worldX = 0;
        if (worldY < 0) worldY = 0;
    }

    /** @return Width of the camera viewport (in pixels). */
    public int getViewWidth() {
        return viewWidth;
    }

    /** @return Height of the camera viewport (in pixels). */
    public int getViewHeight() {
        return viewHeight;
    }

    /** @return World X position of the camera (in pixels). */
    public double getWorldX() {
        return worldX;
    }

    /** @return World Y position of the camera (in pixels). */
    public double getWorldY() {
        return worldY;
    }
}
