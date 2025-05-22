package entity;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import util.TileConstants;
import util.Inventory;
import world.ItemType;
import world.World;

import java.util.Iterator;

/**
 * Represents a single collectible item in the world.
 * <p>
 * Each ItemEntity has a type (with associated sprite),
 * a fixed world position, and a reference to the player's inventory.
 * It will detect when the player overlaps it, add itself to the inventory,
 * and thereafter stop rendering.
 * </p>
 */
public class ItemEntity {
    /** The type of this item (defines ID, sprite path, etc.). */
    private final ItemType type;

    /** The image sprite to draw for this item. */
    private final Image sprite;

    /** World X coordinate (in pixels) of the top-left corner of this item. */
    private final double x;

    /** World Y coordinate (in pixels) of the top-left corner of this item. */
    private final double y;

    /** Has this item already been collected? */
    private boolean collected = false;

    /** Reference to the player's inventory to which this item will be added. */
    private final Inventory inv;

    /**
     * Constructs an ItemEntity at the given world position.
     *
     * @param type the type of item (determines sprite and ID)
     * @param inv  reference to the Inventory to add to upon pickup
     * @param x    world X coordinate (pixels) of this item
     * @param y    world Y coordinate (pixels) of this item
     * @throws RuntimeException if the sprite resource cannot be found
     */
    public ItemEntity(ItemType type, Inventory inv, double x, double y) {
        this.type   = type;
        this.inv    = inv;
        this.x      = x;
        this.y      = y;
        var is = getClass().getResourceAsStream(type.getSpritePath());
        if (is == null) throw new RuntimeException("Sprite not found: " + type.getSpritePath());
        this.sprite = new Image(is);
    }

    /**
     * Called each frame from GameEngine.update().
     * Checks for overlap between this item's bounds and the player's bounds.
     * If they overlap and the item hasn't been collected yet,
     * adds the item to the inventory and marks it collected.
     *
     * @param player the Player instance (for position and size)
     * @param world  the World (unused here, but available for extensions)
     * @return true if the item was just collected this frame, false otherwise
     */
    public boolean update(Player player, World world) {
        // Player bounds
        double px = player.getX();
        double py = player.getY();
        double pw = Player.PLAYER_WIDTH;
        double ph = Player.PLAYER_HEIGHT;

        // Item bounds (1 tile by 1 tile)
        double ix = x;
        double iy = y;
        double iw = TileConstants.TILE_SIZE;
        double ih = TileConstants.TILE_SIZE;

        boolean overlap = px < ix + iw
                && px + pw > ix
                && py < iy + ih
                && py + ph > iy;

        if (overlap && !collected) {
            inv.addItem(type.getId(), 1);
            collected = true;
            return true;
        }
        return false;
    }

    /**
     * Renders the item sprite at its screen position,
     * unless it has already been collected.
     *
     * @param gc  the GraphicsContext to draw on
     * @param cam the Camera used to convert world→screen coordinates
     */
    public void render(GraphicsContext gc, Camera cam) {
        if (collected) return;
        double sx = x - cam.getWorldX();
        double sy = y - cam.getWorldY();
        gc.drawImage(sprite, sx, sy, TileConstants.TILE_SIZE, TileConstants.TILE_SIZE);
    }
}
