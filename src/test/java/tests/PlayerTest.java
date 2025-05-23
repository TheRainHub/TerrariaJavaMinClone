package tests;

import entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import world.TileType;
import world.World;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private static World emptyWorld;

    @BeforeAll
    static void initFXAndWorld() {
        javafx.application.Platform.startup(() -> {});

        TileType[][] tiles = new TileType[10][10];
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                tiles[y][x] = TileType.AIR;
            }
        }
        emptyWorld = new World(tiles);

    }

    @Test
    void testJumpMovement() {
        Player p = new Player(100, 100);
        p.jump();
        p.update(0.1, emptyWorld);
        assertTrue(p.getY() < 100, "After Jump increase Y");
    }

    @Test
    void testGravityFall() {
        Player p = new Player(0, 0);

        p.update(0.1, emptyWorld);
        assertTrue(p.getY() > 0, "After Gravity increase Y");
    }
}
