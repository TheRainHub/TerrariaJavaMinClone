package world;

import util.ResourceLoader;
import java.util.*;

public class WorldLoader {
    public static Level loadLevel(String resourcePath, TileRegistry registry) {
        List<String> lines = ResourceLoader.readResourceLines(resourcePath);
        if (lines.isEmpty()) throw new RuntimeException("Level file is empty: " + resourcePath);

        // 1) Размер «прямоугольной» части карты
        int width  = lines.get(0).length();
        int height = 0;
        while (height < lines.size() && lines.get(height).length() == width) {
            height++;
        }

        // 2) Заполняем тайлы
        TileType[][] tiles = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                tiles[y][x] = registry.fromChar(row.charAt(x));
            }
        }

        // 3) Парсим ITEM и NPC строки после карты
        List<Level.ItemSpawn> itemSpawns = new ArrayList<>();
        List<Level.NPCSpawn>  npcSpawns  = new ArrayList<>();

        for (int i = height; i < lines.size(); i++) {
            String[] p = lines.get(i).trim().split("\\s+");
            if (p[0].equals("ITEM")) {
                ItemType t = ItemType.fromId(p[1]);
                if (t != null) {
                    itemSpawns.add(new Level.ItemSpawn(
                            t,
                            Integer.parseInt(p[2]),
                            Integer.parseInt(p[3])
                    ));
                }
            } else if (p[0].equals("NPC")) {
                npcSpawns.add(new Level.NPCSpawn(
                        p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3])
                ));
            }
        }

        // 4) Вернём новый Level сразу со списками спавнов
        return new Level(tiles, itemSpawns, npcSpawns);
    }
}
