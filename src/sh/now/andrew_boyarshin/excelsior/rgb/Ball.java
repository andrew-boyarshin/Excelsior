package sh.now.andrew_boyarshin.excelsior.rgb;

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public final class Ball {
    public final BallColor color;
    private Cluster cluster;

    public Ball(BallColor color) {
        this.color = color;
    }

    public Cluster cluster() {
        return cluster;
    }

    void assignToCluster(@NotNull Cluster cluster) {
        assert cluster.color == color;
        this.cluster = cluster;
    }

    void resetCluster() {
        this.cluster = null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Ball.class.getSimpleName() + "[", "]")
                .add("color=" + color)
                .add("cluster=" + cluster)
                .toString();
    }
}
