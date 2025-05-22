package world;

public enum ItemType {
    BANANA("banana"),
//    BOW   ("bow"),
//    ARROW ("arrow"),
    BATON ("baton");

    private final String id;

    ItemType(String id) {
        this.id = id;
    }

    /** Уникальный идентификатор предмета — то же, что идёт в map.txt */
    public String getId() {
        return id;
    }

    /** Путь к файлу спрайта в ресурсах (PNG) */
    public String getSpritePath() {
        return "/items/" + id + ".png";
    }

    /** Поиск enum по строковому id. Если нет — возвращает null */
    public static ItemType fromId(String id) {
        for (ItemType t : values()) {
            if (t.id.equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }
}
