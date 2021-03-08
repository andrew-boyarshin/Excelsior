package sh.now.andrew_boyarshin.excelsior.rgb;

public enum BallColor {
    RED, GREEN, BLUE;

    public static BallColor parse(int c) {
        return switch (c) {
            case 'R' -> RED;
            case 'G' -> GREEN;
            case 'B' -> BLUE;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case RED -> "R";
            case GREEN -> "G";
            case BLUE -> "B";
        };
    }
}
