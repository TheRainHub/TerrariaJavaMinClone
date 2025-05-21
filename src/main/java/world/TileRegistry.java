package world;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class TileRegistry {
    private final Map<Character, TileType> charToTile = new HashMap<>();
    private final Map<TileType, Image> tileTextures = new HashMap<>();

    public TileRegistry() {
        register('.', TileType.AIR);
        register('G', TileType.GRASS_TOP);
        register('L', TileType.GRASS_LEFT);
        register('R', TileType.GRASS_RIGHT);
        register('B', TileType.GRASS_BOTTOM);
        register('D', TileType.DIRT);
        register('S', TileType.STONE);
        register('T', TileType.TREE_TRUNK);
        register('E', TileType.TREE_LEAVES);
        loadTextures();
    }


    private void register(char c, TileType tileType) {
        charToTile.put(c, tileType);
    }

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

    public TileType fromChar(char c) {
        return charToTile.getOrDefault(c, TileType.AIR);
    }

    public Image getTexture(TileType type) {
        return tileTextures.get(type);
    }

    public Map<TileType, Image> getAllTextures() {
        return tileTextures;
    }
}
