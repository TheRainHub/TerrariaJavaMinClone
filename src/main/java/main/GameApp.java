package main;

import engine.GameEngine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Entry point for the 2D game application.
 * <p>
 * Sets up the JavaFX window, canvas, input handlers, and starts the game engine.
 * Supports pausing via ESC and routes input either to the pause menu or the game.
 * </p>
 */
public class GameApp extends Application {

    /** Width of the game canvas in pixels. */
    public static final int WIDTH = 1720;
    /** Height of the game canvas in pixels. */
    public static final int HEIGHT = 820;

    /**
     * Called by JavaFX to initialize and show the primary stage.
     * <ol>
     *   <li>Creates a Canvas of fixed size.</li>
     *   <li>Instantiates the GameEngine with the canvas' GraphicsContext.</li>
     *   <li>Wraps the canvas in a StackPane and Scene.</li>
     *   <li>Registers key and mouse event handlers:</li>
     *     <ul>
     *       <li>ESC toggles pause/unpause.</li>
     *       <li>If paused, key events go to a pause-menu handler.</li>
     *       <li>Otherwise, keys go to game movement/jump.</li>
     *       <li>Mouse presses/releases/movement go to world interaction.</li>
     *     </ul>
     *   <li>Displays the window and starts the game loop.</li>
     * </ol>
     *
     * @param primaryStage the main window provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        // 1) Create drawing surface
        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        // 2) Initialize the game engine
        GameEngine engine = new GameEngine(canvas.getGraphicsContext2D(), WIDTH, HEIGHT);

        // 3) Create scene graph
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // 4) Handle key presses: pause toggle or delegate to engine
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                engine.togglePause();
            } else if (engine.isPaused()) {
                engine.handlePauseMenuInput(e);
            } else {
                engine.handleKeyPress(e);
            }
        });

        // 5) Handle other input events
        scene.setOnKeyReleased(engine::handleKeyRelease);
        scene.setOnMousePressed(engine::handleMousePress);
        scene.setOnMouseReleased(engine::handleMouseRelease);
        scene.setOnMouseMoved(engine::handleMouseMove);

        // 6) Configure and show the window
        primaryStage.setTitle("Terraria-Like 2D Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 7) Start the game loop
        engine.start();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
