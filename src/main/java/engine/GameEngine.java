//package engine;
//
//import entity.ItemEntity;
//import entity.NPC;
//import entity.Player;
//import javafx.animation.AnimationTimer;
//import javafx.application.Platform;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.image.Image;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.scene.text.Text;
//import util.*;
//import world.*;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Properties;
//import util.Recipe;
//import util.RecipeLoader;
//
//import static entity.Player.PLAYER_WIDTH;
//
///**
// * The core game loop and state manager.
// * <p>
// * Handles loading levels, updating game objects (player, items, world),
// * rendering the world and UI, processing input, and saving/loading game state.
// * </p>
// */
//public class GameEngine {
//    /** The JavaFX graphics context to draw on. */
//    private final GraphicsContext gc;
//    /** Width of the canvas / viewport in pixels. */
//    private final int width, height;
//
//    /** The current world (tile map, physics, mining, placing). */
//    private World world;
//    /** Renders the world tiles using textures. */
//    private WorldRenderer renderer;
//    /** The player character (position, physics, rendering). */
//    private Player player;
//    /** Active item entities in the world. */
//    private final List<ItemEntity> items = new ArrayList<>();
//    /** The player's inventory. */
//    private final Inventory inventory = new Inventory();
//    /** Camera to follow the player and convert coordinates. */
//    private final Camera camera;
//
//    /** Optional background image (parallax). */
//    private Image backgroundImage;
//    /** The main animation timer driving update & render calls. */
//    private AnimationTimer loop;
//    /** Is the game currently running? */
//    private boolean running = false;
//
//    /** Paths to level description files in resources. */
//    private final List<String> levelFiles = List.of(
//            "/map1.txt",
//            "/map2.txt",
//            "/map3.txt"
//    );
//    /** Index of the current level in levelFiles. */
//    private int currentLevel = 0;
//
//    // new pause logic
//    private boolean paused = false;
//    private final List<String> pauseOptions = List.of("Resume", "Save & Quit", "Exit");
//    private int pauseIndex = 0;
//
//
//    /** File where inventory is persisted between runs. */
//    private static final String INVENTORY_FILE = "src/main/resources/inventory.txt";
//    /** File where current level & player position are saved. */
//    private static final String SAVEGAME_FILE  = "savegame.txt";
//
//    private final List<NPC> npcs = new ArrayList<>();
//
//    // --- для крафта ---
//    private final List<Recipe> recipes = RecipeLoader.loadRecipes("/recipes.txt");
//    private boolean craftingOpen = false;
//    private int craftIndex = 0;
//
//    private boolean gameWon = false;
//
//    /**
//     * Constructs the engine, loads inventory (if any), sets up player, camera,
//     * background, animation loop, and either restores a saved game or loads level 0.
//     *
//     * @param gc     the GraphicsContext to render to
//     * @param width  width of the view in pixels
//     * @param height height of the view in pixels
//     */
//    public GameEngine(GraphicsContext gc, int width, int height) {
//        this.gc     = gc;
//        this.width  = width;
//        this.height = height;
//
//        // 1) Load inventory from disk (if exists)
//        try {
//            inventory.loadFromFile(INVENTORY_FILE);
//        } catch (IOException e) {
//            System.err.println("Cannot load inventory: " + e.getMessage());
//        }
//
//        // 2) Initialize player (position set later by loadLevel or loadGameState)
//        player = new Player(0,0);
//        camera = new Camera(0, 0, width, height);
//
//        // 4) Load background image for parallax (optional)
//        try {
//            backgroundImage = new Image(getClass().getResourceAsStream("/Forest_background_9.png"));
//        } catch (Exception e) {
//            backgroundImage = null;
//        }
//
//        // 5) Create animation loop
//        loop = new AnimationTimer() {
//            private long last = 0;
//            @Override
//            public void handle(long now) {
//                if (last == 0) { last = now; return; }
//                double dt = (now - last) / 1e9;
//                last = now;
//                update(dt);
//                render();
//                renderUI();
//            }
//        };
//
//        // 6) Attempt to restore saved game; otherwise start at level 0
//        if (!loadGameState()) {
//            loadLevel(0);
//        }
//
//        int tileSize = TileConstants.TILE_SIZE;
//        int spawnTileX = world.getWidth() / 2;
//        int spawnTileY = world.getSurfaceY(spawnTileX) - 1;
//        double npcX = spawnTileX * tileSize;
//        double npcY = spawnTileY * tileSize;
//        String[] broDialog = {
//                "Hello Bro!",
//                "To become monkey king, you should have a lot of bananas!",
//                "You can find a lot of bananas in level 2 and 3.",
//                "Just go to the right and when you find 20, you will win."
//        };
//        npcs.add(new NPC(npcX, npcY, broDialog));
//    }
//
//    /**
//     * Starts the game loop.
//     */
//    public void start() {
//        loop.start();
//        running = true;
//    }
//
//    /**
//     * Stops the game loop, saves inventory and game state.
//     */
//    public void stop() {
//        loop.stop();
//        running = false;
//        saveInventory();
//        saveGameState();
//    }
//
//    public boolean isPaused() {
//        return paused;
//    }
//
//    public void togglePause() {
//        System.out.println("Toggling pause! was = " + paused);
//        paused = !paused;
//    }
//
//    public void handlePauseMenuInput(KeyEvent e) {
//        switch (e.getCode()) {
//            case UP, W:
//                pauseIndex = (pauseIndex + pauseOptions.size() - 1) % pauseOptions.size();
//                break;
//            case DOWN, S:
//                pauseIndex = (pauseIndex + 1) % pauseOptions.size();
//                break;
//            case ENTER:
//                switch (pauseOptions.get(pauseIndex)) {
//                    case "Resume" -> paused = false;
//                    case "Save & Quit" -> {
//                        saveGameState();
//                        Platform.exit();
//                    }
//                    case "Exit" -> Platform.exit();
//                }
//                break;
//            default: break;
//        }
//    }
//
//    /**
//     * Updates game logic each frame.
//     *
//     * @param dt time elapsed since last frame in seconds
//     */
//    private void update(double dt) {
//        if (gameWon) return;
//        if (paused || craftingOpen) return;
//        // 1) Physics & movement
//        player.update(dt, world);
//        camera.centerOn(player.getX(), player.getY());
//
//        // 2) Item pickups
//        Iterator<ItemEntity> it = items.iterator();
//        while (it.hasNext()) {
//            if (it.next().update(player, world)) {
//                it.remove();
//            }
//        }
//
//        for (NPC npc : npcs) {
//            npc.update(dt, world);
//        }
//
//        // 3) Level transitions when player crosses left/right bounds
//        int tx = (int)(player.getX() / TileConstants.TILE_SIZE);
//        if (tx < 0 && currentLevel > 0) {
//            loadLevel(currentLevel - 1);
//            player.setPosition(
//                    (world.getWidth() * TileConstants.TILE_SIZE) - PLAYER_WIDTH - 1,
//                    player.getY()
//            );
//            return;
//        }
//        if (tx >= world.getWidth() && currentLevel < levelFiles.size() - 1) {
//            loadLevel(currentLevel + 1);
//            player.setPosition(1, player.getY());
//        }
//    }
//
//    /**
//     * Renders the background, world, items, and player.
//     */
//    private void render() {
//        if (gameWon) {
//            gc.setGlobalAlpha(1.0);
//            // полупрозрачный фон
//            gc.setFill(Color.rgb(0, 0, 0, 0.75));
//            gc.fillRect(0, 0, width, height);
//
//            // крупный золотой текст
//            String msg = "YOU WIN!";
//            Font winFont = Font.font("Consolas", FontWeight.BOLD, 72);
//            gc.setFont(winFont);
//            gc.setFill(Color.GOLD);
//
//            Text helper = new Text(msg);
//            helper.setFont(winFont);
//            double textWidth = helper.getLayoutBounds().getWidth();
//
//            double x = (width - textWidth) / 2;
//            double y = height / 2;
//            gc.fillText(msg, x, y);
//
//            return;
//
//        }
//
//        gc.setGlobalAlpha(1.0);
//        gc.clearRect(0, 0, width, height);
//
//        // draw background (parallax)
//        if (backgroundImage != null) {
//            double p = 0.2;
//            gc.drawImage(
//                    backgroundImage,
//                    -camera.getWorldX() * p,
//                    -camera.getWorldY() * p,
//                    width, height
//            );
//        } else {
//            gc.setFill(Color.CORNFLOWERBLUE);
//            gc.fillRect(0, 0, width, height);
//        }
//
//        for (NPC npc : npcs) {
//            npc.render(gc, camera);
//        }
//
//        // draw tiles, items, player
//        renderer.render(gc, camera, world.getTiles());
//        items.forEach(i -> i.render(gc, camera));
//        player.render(gc, camera);
//        // если какой-то NPC в диалоге — рисуем оверлей
//        for (NPC npc : npcs) {
//            if (npc.isInDialog()) {
//                drawDialogBox(gc, npc.currentDialogLine());
//                break;  // рисуем только первое активное окно
//            }
//        }
//        if (paused) {
//            renderPauseOverlay();
//        }
//    }
//
//    private void drawDialogBox(GraphicsContext gc, String text) {
//        double boxW = width * 0.8;
//        double boxH = 80;
//        double boxX = (width - boxW) / 2;
//        double boxY = height - boxH - 20;
//
//        gc.setGlobalAlpha(0.75);
//        gc.setFill(Color.BLACK);
//        gc.fillRoundRect(boxX, boxY, boxW, boxH, 10, 10);
//        gc.setGlobalAlpha(1.0);
//
//        gc.setStroke(Color.WHITE);
//        gc.strokeRoundRect(boxX, boxY, boxW, boxH, 10, 10);
//
//        gc.setFill(Color.WHITE);
//        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
//        gc.fillText(text, boxX + 20, boxY + 30);
//    }
//
//    private void renderPauseOverlay() {
//        // полупрозрачный фон
//        gc.setFill(Color.rgb(0, 0, 0, 0.5));
//        gc.fillRect(0, 0, width, height);
//
//        // меню «окошко»
//        double boxW = 200, boxH = pauseOptions.size() * 30 + 20;
//        double bx = (width - boxW) / 2;
//        double by = (height - boxH) / 2;
//
//        gc.setFill(Color.rgb(20, 20, 20, 0.8));
//        gc.fillRoundRect(bx, by, boxW, boxH, 10, 10);
//        gc.setStroke(Color.WHITE);
//        gc.setLineWidth(2);
//        gc.strokeRoundRect(bx, by, boxW, boxH, 10, 10);
//
//        // рисуем пункты
//        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
//        for (int i = 0; i < pauseOptions.size(); i++) {
//            String text = pauseOptions.get(i);
//            double tx = bx + 20;
//            double ty = by + 30 + i * 30;
//            if (i == pauseIndex) {
//                // подсветка
//                gc.setFill(Color.YELLOW);
//            } else {
//                gc.setFill(Color.WHITE);
//            }
//            gc.fillText(text, tx, ty);
//        }
//    }
//
//    /**
//     * Renders the UI (inventory text).
//     */
//    private void renderUI() {
//        // 1) инвентарь
//        gc.setFill(Color.WHITE);
//        gc.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
//        int y0 = 20;
//        for (var e : inventory.getItems().entrySet()) {
//            gc.fillText(e.getKey() + " x" + e.getValue(), 10, y0);
//            y0 += 20;
//        }
//
//        // 2) окно крафта поверх
//        if (craftingOpen) {
//            double bx = 10, by = 100, bw = 250, bh = recipes.size() * 22 + 20;
//            gc.setFill(Color.rgb(30, 30, 30, 0.8));
//            gc.fillRoundRect(bx, by, bw, bh, 8, 8);
//            gc.setStroke(Color.WHITE);
//            gc.strokeRoundRect(bx, by, bw, bh, 8, 8);
//
//            gc.setFont(Font.font("Consolas", 14));
//            for (int i = 0; i < recipes.size(); i++) {
//                Recipe r = recipes.get(i);
//                double ty = by + 20 + i * 22;
//                gc.setFill(i == craftIndex ? Color.YELLOW : Color.WHITE);
//                gc.fillText(r.output(), bx + 10, ty);
//            }
//
//            // показать ингредиенты выбранного рецепта
//            Recipe cur = recipes.get(craftIndex);
//            gc.setFill(Color.LIGHTGRAY);
//            gc.fillText("Need:", bx + bw + 10, by + 20);
//            int yy = (int)(by + 40);
//            for (var ingr : cur.ingredients().entrySet()) {
//                gc.fillText(ingr.getKey() + " x" + ingr.getValue(), bx + bw + 10, yy);
//                yy += 20;
//            }
//        }
//    }
//
//    /**
//     * Loads a level by index: parses tile map, item spawns, creates world,
//     * world renderer, item entities, and positions the player on the surface.
//     *
//     * @param levelIndex index in {@code levelFiles}
//     */
//    private void loadLevel(int levelIndex) {
//        currentLevel = levelIndex;
//
//        // a) читаем уровень и создаём world+renderer
//        TileRegistry registry = new TileRegistry();
//        Level lvl = WorldLoader.loadLevel(levelFiles.get(levelIndex), registry);
//        world    = new World(lvl.getTiles());
//        renderer = new WorldRenderer(registry.getAllTextures());
//
//        // b) спавним предметы
//        items.clear();
//        for (var spawn : lvl.getItemSpawns()) {
//            double px = spawn.tileX * TileConstants.TILE_SIZE;
//            double py = spawn.tileY * TileConstants.TILE_SIZE;
//            items.add(new ItemEntity(spawn.itemType, inventory, px, py));
//        }
//
//        // c) спавним NPC
//        npcs.clear();
//        for (var spawn : lvl.getNpcSpawns()) {
//            double px = spawn.tileX * TileConstants.TILE_SIZE;
//            double py = spawn.tileY * TileConstants.TILE_SIZE;
//
//            String[] dialog;
//            if (spawn.npcId.equals("bro")) {
//                dialog = new String[]{
//                        "Hello Bro!",
//                        "To become monkey king, you should have a lot of bananas!",
//                        "You can find a lot of bananas in level 2 and 3.",
//                        "Just go to the right and when you find 20, you will win."
//                };
//            } else {
//                dialog = new String[]{ "...(unknown NPC)..." };
//            }
//
//            npcs.add(new NPC(px, py, dialog));
//        }
//
//        // d) позиционируем игрока на поверхности
//        int sx = world.getWidth() / 2;
//        int sy = world.getSurfaceY(sx) - 1;
//        player.setPosition(
//                sx * TileConstants.TILE_SIZE,
//                sy * TileConstants.TILE_SIZE
//        );
//
//    }
//
//    /**
//     * Saves inventory to disk.
//     */
//    private void saveInventory() {
//        try {
//            inventory.saveToFile(INVENTORY_FILE);
//        } catch (IOException ignored) { }
//    }
//
//    /**
//     * Saves current level index and player position to disk.
//     */
//    private void saveGameState() {
//        saveInventory();        // ваш метод сохранения инвентаря
//        // здесь ещё можно сериализовать currentLevel, позицию игрока и т. д.
//        try (var pw = new PrintWriter("savegame.txt")) {
//            pw.println("level=" + currentLevel);
//            pw.println("playerX=" + player.getX());
//            pw.println("playerY=" + player.getY());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Attempts to load saved game state.
//     *
//     * @return true if successful, false otherwise
//     */
//    private boolean loadGameState() {
//        Path f = Path.of(SAVEGAME_FILE);
//        if (!Files.exists(f)) return false;
//        try (var fis = new FileInputStream(SAVEGAME_FILE)) {
//            Properties p = new Properties();
//            p.load(fis);
//            int lvl = Integer.parseInt(p.getProperty("level"));
//            double px = Double.parseDouble(p.getProperty("playerX"));
//            double py = Double.parseDouble(p.getProperty("playerY"));
//            loadLevel(lvl);
//            player.setPosition(px, py);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * Handles key-press events for movement and jumping.
//     *
//     * @param e the KeyEvent
//     */
//    public void handleKeyPress(KeyEvent e) {
//        switch (e.getCode()) {
//            // --- меню крафта активируется на C ---
//            case C -> {
//                craftingOpen = !craftingOpen;
//                return;
//            }
//            // если меню крафта открыто — обрабатываем навигацию в нём
//            default -> {
//                if (craftingOpen) {
//                    switch (e.getCode()) {
//                        case UP, W    -> craftIndex = (craftIndex + recipes.size() - 1) % recipes.size();
//                        case DOWN, S  -> craftIndex = (craftIndex + 1) % recipes.size();
//                        case ENTER -> {
//                            var rec = recipes.get(craftIndex);
//                            if (CraftingManager.canCraft(rec, inventory)) {
//                                CraftingManager.craft(rec, inventory);
//                                if (rec.output().equalsIgnoreCase("crown")) {
//                                    gameWon = true;
//                                }
//                            }
//                        }
//                        case C -> craftingOpen = false;
//                        default -> {}
//                    }
//                    return;
//                }
//            }
//        }
//
//        switch (e.getCode()) {
//            case DIGIT1:
//                if (inventory.getItems().getOrDefault("baton", 0) > 0) {
//                    player.setEquippedItem(ItemType.BATON);
//                }
//                break;
//
//            case E:
//                double px = player.getX() + Player.PLAYER_WIDTH / 2.0;
//                double py = player.getY() + Player.PLAYER_HEIGHT / 2.0;
//                for (NPC npc : npcs) {
//                    npc.interact(px, py);
//                }
//                break;
//            case A:
//            case LEFT:
//                player.moveLeft();
//                break;
//            case D:
//            case RIGHT:
//                player.moveRight();
//                break;
//            case W:
//            case SPACE:
//            case UP:
//                player.jump();
//                break;
//        }
//    }
//
//    /**
//     * Handles key-release events to stop horizontal movement.
//     *
//     * @param e the KeyEvent
//     */
//    public void handleKeyRelease(KeyEvent e) {
//        switch (e.getCode()) {
//            case A, LEFT  -> player.stopMovingLeft();
//            case D, RIGHT -> player.stopMovingRight();
//        }
//    }
//
//    /**
//     * Handles mouse press for mining (left) or placing dirt (right).
//     *
//     * @param e the MouseEvent
//     */
//    public void handleMousePress(MouseEvent e) {
//        int tx = (int)((camera.getWorldX() + e.getX()) / TileConstants.TILE_SIZE);
//        int ty = (int)((camera.getWorldY() + e.getY()) / TileConstants.TILE_SIZE);
//        if (e.isPrimaryButtonDown())
//            world.mineTile(tx, ty);
//        else if (e.isSecondaryButtonDown())
//            world.placeTile(tx, ty, TileType.DIRT);
//    }
//
//    /** No-op on mouse release. */
//    public void handleMouseRelease(MouseEvent e) {}
//    /** No-op on mouse move. */
//    public void handleMouseMove(MouseEvent e) {}
//}
