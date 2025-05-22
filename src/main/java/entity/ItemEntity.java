package entity;

import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import util.TileConstants;
import util.Inventory;
import world.ItemType;
import world.World;

public class ItemEntity {
    private final ItemType type;
    private final Image    sprite;
    private final double   x, y;
    private       boolean  collected = false;
    private final Inventory inv;

    public ItemEntity(ItemType type, Inventory inv, double x, double y) {
        this.type  = type;
        this.inv   = inv;
        this.x     = x;
        this.y     = y;
        var is = getClass().getResourceAsStream(type.getSpritePath());
        if (is == null) throw new RuntimeException("Sprite not found: " + type.getSpritePath());
        this.sprite = new Image(is);
    }

    /**
     * Вызывается из GameEngine.update; возвращает true, если предмет только что подобран.
     */
    public boolean update(Player player, World world) {
        if (collected) return true;

        // используем коллизию «прямоугольник — прямоугольник»
        double px = player.getX();
        double py = player.getY();
        double pw = Player.PLAYER_WIDTH;
        double ph = Player.PLAYER_HEIGHT;
        double ix = x, iy = y, iw = TileConstants.TILE_SIZE, ih = TileConstants.TILE_SIZE;

        boolean overlap = px < ix+iw && px+pw > ix
                && py < iy+ih && py+ph > iy;
        if (overlap) {
            inv.addItem(type.getId(), 1);
            collected = true;
            return true;
        }
        return false;
    }

    public void render(GraphicsContext gc, Camera cam) {
        if (collected) return;
        double sx = x - cam.getWorldX();
        double sy = y - cam.getWorldY();
        gc.drawImage(sprite, sx, sy, TileConstants.TILE_SIZE, TileConstants.TILE_SIZE);
    }
}
