package entity;

import world.World;
import engine.Camera;
import util.TileConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {

    private static final int PLAYER_WIDTH  = 34;
    private static final int PLAYER_HEIGHT = 42;

    private static final double SPEED       = 150;
    private static final double JUMP_POWER  = -350;
    private static final double GRAVITY     = 900;
    private static final double MAX_FALL    = 600;


    private final Image[] idleLeftFrames;
    private final Image[] idleRightFrames;
    private int   idleFrameIndex = 0;
    private double idleTimer     = 0;
    private static final double IDLE_FRAME_DURATION = 0.25;

    private boolean facingRight = true;

    private double x, y;
    private double vx, vy;
    private boolean movingLeft, movingRight, onGround;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;

        idleLeftFrames  = new Image[4];
        idleRightFrames = new Image[4];
        for (int i = 0; i < 4; i++) {
            String leftPath  = "/animation/JonkleAnimatedLeftStand"  + (i+1) + ".png";
            var urlL = getClass().getResource(leftPath);
            if (urlL == null) throw new RuntimeException("Cannot load idle frame: " + leftPath);
            idleLeftFrames[i] = new Image(urlL.toExternalForm());

            String rightPath = "/animation/JonkleAnimatedStandRight" + (i+1) + ".png";
            var urlR = getClass().getResource(rightPath);
            if (urlR == null) throw new RuntimeException("Cannot load idle frame: " + rightPath);
            idleRightFrames[i] = new Image(urlR.toExternalForm());
        }
    }

    public void moveLeft()  { movingLeft  = true;  facingRight = false; }
    public void moveRight() { movingRight = true;  facingRight = true;  }
    public void stopMovingLeft()  { movingLeft  = false; }
    public void stopMovingRight() { movingRight = false; }
    public void jump()            { if (onGround) vy = JUMP_POWER; }

    public void update(double dt, World world) {
        if      (movingLeft)  vx = -SPEED;
        else if (movingRight) vx =  SPEED;
        else                  vx =  0;

        vy += GRAVITY * dt;
        vy = Math.max(-MAX_FALL, Math.min(MAX_FALL, vy));

        updatePosition(dt, world);


        if (!movingLeft && !movingRight) {
            idleTimer += dt;
            if (idleTimer >= IDLE_FRAME_DURATION) {
                idleTimer -= IDLE_FRAME_DURATION;
                idleFrameIndex = (idleFrameIndex + 1) % idleLeftFrames.length;
            }
        } else {

            idleFrameIndex = 0;
            idleTimer      = 0;
        }
    }

    private void updatePosition(double dt, World world) {

        double newX = x + vx * dt;
        if (!checkCollision(newX, y, world)) {
            x = newX;
        } else {
            vx = 0;
        }


        double newY = y + vy * dt;
        if (!checkCollision(x, newY, world)) {
            y = newY;
            onGround = false;
        } else {
            if (vy > 0) onGround = true;
            vy = 0;
        }
    }

    private boolean checkCollision(double px, double py, World world) {
        int ts     = TileConstants.TILE_SIZE;
        int left   = (int) (px                   / ts);
        int right  = (int) ((px + PLAYER_WIDTH  - 1) / ts);
        int top    = (int) (py                   / ts);
        int bottom = (int) ((py + PLAYER_HEIGHT - 1) / ts);

        return world.isSolid(left,  top   ) ||
                world.isSolid(right, top   ) ||
                world.isSolid(left,  bottom) ||
                world.isSolid(right, bottom);
    }

    public void render(GraphicsContext gc, Camera camera) {
        double sx = x - camera.getWorldX();
        double sy = y - camera.getWorldY();

        // выбираем текущий кадр по направлению взгляда
        Image frame = facingRight
                ? idleRightFrames[idleFrameIndex]
                : idleLeftFrames [idleFrameIndex];

        gc.drawImage(frame, sx, sy, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
