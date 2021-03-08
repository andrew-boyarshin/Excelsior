package sh.now.andrew_boyarshin.excelsior.rgb;

public record Point(int row, int column) {
    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }
}
