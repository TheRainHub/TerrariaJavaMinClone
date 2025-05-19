package item;

import java.util.HashSet;
import java.util.Set;

public class Inventory {
    private Set<String> items = new HashSet<>();

    public void add(String item) {
        items.add(item);
    }

    public boolean has(String item) {
        return items.contains(item);
    }
}
