package util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Inventory {
    private final Map<String,Integer> items = new LinkedHashMap<>();


/**
     * Загружает инвентарь из текстового файла формата key=value.
     */
    public void loadFromFile(String filename) throws IOException {
        items.clear();
        Path p = Paths.get(filename);
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String id = parts[0].trim();
                int qty = Integer.parseInt(parts[1].trim());
                items.put(id, qty);
            }
        }
    }

    /**
     * Сохраняет текущее состояние инвентаря в тот же файл.
     */
    public void saveToFile(String filename) throws IOException {
        Path p = Paths.get(filename);
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            for (var e : items.entrySet()) {
                pw.println(e.getKey() + "=" + e.getValue());
            }
        }
    }

    public Map<String,Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /** Увеличить количество предметов (или добавить новый) */
    public void addItem(String id, int count) {
        items.merge(id, count, Integer::sum);
    }

    /** Уменьшить количество, возвращает true, если хватило */
    public boolean removeItem(String id, int count) {
        Integer cur = items.get(id);
        if (cur == null || cur < count) return false;
        if (cur == count) items.remove(id);
        else items.put(id, cur - count);
        return true;
    }
}
