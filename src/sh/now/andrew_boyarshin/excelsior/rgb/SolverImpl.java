package sh.now.andrew_boyarshin.excelsior.rgb;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolverImpl implements ISolver {
    static final int ROW_COUNT = 10;
    static final int COLUMN_COUNT = 15;

    private static final Comparator<Cluster> sizeComparator = Comparator.comparingInt(Cluster::size);
    private static final Point ORIGIN = new Point(1, 1);

    private final List<Ball[]> columnPool = new ArrayList<>();
    private final List<GameMove> moves = new ArrayList<>();
    private final List<Cluster> clusters = new ArrayList<>();
    private final List<Ball> balls = new ArrayList<>();
    private final ArrayDeque<Point> bfsQueue = new ArrayDeque<>(ROW_COUNT * COLUMN_COUNT / 2);
    private List<Ball[]> columns = null;
    private int score;

    @Contract(pure = true)
    public boolean inRangeColumn(int column) {
        return 0 < column && column <= columns.size();
    }

    private @Nullable Ball @NotNull [] allocateColumn() {
        if (columnPool.isEmpty())
            return new Ball[ROW_COUNT];

        final var index = columnPool.size() - 1;
        final var item = columnPool.get(index);
        columnPool.remove(index);
        return item;
    }

    private @Nullable Ball @NotNull [] columnAt(int c) {
        var column = columnAtOptional(c);
        assert column != null;
        return column;
    }

    @Nullable Ball @Nullable [] columnAtOptional(int c) {
        if (inRangeColumn(c))
            return columns.get(c - 1);
        return null;
    }

    @Nullable
    public Ball atOptional(int r, @Nullable Ball @NotNull [] column) {
        if (r <= 0)
            return null;

        assert column.length == SolverImpl.ROW_COUNT;

        if (r > SolverImpl.ROW_COUNT)
            return null;

        return column[r - 1];
    }

    @Nullable
    public Ball atOptional(int r, int c) {
        final var column = columnAtOptional(c);

        if (column == null)
            return null;

        return atOptional(r, column);
    }

    @NotNull
    public Ball at(int r, int c) {
        var ball = atOptional(r, c);
        assert ball != null;
        return ball;
    }

    @NotNull
    public Ball at(Point p) {
        return at(p.row(), p.column());
    }

    private void set(int r, int c, BallColor color) {
        final var ball = new Ball(color);

        assert Utilities.inRangeRow(r);

        columnAt(c)[r - 1] = ball;
        balls.add(ball);
    }

    private void destroyColumn(int c) {
        var column = columnAt(c);

        assert Arrays.stream(column).allMatch(Objects::isNull);

        columnPool.add(column);
    }

    @Override
    public GameSolution solve(BallColor[][] balls) {
        if (columns == null) {
            columns = IntStream.range(0, COLUMN_COUNT)
                    .mapToObj(x -> allocateColumn())
                    .collect(Collectors.toList());
        } else {
            assert columns.size() <= COLUMN_COUNT;
            while (columns.size() < COLUMN_COUNT)
                columns.add(allocateColumn());
        }

        score = 0;
        moves.clear();
        clusters.clear();
        this.balls.clear();

        resetMap(balls);

        boolean move;

        do {
            cluster();

            move = dropLargestCluster();
        } while (move);

        final var size = this.balls.size();
        assert columns.stream().flatMap(Arrays::stream).filter(Objects::nonNull).count() == size;

        if (size == 0)
            score += 1000;

        return new GameSolution(score, size, moves);
    }

    private boolean dropLargestCluster() {
        var maxClusterOpt = clusters.stream().max(sizeComparator);
        if (maxClusterOpt.isEmpty())
            return false;

        var maxCluster = maxClusterOpt.get();

        final var size = maxCluster.size();

        if (size < 2)
            return false;

        moves.add(new GameMove(maxCluster.position(), size, maxCluster.color));
        score += Utilities.ballCountToScore(size);

        var emptyColumns = new ArrayList<Ball[]>();

        maxCluster.forEachColumn((column, items) -> {
            items.forEach(x -> {
                assert x.ball() == at(x.point());
            });

            final var rows = items.stream().mapToInt(x -> x.point().row()).sorted().toArray();
            final var columnArray = columnAt(column);

            final var currentCount = (int) Arrays.stream(columnArray).takeWhile(Objects::nonNull).count();
            assert currentCount <= ROW_COUNT;

            final var newRowSet = IntStream.rangeClosed(1, currentCount)
                    .filter(x -> Arrays.binarySearch(rows, x) < 0)
                    .mapToObj(x -> columnArray[x - 1])
                    .toArray(Ball[]::new);

            final var newCount = newRowSet.length;
            assert newCount < currentCount;

            System.arraycopy(newRowSet, 0, columnArray, 0, newCount);
            Arrays.fill(columnArray, newCount, currentCount, null);

            if (newCount == 0) {
                destroyColumn(column);
                emptyColumns.add(columnArray);
            }
        });

        this.columns.removeAll(emptyColumns);

        if (!this.clusters.remove(maxCluster))
            throw new AssertionError();

        if (!this.balls.removeAll(maxCluster.toBallCollection()))
            throw new AssertionError();

        return true;
    }

    private void resetMap(BallColor[][] balls) {
        assert balls.length == ROW_COUNT;
        for (int i = 0; i < ROW_COUNT; i++) {
            var row = balls[i];
            assert row.length == COLUMN_COUNT;
            for (int j = 0; j < COLUMN_COUNT; j++) {
                set(ROW_COUNT - i, j + 1, row[j]);
            }
        }
    }

    private void mergeClusters(@NotNull Cluster first, @NotNull Cluster second) {
        first.mergeCluster(second);
        clusters.remove(second);
    }

    private void cluster() {
        for (var ball : balls)
            ball.resetCluster();
        for (var cluster : clusters)
            cluster.destroy();
        clusters.clear();

        if (balls.isEmpty())
            return;

        assert bfsQueue.isEmpty();

        Snapshot snapshot;
        snapshot = new SnapshotBasic(this);
        //snapshot = new SnapshotFull(columns);

        bfsQueue.addFirst(ORIGIN);

        while (!bfsQueue.isEmpty()) {
            final var point = bfsQueue.removeFirst();
            final var ball = snapshot.get(point);
            assert ball != null;
            final var cluster = getCluster(point, ball);
            final var color = ball.color;

            snapshot.forEachNeighbour(point, (neighbourBall, neighbourCluster, neighbourPoint) -> {
                assert (neighbourPoint != null) == (neighbourCluster == null);

                if (neighbourBall.color == color) {
                    if (neighbourCluster != null) {
                        assert neighbourCluster.color == color;

                        if (neighbourCluster != cluster) {
                            mergeClusters(cluster, neighbourCluster);
                        }

                        return;
                    }

                    cluster.add(neighbourPoint, neighbourBall);
                } else if (neighbourCluster == null) {
                    clusters.add(new Cluster(neighbourPoint, neighbourBall));
                }

                if (neighbourCluster == null)
                    bfsQueue.addLast(neighbourPoint);
            });
        }
    }

    @NotNull
    private Cluster getCluster(Point point, Ball ball) {
        var cluster = ball.cluster();

        if (cluster != null)
            return cluster;

        cluster = new Cluster(point, ball);
        clusters.add(cluster);

        return cluster;
    }
}
