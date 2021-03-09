package sh.now.andrew_boyarshin.excelsior.rgb;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class Cluster {
    public final BallColor color;

    // ballSet is actually redundant, itemMap can be made a single source of truth.
    // But it's a nice perf optimization, and I totally don't care about memory in these tasks.
    // If I did, I would've written these tasks in a language where I'm certain how value types,
    // reference types and arrays represented in memory and interop with each other. That is, C/C++/C#.
    private Set<Ball> ballSet = new HashSet<>();

    private TreeMap<Integer, List<ClusterItem>> itemMap = new TreeMap<>();

    private Point position;

    public Cluster(@NotNull Point point, @NotNull Ball ball) {
        this.color = ball.color;
        position = point;
        add(new ClusterItem(point, ball), false);
    }

    public int size() {
        final var size = ballSet.size();
        assert size > 0;
        return size;
    }

    public void forEachColumn(BiConsumer<Integer, Collection<ClusterItem>> function) {
        itemMap.forEach(function);
    }

    public Point position() {
        return position;
    }

    private void add(@NotNull ClusterItem item, boolean updatePosition) {
        final var point = item.point();
        final var ball = item.ball();
        final var column = point.column();

        assert color == ball.color;
        ball.assignToCluster(this);
        final var success = ballSet.add(ball);
        assert success;
        assert Utilities.inRange(point);
        itemMap.compute(column, (k, v) -> {
            if (v == null) {
                return new ArrayList<>(SolverImpl.ROW_COUNT) {{
                    add(item);
                }};
            }

            v.add(item);
            return v;
        });

        // skip updating position in Cluster constructor
        if (updatePosition) {
            final var oldColumn = position.column();

            if (column > oldColumn)
                return;

            if (column < oldColumn || point.row() < position.row())
                position = point;
        }
    }

    public void add(@NotNull Point point, @NotNull Ball ball) {
        add(new ClusterItem(point, ball), true);
    }

    public void mergeCluster(@NotNull Cluster cluster) {
        // TODO: faster merge using Cluster add impl details
        // upd: negative, the impact of this turned out to be very low during perf profiling.
        //      this will work 100%.
        for (var e : cluster.itemMap.entrySet())
            for (var v : e.getValue())
                add(v, true);

        cluster.destroy();
    }

    public void destroy() {
        for (var ball : ballSet)
            assert ball.cluster() != this;

        ballSet.clear();

        // Make this instance explicitly invalid
        ballSet = null;
        itemMap = null;
    }

    public Collection<Ball> toBallCollection() {
        return Collections.unmodifiableCollection(ballSet);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Cluster.class.getSimpleName() + "[", "]")
                .add("color=" + color)
                .add("ballSet=#" + ballSet.size())
                .add("itemMap=#" + itemMap.size())
                .toString();
    }
}
