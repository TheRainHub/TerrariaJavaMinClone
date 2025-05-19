package world;

import java.io.*;
import java.util.*;

public class Level {
    public final int width;
    public final int height;
    public final char[][] tiles;

    public Level(char[][] tiles) {
        this.height = tiles.length;
        this.width = tiles[0].length;
        this.tiles = tiles;
    }

    public static Level loadFromFile(String path) throws IOException {
        List<char[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.toCharArray());
            }
        }
        char[][] tiles = rows.toArray(new char[0][]);
        return new Level(tiles);
    }
}
