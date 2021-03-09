package sh.now.andrew_boyarshin.excelsior.digital_lab;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ISolver {
    @NotNull Collection<Match> solve(@NotNull FlatMatrix haystack, @NotNull PooledRowMatrix needlePool);
}
