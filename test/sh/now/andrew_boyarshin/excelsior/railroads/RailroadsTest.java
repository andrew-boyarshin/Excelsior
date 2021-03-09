package sh.now.andrew_boyarshin.excelsior.railroads;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class RailroadsTest {
    private static final ISolver NAIVE = new NaiveSolverImpl();
    private static final ISolver SEGMENT_TREE = new SegmentTreeSolverImpl();
    private static final ISolver[] SOLVERS = {NAIVE, SEGMENT_TREE};

    private static @NotNull
    List<Boolean> solve(ISolver impl, String cases, int n) {
        final var startTime = System.nanoTime();
        try {
            try (var scanner = new Scanner(cases)) {
                assertTrue(scanner.hasNext());
                return Railroads.solveMultiple(impl, scanner, n);
            }
        } finally {
            // Absolutely wrong way to benchmark anything.
            // Can give a first impression even without profiler though.
            System.err.println(System.nanoTime() - startTime);
        }
    }

    private static List<Boolean> solve(String cases, int n) {
        var solverResults = Arrays.stream(SOLVERS).map(x -> solve(x, cases, n)).collect(Collectors.toList());
        solverResults.stream().skip(1).forEach(x -> assertIterableEquals(solverResults.get(0), x));
        return solverResults.get(0);
    }

    @Test
    void n_4() {
        var result = solve(
                """
                        1 2 3 4
                        4 3 2 1
                        2 1 3 4
                        1 4 3 2
                        3 2 1 4
                        4 3 1 2
                        4 1 2 3
                        0""",
                4
        );
        assertIterableEquals(Arrays.asList(true, true, true, true, true, false, false), result);
    }

    @Test
    void n_5() {
        var result = solve(
                """
                        1 2 3 4 5
                        5 4 1 2 3
                        3 2 4 1 5
                        3 2 5 1 4
                        3 2 5 4 1
                        0""",
                5
        );
        assertIterableEquals(Arrays.asList(true, false, true, false, true), result);
    }

    @Test
    void n_6() {
        var result = solve(
                """
                        6 5 4 3 2 1
                        1 2 3 4 5 6
                        2 1 4 3 6 5
                        2 1 4 3 5 6
                        2 4 3 5 6 1
                        4 3 5 6 1 2
                        0""",
                6
        );
        assertIterableEquals(Arrays.asList(true, true, true, true, true, false), result);
    }
}
