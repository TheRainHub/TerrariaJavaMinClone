package entity;

import world.World;
import engine.Camera;
import util.TileConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {

    public static final int PLAYER_WIDTH  = 34;
    public static final int PLAYER_HEIGHT = 42;

    private static final double SPEED       = 150;
    private static final double JUMP_POWER  = -500;
    private static final double GRAVITY     = 900;
    private static final double MAX_FALL    = 600;


    private final Image[] idleLeftFrames   = new Image[4];
    private final Image[] idleRightFrames  = new Image[4];
    private final Image[] runLeftFrames    = new Image[4];
    private final Image[] runRightFrames   = new Image[4];
    private final Image[] jumpLeftFrames   = new Image[5];
    private final Image[] jumpRightFrames  = new Image[5];

    private int   frameIndex    = 0;
    private double frameTimer   = 0;
    private static final double FRAME_DURATION     = 0.25;
    private static final double JUMP_FRAME_DURATION = 0.13;

    private boolean facingRight = true;
    private boolean wasOnGround = true;

    private double x, y, vx, vy;
    private boolean movingLeft, movingRight, onGround;

    public Player(double x, double y) {
        this.x = x; this.y = y;
        loadAnimations();
    }

    private void loadAnimations() {
        for (int i = 0; i < 4; i++) {
            String n = Integer.toString(i+1);
            idleLeftFrames[i]  = load("/animation/JonkleAnimatedLeftStand"  + n + ".png");
            idleRightFrames[i] = load("/animation/JonkleAnimatedStandRight" + n + ".png");
            runLeftFrames[i]   = load("/animation/JonkleRunLeft"            + n + ".png");
            runRightFrames[i]  = load("/animation/JonkleRunRight"           + n + ".png");
        }
        for (int i = 0; i < 5; i++) {
            String n = Integer.toString(i+1);
            jumpLeftFrames[i]  = load("/animation/JonkleJumpLeft"  + n + ".png");
            jumpRightFrames[i] = load("/animation/JonkleJumpRight" + n + ".png");
        }
    }

    private Image load(String path) {
        var is = getClass().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("Animation file not found: " + path);
        return new Image(is);
    }

    public void moveLeft()      { movingLeft  = true;  facingRight = false; }
    public void moveRight()     { movingRight = true;  facingRight = true;  }
    public void stopMovingLeft(){ movingLeft  = false; }
    public void stopMovingRight(){movingRight = false; }
    public void jump() {
        if (onGround) {
            vy = JUMP_POWER;

            wasOnGround = true;
            frameIndex  = 0;
            frameTimer  = 0;
        }
    }

    public void update(double dt, World world) {
        // 0) сохраним предыдущее состояние onGround
        boolean prevOnGround = onGround;

        if      (movingLeft)  vx = -SPEED;
        else if (movingRight) vx =  SPEED;
        else                  vx =  0;

        vy += GRAVITY * dt;
        vy = Math.max(-MAX_FALL, Math.min(MAX_FALL, vy));
        updatePosition(dt, world);

        boolean justStartedJump = (!onGround) && prevOnGround;
        boolean justLanded      = onGround && (!prevOnGround);

        if (justStartedJump) {
            frameIndex  = 0;
            frameTimer  = 0;
        }

        if (!onGround) {
            frameTimer += dt;
            if (frameIndex < 2 && frameTimer >= JUMP_FRAME_DURATION) {
                frameTimer -= JUMP_FRAME_DURATION;
                frameIndex++;
            }
        }
        else if (justLanded) {
            frameIndex = 3;
            frameTimer = 0;
        }
        else if (frameIndex == 3) {
            frameTimer += dt;
            if (frameTimer >= JUMP_FRAME_DURATION) {
                frameTimer -= JUMP_FRAME_DURATION;
                frameIndex = 4;    // последний кадр «возврат»
            }
        }
        else if (movingLeft || movingRight) {
            frameTimer += dt;
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % runLeftFrames.length;
            }
        }
        else {
            frameTimer += dt;
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frameIndex = (frameIndex + 1) % idleLeftFrames.length;
            }
        }
    }

    private void updatePosition(double dt, World world) {
        double nx = x + vx * dt;
        if (!checkCollision(nx, y, world)) x = nx; else vx = 0;
        double ny = y + vy * dt;
        if (!checkCollision(x, ny, world)) {
            y = ny;
            onGround = false;
        } else {
            if (vy > 0) onGround = true;
            vy = 0;
        }
    }

    private boolean checkCollision(double px, double py, World world) {
        int ts     = TileConstants.TILE_SIZE;
        int left   = (int)(px                  / ts),
                right  = (int)((px+PLAYER_WIDTH -1)/ ts),
                top    = (int)(py                  / ts),
                bottom = (int)((py+PLAYER_HEIGHT-1)/ ts);
        return world.isSolid(left, top) ||
                world.isSolid(right, top)||
                world.isSolid(left, bottom)||
                world.isSolid(right,bottom);
    }

    public void render(GraphicsContext gc, Camera cam) {
        double sx = x - cam.getWorldX(), sy = y - cam.getWorldY();
        Image[] set;
        if (!onGround) {
            set = facingRight ? jumpRightFrames : jumpLeftFrames;
        } else if (movingLeft || movingRight) {
            set = facingRight ? runRightFrames : runLeftFrames;
        } else {
            set = facingRight ? idleRightFrames : idleLeftFrames;
        }
        int idx = frameIndex;
        if (idx < 0) idx = 0;
        if (idx >= set.length) idx = set.length - 1;
        gc.drawImage(set[idx], sx, sy, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public double getX(){return x;}
    public double getY(){return y;}
}
