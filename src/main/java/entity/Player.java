package entity;

import world.World;
import engine.Camera;
import util.TileConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Player {

    private static final int PLAYER_WIDTH  = 16;
    private static final int PLAYER_HEIGHT = 34;

    private static final double SPEED       = 150;
    private static final double JUMP_POWER  = -350;
    private static final double GRAVITY     = 900;
    private static final double MAX_FALL    = 600;

    private double x, y;
    private double vx, vy;
    private boolean movingLeft, movingRight, onGround;

    public Player(double x, double y) { this.x = x; this.y = y; }

    /* ───────────── управление ───────────── */
    public void moveLeft()      { movingLeft  = true;  }
    public void moveRight()     { movingRight = true;  }
    public void stopMovingLeft(){ movingLeft  = false; }
    public void stopMovingRight(){movingRight = false; }
    public void jump()          { if (onGround) vy = JUMP_POWER; }

    public void update(double dt, World world) {
        if (movingLeft)  vx = -SPEED;
        else if (movingRight) vx =  SPEED;
        else vx = 0;

        vy += GRAVITY * dt;
        if (vy >  MAX_FALL) vy =  MAX_FALL;
        if (vy < -MAX_FALL) vy = -MAX_FALL;

        updatePosition(dt, world);
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
            if (vy > 0) {
                onGround = true;
            }
            vy = 0;
        }
    }

    private boolean checkCollision(double px, double py, World world) {
        int left   = (int) (px                   / TileConstants.TILE_SIZE);
        int right  = (int) ((px + PLAYER_WIDTH  - 1) / TileConstants.TILE_SIZE);
        int top    = (int) (py                   / TileConstants.TILE_SIZE);
        int bottom = (int) ((py + PLAYER_HEIGHT - 1) / TileConstants.TILE_SIZE);

        return world.isSolid(left,top   ) ||
                world.isSolid(right, top   ) ||
                world.isSolid(left,  bottom) ||
                world.isSolid(right, bottom);
    }

    public void render(GraphicsContext gc, Camera camera) {
        double sx = x - camera.getWorldX();
        double sy = y - camera.getWorldY();
        gc.setFill(Color.BLUE);
        gc.fillRect(sx, sy, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
