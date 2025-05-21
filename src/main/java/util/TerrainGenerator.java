package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TerrainGenerator{
    public static void generatePerlinLike(String file, int w, int h, long seed) throws IOException {
        Random rnd = new Random(seed);
        int[] surface = new int[w];
        int baseDirtThickness = 15;
        double stoneToDirtChance = 0.2;

        // 1. поверхность
        surface[0] = h / 2;
        for (int x = 1; x < w; x++) {
            surface[x] = surface[x-1] + rnd.nextInt(3) - 1;
            surface[x] = Math.max(h/4, Math.min(3*h/4, surface[x]));
        }
        // сглаживание
        for (int k = 0; k < 2; k++)
            for (int x = 1; x < w-1; x++)
                surface[x] = (surface[x-1] + surface[x] + surface[x+1]) / 3;

        // 2. пишем базовый текстовый слой
        char[][] map = new char[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if      (y < surface[x])                  map[y][x] = '.';
                else if (y == surface[x])                 map[y][x] = 'G';
                else if (y < surface[x] + baseDirtThickness) map[y][x] = 'D';
                else {
                    // below the thicker dirt layer → STONE or maybe DIRT
                    if (rnd.nextDouble() < stoneToDirtChance)
                        map[y][x] = 'D';
                    else
                        map[y][x] = 'S';
                }
            }
        }

        // 3. (опционально) пещеры
        int walkers = w/15;
        for (int i = 0; i < walkers; i++) {
            int wx = rnd.nextInt(w), wy = rnd.nextInt(h);
            for (int step = 0; step < w*3; step++) {
                map[wy][wx] = '.';
                int dir = rnd.nextInt(4);
                wx = Math.max(1, Math.min(w-2, wx + (dir==0?1:dir==1?-1:0)));
                wy = Math.max(1, Math.min(h-2, wy + (dir==2?1:dir==3?-1:0)));
            }
        }


        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (map[y][x] != 'D') continue;
                int m = 0;
                if (y>0             && map[y-1][x] == '.') m |= 1;
                if (x<w-1           && map[y][x+1] == '.') m |= 2;
                if (y<h-1           && map[y+1][x] == '.') m |= 4;
                if (x>0             && map[y][x-1] == '.') m |= 8;

                if      ((m & 1) != 0) map[y][x] = 'G';    // верхняя
                else if ((m & 8) != 0) map[y][x] = 'L';    // левая
                else if ((m & 2) != 0) map[y][x] = 'R';    // правая
                else if ((m & 4) != 0) map[y][x] = 'B';    // нижняя
            }
        }

        // 5. финальный сброс в файл
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (char[] row : map) pw.println(new String(row));
        }

    }
    public static void main(String[] args) throws IOException {
        generatePerlinLike("src/main/resources/map.txt", 200, 100, System.currentTimeMillis());
    }
}

