package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TerrainGenerator {
    public static void generatePerlinLike(String file, int w, int h, long seed) throws IOException {
        Random rnd = new Random(seed);
        int[] surface = new int[w];
        int baseDirtThickness = 15;
        double stoneToDirtChance = 0.2;

        int treeCount   = w / 30;    // например, одно дерево на каждые ~30 тайлов
        int trunkHeight = 4;
        int leafRadius  = 3;


        // 1. генерируем профиль поверхности
        surface[0] = h / 2;
        for (int x = 1; x < w; x++) {
            surface[x] = surface[x-1] + rnd.nextInt(3) - 1;
            surface[x] = Math.max(h/4, Math.min(3*h/4, surface[x]));
        }
        // сглаживание
        for (int k = 0; k < 2; k++)
            for (int x = 1; x < w-1; x++)
                surface[x] = (surface[x-1] + surface[x] + surface[x+1]) / 3;

        // 2. создаём базовый char-слой
        char[][] map = new char[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if      (y < surface[x])                      map[y][x] = '.';
                else if (y == surface[x])                     map[y][x] = 'G';
                else if (y < surface[x] + baseDirtThickness)  map[y][x] = 'D';
                else {
                    if (rnd.nextDouble() < stoneToDirtChance) map[y][x] = 'D';
                    else                                      map[y][x] = 'S';
                }
            }
        }

        // 5. генерация деревьев

        for (int i = 0; i < treeCount; i++) {
            int tx = rnd.nextInt(w);
            int sy = surface[tx] - 1; // место, где будет стоять ствол (над поверхностью)
            // проверяем, что в этих координатах AIR, чтобы дерево не накладывалось
            if (sy - trunkHeight - leafRadius < 0) continue;
            boolean canPlace = true;
            for (int dy = 0; dy <= trunkHeight + leafRadius; dy++) {
                if (map[sy - dy][tx] != '.') {
                    canPlace = false;
                    break;
                }
            }
            if (!canPlace) {
                i--;
                continue;
            } // попробуем другое место

            // ставим пенёк
            map[sy + 1][tx] = 'M'; // ниже ствола, ровно на поверхности

            // ставим ствол
            for (int dy = 0; dy < trunkHeight; dy++) {
                map[sy - dy][tx] = 'T';
            }

            // ставим листья вплоть до заданного радиуса (окружность)
            for (int dx = -leafRadius; dx <= leafRadius; dx++) {
                for (int dy = -leafRadius; dy <= leafRadius; dy++) {
                    int dist2 = dx * dx + dy * dy;
                    if (dist2 <= leafRadius * leafRadius) {
                        int lx = tx + dx;
                        int ly = sy - trunkHeight + dy;
                        if (lx >= 0 && lx < w && ly >= 0 && ly < h) {
                            // перекрываем только если там воздух
                            if (map[ly][lx] == '.') {
                                map[ly][lx] = 'E';
                            }
                        }
                    }
                }
            }
        }


        // 3. пещеры «рандомным блужданием»
        int walkers = w / 15;
        for (int i = 0; i < walkers; i++) {
            int wx = rnd.nextInt(w), wy = rnd.nextInt(h);
            for (int step = 0; step < w * 3; step++) {
                map[wy][wx] = '.';
                int dir = rnd.nextInt(4);
                wx = Math.max(1, Math.min(w-2, wx + (dir==0?1:dir==1?-1:0)));
                wy = Math.max(1, Math.min(h-2, wy + (dir==2?1:dir==3?-1:0)));
            }
        }

        // 4. автотайл травы по соседям
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (map[y][x] != 'D') continue;
                int m = 0;
                if (y>0             && map[y-1][x] == '.') m |= 1;
                if (x<w-1           && map[y][x+1] == '.') m |= 2;
                if (y<h-1           && map[y+1][x] == '.') m |= 4;
                if (x>0             && map[y][x-1] == '.') m |= 8;

                if      ((m & 1)!=0) map[y][x] = 'G'; // верх
                else if ((m & 8)!=0) map[y][x] = 'L'; // лево
                else if ((m & 2)!=0) map[y][x] = 'R'; // право
                else if ((m & 4)!=0) map[y][x] = 'B'; // низ
            }
        }

        // 5. сброс тайловой части в файл
        try ( PrintWriter pw = new PrintWriter(new FileWriter(file)) ) {
            for (char[] row : map) {
                pw.println(new String(row));
            }

            // 6. рандомные бананы — только на поверхности
            int bananaCount = 2 + rnd.nextInt(2); // 2–3 банана
            for (int i = 0; i < bananaCount; i++) {
                int bx = rnd.nextInt(w);
                int by = surface[bx] - 1;            // сразу над поверхностью
                pw.printf("ITEM %s %d %d%n", "banana", bx, by);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        generatePerlinLike("src/main/resources/map.txt", 800, 400, System.currentTimeMillis());
    }
}
