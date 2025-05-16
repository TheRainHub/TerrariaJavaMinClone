package engine;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameLoop extends AnimationTimer {

    private final GraphicsContext gc;

    public GameLoop(Canvas canvas) {
        this.gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void handle(long now) {
        render();
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        gc.setFill(Color.WHITE);
        gc.fillText("MyGame", 100, 100);
    }
}
