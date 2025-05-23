package entity;

import world.World;
import engine.Camera;
import util.TileConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import world.ItemType;

/**
 * Represents the player character in the game world.
 * <p>
 * Handles movement physics (gravity, jumping, horizontal movement), collision detection,
 * animation frame selection (idle, running, jumping), equipment state (e.g., baton equipped),
 * and rendering of the player sprite.
 * </p>
 */
public class Player {

    /** Width of the player sprite in pixels. */
    public static final int PLAYER_WIDTH  = 34;
    /** Height of the player sprite in pixels. */
    public static final int PLAYER_HEIGHT = 42;

    private static final double SPEED       = 150;
    private static final double JUMP_POWER  = -500;
    private static final double GRAVITY     = 900;
    private static final double MAX_FALL    = 600;

    // Animation frames for standard and baton-equipped states
    private final Image[] idleLeftFrames   = new Image[4];
    private final Image[] idleRightFrames  = new Image[4];
    private final Image[] runLeftFrames    = new Image[4];
    private final Image[] runRightFrames   = new Image[4];
    private final Image[] jumpLeftFrames   = new Image[3];
    private final Image[] jumpRightFrames  = new Image[3];
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

    /**
     * Constructs a new Player at the specified initial position and loads animations.
     *
     * @param x initial world x-coordinate of the player
     * @param y initial world y-coordinate of the player
     */
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        loadAnimations();
    }

    /**
     * Loads all animation frames for the player from resource files.
     * <p>
     * Throws RuntimeException if any resource is missing.
     * </p>
     */
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
        for (int i = 0; i < 3; i++) {
            String n = String.valueOf(i + 1);
            jumpLeftFrames[i]   = load("/animation/JonkleJumpLeft"          + n + ".png");
            jumpRightFrames[i]  = load("/animation/JonkleJumpRight"         + n + ".png");
        }
    }

    /**
     * Loads an image from the given resource path.
     *
     * @param path resource path to the image file
     * @return the loaded Image
     * @throws RuntimeException if the resource cannot be found
     */
    private Image load(String path) {
        var is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new RuntimeException("Animation file not found: " + path);
        }
        return new Image(is);
    }

    /**
     * Equips or unequips the baton and resets idle animation state.
     *
     * @param eq true to equip baton, false to unequip
     */
    public void setBatonEquipped(boolean eq) {
        this.hasBaton = eq;
        this.frameIndex = 0;
        this.frameTimer = 0;
    }

    /**
     * Starts moving the player to the left and faces left.
     */
    public void moveLeft()       { movingLeft = true;  facingRight = false; }
    /**
     * Starts moving the player to the right and faces right.
     */
    public void moveRight()      { movingRight = true; facingRight = true;  }
    /**
     * Stops leftward movement.
     */
    public void stopMovingLeft() { movingLeft = false; }
    /**
     * Stops rightward movement.
     */
    public void stopMovingRight(){ movingRight = false; }

    /**
     * Initiates a jump if the player is on the ground.
     * <p>
     * Sets vertical velocity and resets jump animation.
     * </p>
     */
    public void jump() {
        if (onGround) {
            vy = JUMP_POWER;
            onGround = false;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    /**
     * Updates player physics, handles input state, collision, and animation.
     * <p>
     * Should be called once per frame with the elapsed time.
     * </p>
     *
     * @param dt    time elapsed since last frame in seconds
     * @param world the game world for collision checks
     */
    public void update(double dt, World world) {
        boolean prevOnGround = this.onGround;
        // Horizontal movement
        if      (movingLeft)  vx = -SPEED;
        else if (movingRight) vx =  SPEED;
        else                  vx =  0;

        boolean justStartedJump = !onGround && prevOnGround;
        if (justStartedJump) {
            frameIndex = 0;
            frameTimer = 0;
        }
        // Apply gravity
        vy += GRAVITY * dt;
        vy = Math.max(-MAX_FALL, Math.min(MAX_FALL, vy));

        // Horizontal collision
        double nx = x + vx * dt;
        if (!collides(nx, y, world)) x = nx; else vx = 0;

        // Vertical collision
        double ny = y + vy * dt;
        if (!collides(x, ny, world)) {
            y = ny;
            onGround = false;
        } else {
            if (vy > 0) onGround = true;
            vy = 0;
        }

        boolean justLanded      =  onGround && !prevOnGround;
        if (justStartedJump) {
            frameIndex = 0;
            frameTimer = 0;
        } else if (justLanded) {
            frameIndex = jumpLeftFrames.length - 1;
            frameTimer = 0;
        }

        // Animation frame selection
        if (!onGround) {
            frameTimer += dt;
            if (frameTimer >= JUMP_FRAME_DURATION) {
                frameTimer -= JUMP_FRAME_DURATION;
                frameIndex = Math.min(frameIndex + 1, jumpLeftFrames.length - 1);
            }
        } else if (movingLeft || movingRight) {
            frameTimer += dt;
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % runLeftFrames.length;
            }
        } else {
            frameTimer += dt;
            int len = hasBaton ? idleLeftBaton.length : idleLeftFrames.length;
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % len;
            }
        }
    }

    /**
     * Equips an item, resetting animation state. Currently supports baton only.
     *
     * @param item the item type to equip
     */
    public void setEquippedItem(ItemType item) {
        this.frameIndex = 0;
        this.frameTimer = 0;
        this.hasBaton = (item == ItemType.BATON);
    }

    /**
     * Checks collision of the player's bounding box with solid tiles.
     *
     * @param px    proposed x-coordinate
     * @param py    proposed y-coordinate
     * @param world the game world to query for solidity
     * @return true if collision would occur, false otherwise
     */
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

    /**
     * Renders the player sprite at the current position with the correct frame.
     *
     * @param gc  the graphics context for drawing
     * @param cam the camera for world-to-screen translation
     */
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

    /**
     * Sets the player's position in world coordinates.
     *
     * @param x new world x-coordinate
     * @param y new world y-coordinate
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the player's current world x-coordinate.
     *
     * @return x-coordinate of the player
     */
    public double getX() { return x; }

    /**
     * Returns the player's current world y-coordinate.
     *
     * @return y-coordinate of the player
     */
    public double getY() { return y; }
}
