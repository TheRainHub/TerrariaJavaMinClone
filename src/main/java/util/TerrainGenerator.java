package util;

import world.TileType;
import java.io.*;
import java.util.Random;

public class TerrainGenerator {

    public static void generatePerlinLike(String file, int width, int height, long seed) throws IOException {
        Random rnd = new Random(seed);
        int[] ground = new int[width];

        ground[0] = height / 2;
        for (int x = 1; x < width; x++) {
            ground[x] = ground[x-1] + rnd.nextInt(3) - 1;
            ground[x] = Math.max(height/4, Math.min(3*height/4, ground[x]));
        }
        for (int k = 0; k < 2; k++)
            for (int x = 1; x < width-1; x++)
                ground[x] = (ground[x-1] + ground[x] + ground[x+1]) / 3;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (int y = 0; y < height; y++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < width; x++) {
                    row.append(y >= ground[x] ? '#' : '.');
                }
                pw.println(row.toString());
            }
        }
    }

    public static void generateCaves(String file, int width, int height, long seed) throws IOException {
        char[][] map = new char[height][width];
        for (int y=0; y<height; y++) for (int x=0; x<width; x++) map[y][x] = '#';

        int walkerX = width / 2, walkerY = height / 2;
        Random rnd = new Random(seed);
        for (int i = 0; i < width*height*4; i++) {
            map[walkerY][walkerX] = '.';
            int dir = rnd.nextInt(4);
            walkerX = Math.max(1, Math.min(width - 2, walkerX + (dir==0?1:dir==1?-1:0)));
            walkerY = Math.max(1, Math.min(height - 2, walkerY + (dir==2?1:dir==3?-1:0)));
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (char[] row : map) pw.println(row);
        }
    }

    public static void main(String[] args) throws IOException {
        generatePerlinLike("src/main/resources/level1.txt", 200, 60, System.currentTimeMillis());
    }
}
