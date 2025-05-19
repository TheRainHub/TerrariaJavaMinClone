package entity;

import world.World;
import engine.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    private double x, y;
    private double vx, vy;
    private boolean movingLeft, movingRight, onGround;
    private static final double SPEED = 150, JUMP_POWER = -350, GRAVITY = 900;

    public Player(double x, double y) { this.x = x; this.y = y; }

    public void moveLeft() { movingLeft = true; }
    public void moveRight() { movingRight = true; }
    public void stopMovingLeft() { movingLeft = false; }
    public void stopMovingRight() { movingRight = false; }
    public void jump() { if (onGround) vy = JUMP_POWER; }

    public void update(double dt, World world) {
        double ax = 0;
        if (movingLeft) ax -= SPEED;
        if (movingRight) ax += SPEED;
        vx = ax;
        vy += GRAVITY * dt;
        x += vx * dt;
        y += vy * dt;
        // Простая коллизия по полу
        if (y > world.getHeight() * 32 - 64) {
            y = world.getHeight() * 32 - 64;
            vy = 0;
            onGround = true;
        } else onGround = false;
    }

    public void render(GraphicsContext gc, Camera camera) {
        int tileSize = 32;
        double sx = x - camera.getWorldX();
        double sy = y - camera.getWorldY();
        gc.setFill(Color.BLUE);
        gc.fillOval(sx, sy, tileSize, tileSize);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
