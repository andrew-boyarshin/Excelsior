package sh.now.andrew_boyarshin.excelsior.digital_lab;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public final class Utilities {
    public static <T> boolean isInStrictOrder(@NotNull Iterable<? extends T> iterable, @NotNull Comparator<T> comparator) {
        var it = iterable.iterator();
        if (it.hasNext()) {
            var prev = it.next();
            while (it.hasNext()) {
                var next = it.next();
                if (comparator.compare(prev, next) >= 0)
                    return false;

                prev = next;
            }
        }
        return true;
    }
}
