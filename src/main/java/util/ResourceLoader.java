package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Utility class for loading text resources from the classpath.
 * <p>
 * Provides methods to read files packaged in the JAR (or in the classpath)
 * as UTF-8 text.
 * </p>
 */
public class ResourceLoader {

    /**
     * Reads all lines from a text resource located on the classpath.
     * <p>
     * The {@code path} may be provided with or without a leading slash;
     * this method will ensure it is looked up correctly.
     * Lines are interpreted as UTF-8 text.
     * </p>
     *
     * @param path the resource path, e.g. "/levels/map1.txt" or "config/settings.cfg"
     * @return a {@link List} of all lines in the resource, in original order
     * @throws RuntimeException if the resource is not found or an I/O error occurs
     */
    public static List<String> readResourceLines(String path) {
        // Ensure a leading '/'
        String actualPath = path.startsWith("/") ? path : "/" + path;

        try (InputStream is = ResourceLoader.class.getResourceAsStream(actualPath);
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8)))
        {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + actualPath);
            }
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource " + actualPath, e);
        }
    }
}
