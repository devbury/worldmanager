package devbury.worldmanager.domain;

public enum GameMode {

    CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
