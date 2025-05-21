package main;

import engine.GameEngine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameApp extends Application {

    public static final int WIDTH = 1720;
    public static final int HEIGHT = 820;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        GameEngine engine = new GameEngine(canvas.getGraphicsContext2D(), WIDTH, HEIGHT);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // Проброс событий
        scene.setOnKeyPressed(engine::handleKeyPress);
        scene.setOnKeyReleased(engine::handleKeyRelease);
        scene.setOnMousePressed(engine::handleMousePress);
        scene.setOnMouseReleased(engine::handleMouseRelease);
        scene.setOnMouseMoved(engine::handleMouseMove);

        primaryStage.setTitle("Terraria-Like 2D Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        engine.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
