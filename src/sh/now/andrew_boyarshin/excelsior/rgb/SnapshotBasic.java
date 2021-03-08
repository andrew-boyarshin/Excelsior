package sh.now.andrew_boyarshin.excelsior.rgb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SnapshotBasic implements Snapshot {
    private final SolverImpl instance;

    public SnapshotBasic(SolverImpl instance) {
        this.instance = instance;
    }

    @Nullable
    @Override
    public Ball get(@NotNull Point point) {
        return instance.atOptional(point.row(), point.column());
    }

    @Override
    public void forEachNeighbour(@NotNull Point p, @NotNull TriConsumer consumer) {
        final var row = p.row();
        final var column = p.column();

        final var columnM = instance.columnAtOptional(column);
        final var columnL = instance.columnAtOptional(column - 1);
        final var columnR = instance.columnAtOptional(column + 1);

        Ball ball;
        Cluster cluster;

        if (columnM != null) {
            ball = instance.atOptional(row - 1, columnM);
            if (ball != null) {
                cluster = ball.cluster();
                consumer.accept(ball, cluster, cluster != null ? null : new Point(row - 1, column));
            }

            ball = instance.atOptional(row + 1, columnM);
            if (ball != null) {
                cluster = ball.cluster();
                consumer.accept(ball, cluster, cluster != null ? null : new Point(row + 1, column));
            }
        }

        if (columnL != null) {
            ball = instance.atOptional(row, columnL);
            if (ball != null) {
                cluster = ball.cluster();
                consumer.accept(ball, cluster, cluster != null ? null : new Point(row, column - 1));
            }
        }

        if (columnR != null) {
            ball = instance.atOptional(row, columnR);
            if (ball != null) {
                cluster = ball.cluster();
                consumer.accept(ball, cluster, cluster != null ? null : new Point(row, column + 1));
            }
        }
    }
}
