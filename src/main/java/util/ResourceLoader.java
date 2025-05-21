package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceLoader {
    public static List<String> readResourceLines(String resourcePath) {
        InputStream is = ResourceLoader.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new RuntimeException("Resource not found on classpath: " + resourcePath);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed reading resource: " + resourcePath, e);
        }
    }
}
