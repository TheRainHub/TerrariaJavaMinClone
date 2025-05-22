package entity;

import world.World;
import engine.Camera;
import util.TileConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Represents the player character in the world.
 * <p>
 * Handles physics (gravity, jumping, horizontal movement), collision with solid tiles,
 * animation frame selection (idle, running, jumping), and rendering.
 * </p>
 */
public class Player {

    /** Width of the player sprite (pixels). */
    public static final int PLAYER_WIDTH  = 34;
    /** Height of the player sprite (pixels). */
    public static final int PLAYER_HEIGHT = 42;

    /** Horizontal movement speed (pixels per second). */
    private static final double SPEED       = 150;
    /** Initial jump velocity (negative = upward, pixels per second). */
    private static final double JUMP_POWER  = -500;
    /** Downward acceleration due to gravity (pixels per second²). */
    private static final double GRAVITY     = 900;
    /** Maximum absolute vertical velocity (pixels per second). */
    private static final double MAX_FALL    = 600;

    // Animation frames for each state and direction
    private final Image[] idleLeftFrames   = new Image[4];
    private final Image[] idleRightFrames  = new Image[4];
    private final Image[] runLeftFrames    = new Image[4];
    private final Image[] runRightFrames   = new Image[4];
    private final Image[] jumpLeftFrames   = new Image[5];
    private final Image[] jumpRightFrames  = new Image[5];

    /** Current animation frame index. */
    private int   frameIndex    = 0;
    /** Timer accumulated since last frame switch. */
    private double frameTimer   = 0;
    /** Duration between run/idle frames (seconds). */
    private static final double FRAME_DURATION     = 0.25;
    /** Duration between jump frames (seconds). */
    private static final double JUMP_FRAME_DURATION = 0.13;

    /** True if the player is facing right, false if facing left. */
    private boolean facingRight = true;
    /** True if the player was on the ground at end of previous update. */
    private boolean wasOnGround = true;

    /** World coordinates (pixels). */
    private double x, y;
    /** Current velocity (pixels per second). */
    private double vx, vy;
    /** Input flags and ground state. */
    private boolean movingLeft, movingRight, onGround;

    /**
     * Constructs a Player at the given world coordinates and loads animations.
     *
     * @param x initial X coordinate (pixels)
     * @param y initial Y coordinate (pixels)
     */
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        loadAnimations();
    }

    /** Loads all animation frames from resources into their respective arrays. */
    private void loadAnimations() {
        for (int i = 0; i < 4; i++) {
            String n = Integer.toString(i + 1);
            idleLeftFrames[i]  = load("/animation/JonkleAnimatedLeftStand"  + n + ".png");
            idleRightFrames[i] = load("/animation/JonkleAnimatedStandRight" + n + ".png");
            runLeftFrames[i]   = load("/animation/JonkleRunLeft"            + n + ".png");
            runRightFrames[i]  = load("/animation/JonkleRunRight"           + n + ".png");
        }
        for (int i = 0; i < 5; i++) {
            String n = Integer.toString(i + 1);
            jumpLeftFrames[i]  = load("/animation/JonkleJumpLeft"  + n + ".png");
            jumpRightFrames[i] = load("/animation/JonkleJumpRight" + n + ".png");
        }
    }

    /**
     * Helper to load an Image from a resource path.
     *
     * @param path resource path to PNG file
     * @return loaded Image
     * @throws RuntimeException if resource not found
     */
    private Image load(String path) {
        var is = getClass().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("Animation file not found: " + path);
        return new Image(is);
    }

    /** Start moving left; sets facing direction accordingly. */
    public void moveLeft()       { movingLeft = true;  facingRight = false; }
    /** Start moving right; sets facing direction accordingly. */
    public void moveRight()      { movingRight = true; facingRight = true;  }
    /** Stop moving left. */
    public void stopMovingLeft() { movingLeft = false; }
    /** Stop moving right. */
    public void stopMovingRight(){ movingRight = false; }
    /**
     * Initiates a jump if on the ground by setting upward velocity.
     * Resets jump animation.
     */
    public void jump() {
        if (onGround) {
            vy = JUMP_POWER;
            wasOnGround = true;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    /**
     * Updates physics (movement, gravity, collision) and animation state.
     *
     * @param dt    time elapsed since last frame (seconds)
     * @param world the World for collision checks
     */
    public void update(double dt, World world) {
        boolean prevOnGround = onGround;

        // Horizontal velocity from input
        if      (movingLeft)  vx = -SPEED;
        else if (movingRight) vx =  SPEED;
        else                  vx =  0;

        // Apply gravity
        vy += GRAVITY * dt;
        vy = Math.max(-MAX_FALL, Math.min(MAX_FALL, vy));

        // Move and collide
        updatePosition(dt, world);

        // Detect jump start / landing
        boolean justStartedJump = !onGround && prevOnGround;
        boolean justLanded      = onGround && !prevOnGround;

        // Reset animation if jump just started
        if (justStartedJump) {
            frameIndex = 0;
            frameTimer = 0;
        }

        // Advance jump frames
        if (!onGround) {
            frameTimer += dt;
            if (frameIndex < 2 && frameTimer >= JUMP_FRAME_DURATION) {
                frameTimer -= JUMP_FRAME_DURATION;
                frameIndex++;
            }
        }
        // After landing, show landing frames
        else if (justLanded) {
            frameIndex = 3;
            frameTimer = 0;
        }
        else if (frameIndex == 3) {
            frameTimer += dt;
            if (frameTimer >= JUMP_FRAME_DURATION) {
                frameTimer -= JUMP_FRAME_DURATION;
                frameIndex = 4; // return pose
            }
        }
        // Running animation
        else if (movingLeft || movingRight) {
            frameTimer += dt;
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % runLeftFrames.length;
            }
        }
        // Idle animation
        else {
            frameTimer += dt;
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % idleLeftFrames.length;
            }
        }
    }

    /**
     * Moves the player according to velocity and checks for tile collisions.
     * Sets `onGround` when vertical movement is blocked downward.
     */
    private void updatePosition(double dt, World world) {
        double nx = x + vx * dt;
        if (!checkCollision(nx, y, world)) {
            x = nx;
        } else {
            vx = 0;
        }

        double ny = y + vy * dt;
        if (!checkCollision(x, ny, world)) {
            y = ny;
            onGround = false;
        } else {
            if (vy > 0) onGround = true;
            vy = 0;
        }
    }

    /**
     * Checks if the rectangle (px,py,width,height) intersects any solid tile.
     *
     * @param px    proposed X coordinate (pixels)
     * @param py    proposed Y coordinate (pixels)
     * @param world the World providing isSolid(x,y)
     * @return true if collision detected
     */
    private boolean checkCollision(double px, double py, World world) {
        int ts     = TileConstants.TILE_SIZE;
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
     * Renders the correct animation frame at the player's screen position.
     *
     * @param gc  the GraphicsContext to draw on
     * @param cam the Camera to convert world→screen coordinates
     */
    public void render(GraphicsContext gc, Camera cam) {
        double sx = x - cam.getWorldX();
        double sy = y - cam.getWorldY();

        Image[] frames;
        if (!onGround) {
            frames = facingRight ? jumpRightFrames : jumpLeftFrames;
        } else if (movingLeft || movingRight) {
            frames = facingRight ? runRightFrames : runLeftFrames;
        } else {
            frames = facingRight ? idleRightFrames : idleLeftFrames;
        }

        int idx = Math.max(0, Math.min(frameIndex, frames.length - 1));
        gc.drawImage(frames[idx], sx, sy, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    /** Instantly set the player's position (used on level transitions). */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** @return world X coordinate (pixels). */
    public double getX() { return x; }
    /** @return world Y coordinate (pixels). */
    public double getY() { return y; }
}
