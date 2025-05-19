package world;

public enum TileType {
    AIR, DIRT;
    public boolean isSolid() {
        return this == DIRT;
    }
}
