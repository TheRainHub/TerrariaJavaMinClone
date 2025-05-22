package world;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry that maps characters from level files to {@link TileType} values
 * and loads the corresponding tile textures.
 * <p>
 * When parsing a level, you translate each character (e.g. '.', 'G', 'D') into a
 * {@link TileType} using {@link #fromChar(char)}.  Then you render using the
 * texture for that type obtained via {@link #getTexture(TileType)} or retrieve
 * the full map of textures via {@link #getAllTextures()}.
 * </p>
 */
public class TileRegistry {
    /** Maps a level-file character to its corresponding TileType. */
    private final Map<Character, TileType> charToTile = new HashMap<>();
    /** Holds loaded Image textures for each TileType. */
    private final Map<TileType, Image> tileTextures = new HashMap<>();

    /**
     * Constructs a TileRegistry by registering all known characters
     * and then loading their textures from the classpath.
     */
    public TileRegistry() {
        // Register char→TileType mappings
        register('.', TileType.AIR);
        register('G', TileType.GRASS_TOP);
        register('L', TileType.GRASS_LEFT);
        register('R', TileType.GRASS_RIGHT);
        register('B', TileType.GRASS_BOTTOM);
        register('D', TileType.DIRT);
        register('S', TileType.STONE);
        register('M', TileType.TREE_MAIN);
        register('T', TileType.TREE_TRUNK);
        register('E', TileType.TREE_LEAVES);
        // Load textures for all registered types
        loadTextures();
    }

    /**
     * Associates a single character with a TileType.
     *
     * @param c        the character used in a level file
     * @param tileType the TileType to map to
     */
    private void register(char c, TileType tileType) {
        charToTile.put(c, tileType);
    }

    /**
     * Loads PNG textures for each {@link TileType} that has a texture key.
     * <p>
     * Looks up resources under "/tiles/&lt;key&gt;.png" where key is
     * {@code type.getTextureKey()}.  If a texture file is missing, logs an error
     * and skips that type.
     * </p>
     */
    private void loadTextures() {
        for (TileType type : TileType.values()) {
            String key = type.getTextureKey();
            if (key == null) continue;
            var url = getClass().getResource("/tiles/" + key + ".png");
            if (url == null) {
                System.err.println("Texture not found: " + key);
                continue;
            }
            tileTextures.put(type, new Image(url.toExternalForm()));
        }
    }

    /**
     * Converts a level-file character into the corresponding TileType.
     *
     * @param c the character read from the level file
     * @return the mapped TileType, or {@link TileType#AIR} if the character is unrecognized
     */
    public TileType fromChar(char c) {
        return charToTile.getOrDefault(c, TileType.AIR);
    }

    /**
     * Retrieves the preloaded Image for a given TileType.
     *
     * @param type the TileType whose texture is requested
     * @return the Image, or {@code null} if no texture was loaded for this type
     */
    public Image getTexture(TileType type) {
        return tileTextures.get(type);
    }

    /**
     * Returns the entire map of loaded tile textures.
     *
     * @return a map from TileType to its Image texture
     */
    public Map<TileType, Image> getAllTextures() {
        return tileTextures;
    }
}
