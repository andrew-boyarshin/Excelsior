package sh.now.andrew_boyarshin.excelsior.rgb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
interface TriConsumer {
    void accept(@NotNull Ball ball, @Nullable Cluster cluster, @Nullable Point point);
}

public interface Snapshot {
    Ball get(@NotNull Point point);

    void forEachNeighbour(@NotNull Point p, @NotNull TriConsumer consumer);
}
