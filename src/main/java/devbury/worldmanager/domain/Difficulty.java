package devbury.worldmanager.domain;

public enum Difficulty {
    EASY, PEACEFUL, NORMAL, HARD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
