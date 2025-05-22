package entity;

import world.World;
import engine.Camera;
import util.TileConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import world.ItemType;

/**
 * Represents the player character in the world.
 * <p>
 * Handles physics (gravity, jumping, horizontal movement), collision with solid tiles,
 * animation frame selection (idle, running, jumping), baton-equipped idle, and rendering.
 * </p>
 */
public class Player {

    public static final int PLAYER_WIDTH  = 34;
    public static final int PLAYER_HEIGHT = 42;

    private static final double SPEED       = 150;
    private static final double JUMP_POWER  = -500;
    private static final double GRAVITY     = 900;
    private static final double MAX_FALL    = 600;

    // standard animations
    private final Image[] idleLeftFrames   = new Image[4];
    private final Image[] idleRightFrames  = new Image[4];
    private final Image[] runLeftFrames    = new Image[4];
    private final Image[] runRightFrames   = new Image[4];
    private final Image[] jumpLeftFrames   = new Image[5];
    private final Image[] jumpRightFrames  = new Image[5];

    // baton-in-hand idle animations
    private final Image[] idleLeftBaton    = new Image[4];
    private final Image[] idleRightBaton   = new Image[4];
    private boolean hasBaton = false;

    private int   frameIndex    = 0;
    private double frameTimer   = 0;
    private static final double FRAME_DURATION       = 0.25;
    private static final double JUMP_FRAME_DURATION  = 0.13;

    private boolean facingRight = true;
    private boolean onGround    = true;

    private double x, y, vx, vy;
    private boolean movingLeft, movingRight;


    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        loadAnimations();
    }

    private void loadAnimations() {
        for (int i = 0; i < 4; i++) {
            String n = String.valueOf(i + 1);
            idleLeftFrames[i]   = load("/animation/JonkleAnimatedLeftStand"  + n + ".png");
            idleRightFrames[i]  = load("/animation/JonkleAnimatedStandRight"  + n + ".png");
            runLeftFrames[i]    = load("/animation/JonkleRunLeft"            + n + ".png");
            runRightFrames[i]   = load("/animation/JonkleRunRight"           + n + ".png");
            idleLeftBaton[i]    = load("/animation/JonkleStandLeftWithBaton"  + n + ".png");
            idleRightBaton[i]   = load("/animation/JonkleStandRightWithBaton" + n + ".png");
        }
        for (int i = 0; i < 5; i++) {
            String n = String.valueOf(i + 1);
            jumpLeftFrames[i]   = load("/animation/JonkleJumpLeft"          + n + ".png");
            jumpRightFrames[i]  = load("/animation/JonkleJumpRight"         + n + ".png");

        }
    }

    private Image load(String path) {
        var is = getClass().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("Animation file not found: " + path);
        return new Image(is);
    }

    /**
     * Equip or unequip the baton, resetting the idle animation.
     */
    public void setBatonEquipped(boolean eq) {
        this.hasBaton = eq;
        this.frameIndex = 0;
        this.frameTimer = 0;
    }

    public void moveLeft()       { movingLeft = true;  facingRight = false; }
    public void moveRight()      { movingRight = true; facingRight = true;  }
    public void stopMovingLeft() { movingLeft = false; }
    public void stopMovingRight(){ movingRight = false; }

    public void jump() {
        if (onGround) {
            vy = JUMP_POWER;
            onGround = false;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    public void update(double dt, World world) {
        // horizontal movement
        if      (movingLeft)  vx = -SPEED;
        else if (movingRight) vx =  SPEED;
        else                  vx =  0;

        // apply gravity
        vy += GRAVITY * dt;
        vy = Math.max(-MAX_FALL, Math.min(MAX_FALL, vy));

        // move & collide horizontally
        double nx = x + vx * dt;
        if (!collides(nx, y, world)) x = nx; else vx = 0;

        // move & collide vertically
        double ny = y + vy * dt;
        if (!collides(x, ny, world)) {
            y = ny;
            onGround = false;
        } else {
            if (vy > 0) onGround = true;
            vy = 0;
        }

        // animation timing
        frameTimer += dt;
        if (!onGround) {
            // jump animation
            if (frameIndex < jumpLeftFrames.length - 1
                    && frameTimer >= JUMP_FRAME_DURATION) {
                frameTimer -= JUMP_FRAME_DURATION;
                frameIndex++;
            }
        } else if (movingLeft || movingRight) {
            // running animation
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % runLeftFrames.length;
            }
        } else {
            // idle or baton idle
            int len = hasBaton ? idleLeftBaton.length : idleLeftFrames.length;
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % len;
            }
        }
    }

    public void setEquippedItem(ItemType item) {
        // сброс анимации
        this.frameIndex = 0;
        this.frameTimer = 0;

        switch (item) {
            case BATON:
                this.hasBaton = true;
                break;
            default:
                // если в будущем появятся другие предметы, здесь будет удобное место
                this.hasBaton = false;
                break;
        }
    }

    private boolean collides(double px, double py, World world) {
        int ts = TileConstants.TILE_SIZE;
        int left   = (int)(px                   / ts);
        int right  = (int)((px + PLAYER_WIDTH-1) / ts);
        int top    = (int)(py                   / ts);
        int bottom = (int)((py + PLAYER_HEIGHT-1)/ ts);
        return world.isSolid(left, top)
                || world.isSolid(right, top)
                || world.isSolid(left, bottom)
                || world.isSolid(right, bottom);
    }

    public void render(GraphicsContext gc, Camera cam) {
        double sx = x - cam.getWorldX();
        double sy = y - cam.getWorldY();

        Image[] frames;
        if (!onGround) {
            frames = facingRight ? jumpRightFrames : jumpLeftFrames;
        } else if (movingLeft || movingRight) {
            frames = facingRight ? runRightFrames : runLeftFrames;
        } else if (hasBaton) {
            frames = facingRight ? idleRightBaton : idleLeftBaton;
        } else {
            frames = facingRight ? idleRightFrames : idleLeftFrames;
        }

        int idx = Math.max(0, Math.min(frameIndex, frames.length - 1));
        gc.drawImage(frames[idx], sx, sy, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
