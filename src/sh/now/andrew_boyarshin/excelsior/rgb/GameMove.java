package sh.now.andrew_boyarshin.excelsior.rgb;

import java.util.StringJoiner;

public record GameMove(Point point, int count, BallColor color) {
    public GameMove {
    }

    public GameMove(int row, int column, int count, BallColor color) {
        this(new Point(row, column), count, color);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GameMove.class.getSimpleName() + point + "[", "]")
                .add("count=" + count)
                .add("color=" + color)
                .toString();
    }
}
