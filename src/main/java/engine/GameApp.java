package engine;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

public class GameApp extends Application {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;


    @Override
    public void start(Stage primaryStage){
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GameLoop loop = new GameLoop(canvas);

        Group root = new Group(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setTitle("MIWGame Engine");
        primaryStage.setScene(scene);
        primaryStage.show();

        loop.start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
