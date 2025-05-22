package world;

/**
 * Defines all collectible item types in the game, each with a unique ID and associated sprite path.
 * <p>
 * The {@link #id} corresponds to the identifier used in level files (e.g. `"banana"`),
 * and {@link #getSpritePath()} returns the location of the PNG sprite in resources.
 * </p>
 */
public enum ItemType {
    /** A banana that the player can collect. */
    BANANA("banana"),
    // /** A bow weapon. */
    // BOW("bow"),
    // /** An arrow ammunition. */
    // ARROW("arrow"),
    /** A baton collectible or usable object. */
    BATON("baton");

    /** Unique string ID, as it appears in map/item spawn files. */
    private final String id;

    /**
     * Constructs an ItemType with the given unique ID.
     *
     * @param id the identifier used in map files and inventory
     */
    ItemType(String id) {
        this.id = id;
    }

    /**
     * Returns the unique identifier of this item type.
     *
     * @return the string ID (e.g. "banana")
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the classpath resource path to this item's sprite PNG.
     *
     * @return the resource path (e.g. "/items/banana.png")
     */
    public String getSpritePath() {
        return "/items/" + id + ".png";
    }

    /**
     * Looks up an ItemType by its string ID, case-insensitive.
     *
     * @param id the ID to search for
     * @return the matching ItemType, or {@code null} if none matches
     */
    public static ItemType fromId(String id) {
        for (ItemType t : values()) {
            if (t.id.equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }
}
