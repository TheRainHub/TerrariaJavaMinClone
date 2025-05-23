package main;

import engine.core.GameLoop;
import engine.input.InputHandler;
import engine.level.LevelManager;
import engine.save.SaveLoadManager;
import engine.ui.UIManager;
import entity.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.Inventory;
import util.Recipe;
import util.RecipeLoader;
import engine.Camera;

import java.util.List;

/**
 * Main application class for the 2D Terraria-like game.
 * <p>
 * Initializes JavaFX, loads resources, sets up game systems (world, UI, input, saving),
 * and starts the game loop.
 * </p>
 */
public class GameApp extends Application {

    /**
     * Width of the game canvas in pixels.
     */
    public static final int WIDTH  = 1720;
    /**
     * Height of the game canvas in pixels.
     */
    public static final int HEIGHT =  820;

    /**
     * Entry point for JavaFX application. Sets up the stage, scene, and game components.
     *
     * @param primaryStage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        // 1) Create canvas and scene
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // 2) Load background image (nullable)
        Image bg = null;
        try {
            bg = new Image(getClass().getResourceAsStream("/Forest_background_9.png"));
        } catch (Exception ignored) {}

        List<String> levelFiles = List.of("/map1.txt", "/map2.txt", "/map3.txt");

        // 3) Initialize core systems: inventory, player, camera, levels, save/load
        Inventory inventory = new Inventory();
        Player player       = new Player(0, 0);
        Camera camera       = new Camera(0, 0, WIDTH, HEIGHT);

        LevelManager lvlMgr = new LevelManager(
                player,
                camera,
                inventory,
                levelFiles
        );

        SaveLoadManager saveMgr = new SaveLoadManager(inventory, player, lvlMgr);
        if (!saveMgr.loadAll()) {
            lvlMgr.init();
        }

        List<Recipe> recipes = RecipeLoader.loadRecipes("/recipes.txt");
        UIManager uiMgr = new UIManager(inventory, recipes, lvlMgr.getNpcs(), saveMgr);

        InputHandler input = new InputHandler(
                player,
                lvlMgr,
                uiMgr,
                lvlMgr.getNpcs()
        );

        // 4) Create and configure the game loop
        GameLoop loop = new GameLoop(
                canvas.getGraphicsContext2D(),
                scene,
                WIDTH,
                HEIGHT,
                lvlMgr,
                uiMgr,
                input,
                player,
                bg
        );

        // 5) Configure and show the primary stage
        primaryStage.setTitle("Terraria-Like 2D Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 6) Start the game loop
        loop.start();
    }

    /**
     * Main method, launches the JavaFX application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
