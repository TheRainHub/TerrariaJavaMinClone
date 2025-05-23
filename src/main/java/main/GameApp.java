package main;

import engine.core.GameLoop;
import engine.input.InputHandler;
import engine.level.LevelManager;
import engine.save.SaveLoadManager;
import engine.ui.UIManager;
import entity.Player;
import entity.NPC;
import entity.ItemEntity;
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

public class GameApp extends Application {

    public static final int WIDTH  = 1720;
    public static final int HEIGHT =  820;

    @Override
    public void start(Stage primaryStage) {
        // 1) Создаём холст и сцену
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // 2) Загружаем фон (может быть null)
        Image bg = null;
        try {
            bg = new Image(getClass().getResourceAsStream("/Forest_background_9.png"));
        } catch (Exception ignored) {}
        List<String> levelFiles = List.of("/map1.txt", "/map2.txt", "/map3.txt");
        // 3) Инициализируем основные системы
        Inventory inventory = new Inventory();
        Player player       = new Player(0, 0);
        Camera camera       = new Camera(0, 0, WIDTH, HEIGHT);

        LevelManager lvlMgr = new LevelManager(
                player,
                camera,
                inventory,    // <— вот он, недостающий аргумент
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

        // 4) Создаём и настраиваем главный цикл
        GameLoop loop = new GameLoop(
                canvas.getGraphicsContext2D(), // gc
                scene,                         // scene
                WIDTH,                         // width
                HEIGHT,                        // height
                lvlMgr,                        // уровень
                uiMgr,                         // UI-менеджер
                input,                         // ввод
                player,                        // игрок
                bg                             // фон
        );

        // 5) Конфигурируем и показываем окно
        primaryStage.setTitle("Terraria-Like 2D Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 6) Стартуем игровой цикл
        loop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
