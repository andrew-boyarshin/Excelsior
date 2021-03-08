package sh.now.andrew_boyarshin.excelsior.rgb;

import org.jetbrains.annotations.NotNull;

import java.util.List;

final class SnapshotFull implements Snapshot {
    private static final int rowCount = SolverImpl.ROW_COUNT;
    private static Ball[] cache;
    private final Ball[] map;
    private final int length;

    public SnapshotFull(List<Ball[]> columns) {
        length = columns.size() * rowCount;
        if (cache != null && cache.length >= length) {
            map = cache;
        } else {
            map = new Ball[length];
            cache = map;
        }

        var position = 0;
        for (var column : columns) {
            System.arraycopy(column, 0, map, position, rowCount);
            position += rowCount;
        }
    }

    public Ball get(int row, int column) {
        if (row == 0 || column == 0)
            return null;
        final var index = (column - 1) * rowCount + row - 1;
        if (index >= length)
            return null;
        return map[index];
    }

    @Override
    public Ball get(@NotNull Point point) {
        return get(point.row(), point.column());
    }

    @Override
    public void forEachNeighbour(@NotNull Point p, @NotNull TriConsumer consumer) {
        final var row = p.row();
        final var column = p.column();

        Ball ball;
        Cluster cluster;

        ball = get(row - 1, column);
        if (ball != null) {
            cluster = ball.cluster();
            consumer.accept(ball, cluster, cluster != null ? null : new Point(row - 1, column));
        }

        ball = get(row + 1, column);
        if (ball != null) {
            cluster = ball.cluster();
            consumer.accept(ball, cluster, cluster != null ? null : new Point(row + 1, column));
        }

        ball = get(row, column - 1);
        if (ball != null) {
            cluster = ball.cluster();
            consumer.accept(ball, cluster, cluster != null ? null : new Point(row, column - 1));
        }

        ball = get(row, column + 1);
        if (ball != null) {
            cluster = ball.cluster();
            consumer.accept(ball, cluster, cluster != null ? null : new Point(row, column + 1));
        }
    }
}
