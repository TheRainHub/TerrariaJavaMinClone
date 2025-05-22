package world;

import util.ResourceLoader;
import java.util.*;

public class WorldLoader {
    public static Level loadLevel(String resourcePath, TileRegistry registry) {
        List<String> lines = ResourceLoader.readResourceLines(resourcePath);
        if (lines == null || lines.isEmpty()) {
            throw new RuntimeException("Failed to load level data from '" + resourcePath + "'");
        }

        // 1) Определяем размеры «прямоугольной» части карты
        int width  = lines.get(0).length();
        int height = 0;
        while (height < lines.size() && lines.get(height).length() == width) {
            height++;
        }

        // 2) Собираем тайлы
        TileType[][] tiles = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                tiles[y][x] = registry.fromChar(row.charAt(x));
            }
        }

        // 3) Собираем спавны предметов (строки после основной карты)
        List<Level.ItemSpawn> itemSpawns = new ArrayList<>();
        for (int i = height; i < lines.size(); i++) {
            String l = lines.get(i).trim();
            if (!l.startsWith("ITEM")) continue;
            String[] p = l.split("\\s+");
            // формат: ITEM <id> <x> <y>
            ItemType t = ItemType.fromId(p[1]);
            if (t != null) {
                int tx = Integer.parseInt(p[2]);
                int ty = Integer.parseInt(p[3]);
                itemSpawns.add(new Level.ItemSpawn(t, tx, ty));
            }
        }

        // 4) Возвращаем собранный Level
        return new Level(tiles, itemSpawns);
    }
}
