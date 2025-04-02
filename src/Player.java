public class Player {
    private final String name;
    private final int storeIndex;
    private final int startPit;
    private final int endPit;

    public Player(String name, int startPit, int endPit, int storeIndex) {
        this.name = name;
        this.startPit = startPit;
        this.endPit = endPit;
        this.storeIndex = storeIndex;
    }

    public String getName() {
        return name;
    }

    public int getStoreIndex() {
        return storeIndex;
    }

    public boolean ownsPit(int index) {
        return index >= startPit && index <= endPit;
    }
}
