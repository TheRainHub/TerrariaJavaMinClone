package world;

import util.ResourceLoader;
import java.util.List;

public class WorldLoader {
    public static TileType[][] loadFromResource(String resourcePath, TileRegistry registry) {
        List<String> lines = ResourceLoader.readResourceLines(resourcePath);
        int height = lines.size();
        int width = lines.get(0).length();
        TileType[][] tiles = new TileType[height][width];

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                tiles[y][x] = registry.fromChar(c);
            }
        }

        return tiles;
    }
}