package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ResourceLoader {
    public static List<String> readResourceLines(String path) {
        // Добавляем ведущий slash, если его нет
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
