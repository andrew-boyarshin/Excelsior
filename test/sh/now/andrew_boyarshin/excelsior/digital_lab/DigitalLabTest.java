package sh.now.andrew_boyarshin.excelsior.digital_lab;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class DigitalLabTest {
    private static final ISolver AHO_CORASICK_SOLVER = new AhoCorasickSolverImpl();

    private static Collection<Match> solve(ISolver impl, FlatMatrix haystack, PooledRowMatrix needlePool) {
        return impl.solve(haystack, needlePool);
    }

    private static Collection<Match> solve(FlatMatrix haystack, PooledRowMatrix needlePool) {
        return solve(AHO_CORASICK_SOLVER, haystack, needlePool);
    }

    private static FlatMatrix matrixPlain(int rows, int columns, int... data) {
        assertEquals(rows * columns, data.length);

        var array = new boolean[data.length];

        for (int i = 0; i < data.length; i++) {
            final var item = data[i];
            assertTrue(item == 1 || item == 0);
            array[i] = item == 1;
        }

        return new FlatMatrix(rows, columns, array);
    }

    private static void assertSolution(FlatMatrix haystack, PooledRowMatrix needlePool, int... startPositions) {
        assertNotNull(haystack);
        assertNotNull(needlePool);
        var actual = solve(haystack, needlePool).toArray(Match[]::new);
        assertEquals(0, startPositions.length % 2);
        assertEquals(startPositions.length / 2, actual.length);
        for (int i = 0; i < actual.length; i++) {
            var match = actual[i];
            assertEquals(startPositions[i * 2], match.startRowInclusive());
            assertEquals(startPositions[i * 2 + 1], match.startColumnInclusive());
        }
    }

    @Test
    void sample_1() {
        try (var pool = new ScopedArrayPool(2, 2)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            1, 0,
                            1, 1
                    ),
                    0, 1,
                    2, 0,
                    3, 3
            );
        }
    }

    @Test
    void sample_2() {
        try (var pool = new ScopedArrayPool(1, 1)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            1
                    ),
                    0, 0,
                    0, 1,
                    1, 1,
                    1, 2,
                    2, 0,
                    2, 3,
                    3, 0,
                    3, 1,
                    3, 2,
                    3, 3,
                    4, 2,
                    4, 3,
                    4, 4
            );
        }
    }

    @Test
    void sample_3() {
        try (var pool = new ScopedArrayPool(1, 1)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            0
                    ),
                    0, 2,
                    0, 3,
                    0, 4,
                    1, 0,
                    1, 3,
                    1, 4,
                    2, 1,
                    2, 2,
                    2, 4,
                    3, 4,
                    4, 0,
                    4, 1
            );
        }
    }

    @Test
    void sample_4() {
        try (var pool = new ScopedArrayPool(2, 6)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            1, 0, 0, 1, 0, 1,
                            1, 1, 1, 0, 1, 0
                    )
            );
        }
    }

    @Test
    void sample_5() {
        try (var pool = new ScopedArrayPool(2, 5)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            1, 0, 0, 1, 0,
                            1, 1, 1, 0, 1
                    )
            );
        }
    }

    @Test
    void sample_6() {
        try (var pool = new ScopedArrayPool(2, 5)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0
                    ),
                    2, 0
            );
        }
    }

    @Test
    void sample_7() {
        try (var pool = new ScopedArrayPool(2, 2)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            1, 1,
                            1, 1
                    ),
                    3, 2
            );
        }
    }

    @Test
    void sample_8() {
        try (var pool = new ScopedArrayPool(2, 2)) {
            assertSolution(
                    matrixPlain(
                            5, 5,
                            1, 1, 0, 0, 0,
                            0, 1, 1, 0, 0,
                            1, 0, 0, 1, 0,
                            1, 1, 1, 1, 0,
                            0, 0, 1, 1, 1
                    ),
                    pool.needle(
                            0, 0,
                            0, 0
                    ),
                    0, 3
            );
        }
    }

    private static final class ScopedArrayPool implements AutoCloseable {
        private final int rows;
        private final int columns;
        private final PooledRowMatrix instance;

        private ScopedArrayPool(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            this.instance = new PooledRowMatrix(rows, columns);

            assertNull(PooledRowMatrix.instance);
            PooledRowMatrix.instance = instance;
        }

        private PooledRowMatrix needle(int... data) {
            assertEquals(rows * columns, data.length);

            for (int i = 0; i < rows; i++) {
                var row = this.instance.allocateStatic(i);
                for (int j = 0; j < columns; j++) {
                    final var item = data[i * columns + j];
                    assertTrue(item == 1 || item == 0);
                    row[j] = item == 1;
                }
            }

            return instance;
        }

        @Override
        public void close() {
            assertEquals(instance, PooledRowMatrix.instance);
            PooledRowMatrix.instance = null;
        }
    }
}
