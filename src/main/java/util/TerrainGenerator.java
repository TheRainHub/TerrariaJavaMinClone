package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Generates a simple 2D terrain map with a Perlin-like surface profile,
 * layered soil/stone, random caves, trees, autotiled grass edges, and item spawns.
 * <p>
 * The output is written to a text file where each line represents one row of tiles:
 * <ul>
 *   <li>'.' — air</li>
 *   <li>'G' — grass block</li>
 *   <li>'D' — dirt block</li>
 *   <li>'S' — stone block</li>
 *   <li>'T' — tree trunk</li>
 *   <li>'E' — leaf (tree foliage)</li>
 *   <li>'M' — tree stump</li>
 * </ul>
 * After the tile grid, a small number of item spawn lines are appended in the format:
 * <pre>ITEM &lt;itemId&gt; &lt;x&gt; &lt;y&gt;</pre>
 * </p>
 */
public class TerrainGenerator {

    /**
     * Generates a terrain map with the specified dimensions and writes it to {@code file}.
     *
     * <p>Algorithm steps:
     * <ol>
     *   <li>Generate a height profile of width {@code w} using random walk, clamped to [h/4, 3h/4].</li>
     *   <li>Smooth the profile by averaging each point with its neighbors (two passes).</li>
     *   <li>Build a {@code char[h][w]} grid:
     *     <ul>
     *       <li>Y &lt; surface[x]: air ('.')</li>
     *       <li>Y == surface[x]: grass ('G')</li>
     *       <li>surface[x] &lt; Y &lt; surface[x]+baseDirtThickness: dirt ('D')</li>
     *       <li>below that: stone ('S') or occasional dirt by chance</li>
     *     </ul>
     *   </li>
     *   <li>Plant {@code w/30} trees at random X positions if space allows:
     *     <ul>
     *       <li>Mark stump ('M'), trunk ('T'), and circular leaf clusters ('E')</li>
     *     </ul>
     *   </li>
     *   <li>Carve random caves using multiple "walkers": each walker erases tiles to air as it randomly moves.</li>
     *   <li>Autotile grass edges: convert certain dirt blocks adjacent to air into edge variants ('G','L','R','B').</li>
     *   <li>Write each row of {@code map} to {@code file}, then append 2–3 random banana item spawns.</li>
     * </ol>
     * </p>
     *
     * @param file  path to the output text file (will be overwritten)
     * @param w     map width in tiles
     * @param h     map height in tiles
     * @param seed  random seed for deterministic generation
     * @throws IOException if writing to {@code file} fails
     */
    public static void generatePerlinLike(String file, int w, int h, long seed) throws IOException {
        Random rnd = new Random(seed);
        int[] surface = new int[w];
        int baseDirtThickness = 15;
        double stoneToDirtChance = 0.2;
        int treeCount   = w / 30;
        int trunkHeight = 4;
        int leafRadius  = 3;

        // 1) Generate height profile by random walk
        surface[0] = h / 2;
        for (int x = 1; x < w; x++) {
            surface[x] = surface[x - 1] + rnd.nextInt(3) - 1;
            surface[x] = Math.max(h/4, Math.min(3*h/4, surface[x]));
        }
        // Smooth the profile twice
        for (int k = 0; k < 2; k++) {
            for (int x = 1; x < w - 1; x++) {
                surface[x] = (surface[x - 1] + surface[x] + surface[x + 1]) / 3;
            }
        }

        // 2) Create base tile grid
        char[][] map = new char[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (y < surface[x]) {
                    map[y][x] = '.';
                } else if (y == surface[x]) {
                    map[y][x] = 'G';
                } else if (y < surface[x] + baseDirtThickness) {
                    map[y][x] = 'D';
                } else {
                    map[y][x] = rnd.nextDouble() < stoneToDirtChance ? 'D' : 'S';
                }
            }
        }

        // 3) Plant trees
        for (int i = 0; i < treeCount; i++) {
            int tx = rnd.nextInt(w);
            int sy = surface[tx] - 1;
            if (sy - trunkHeight - leafRadius < 0) {
                i--; continue;
            }
            boolean canPlace = true;
            for (int dy = 0; dy <= trunkHeight + leafRadius; dy++) {
                if (map[sy - dy][tx] != '.') { canPlace = false; break; }
            }
            if (!canPlace) { i--; continue; }

            // stump
            map[sy + 1][tx] = 'M';
            // trunk
            for (int dy = 0; dy < trunkHeight; dy++) {
                map[sy - dy][tx] = 'T';
            }
            // leaves in a circle
            for (int dx = -leafRadius; dx <= leafRadius; dx++) {
                for (int dy = -leafRadius; dy <= leafRadius; dy++) {
                    if (dx*dx + dy*dy <= leafRadius*leafRadius) {
                        int lx = tx + dx, ly = sy - trunkHeight + dy;
                        if (lx >= 0 && lx < w && ly >= 0 && ly < h && map[ly][lx] == '.') {
                            map[ly][lx] = 'E';
                        }
                    }
                }
            }
        }

        // 4) Carve caves with random walkers
        int walkers = w / 15;
        for (int i = 0; i < walkers; i++) {
            int wx = rnd.nextInt(w), wy = rnd.nextInt(h);
            for (int step = 0; step < w * 3; step++) {
                map[wy][wx] = '.';
                int dir = rnd.nextInt(4);
                wx = Math.max(1, Math.min(w - 2, wx + (dir==0?1:dir==1?-1:0)));
                wy = Math.max(1, Math.min(h - 2, wy + (dir==2?1:dir==3?-1:0)));
            }
        }

        // 5) Autotile grass edges on dirt blocks adjacent to air
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (map[y][x] != 'D') continue;
                int m = 0;
                if (y>0   && map[y-1][x] == '.') m |= 1;
                if (x<w-1 && map[y][x+1] == '.') m |= 2;
                if (y<h-1 && map[y+1][x] == '.') m |= 4;
                if (x>0   && map[y][x-1] == '.') m |= 8;
                if ((m & 1)!=0)      map[y][x] = 'G';
                else if ((m & 8)!=0) map[y][x] = 'L';
                else if ((m & 2)!=0) map[y][x] = 'R';
                else if ((m & 4)!=0) map[y][x] = 'B';
            }
        }

        // 6) Write map and random item spawns to file
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (char[] row : map) {
                pw.println(new String(row));
            }
            int bananaCount = 5 + rnd.nextInt(6);
            for (int i = 0; i < bananaCount; i++) {
                int bx = rnd.nextInt(w);
                int by = surface[bx] - 1;
                pw.printf("ITEM %s %d %d%n", "banana", bx, by);
            }
            int centerX = w / 2;
            int centerY = surface[centerX] - 1;
            pw.printf("NPC %s %d %d%n", "bro", centerX, centerY);
        }
    }


    /**
     * A simple CLI entry point for quick testing.
     * Generates a 100×50 map using the current time as seed,
     * and writes it to "src/main/resources/map3.txt".
     */
    public static void main(String[] args) throws IOException {
        generatePerlinLike("src/main/resources/map1.txt", 100, 100, System.currentTimeMillis());
    }
}
