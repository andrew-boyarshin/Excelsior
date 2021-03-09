package sh.now.andrew_boyarshin.excelsior.digital_lab;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.OptionalInt;

public final class PooledRowMatrix {
    public static final boolean[] empty = new boolean[0];

    public static PooledRowMatrix instance;

    public final int rows;
    public final int columns;
    private final boolean[][] staticPool;
    private int[] needleHashes;
    private int[] needleIndices;
    private boolean[][] needle;

    public PooledRowMatrix(int rows, int columns) {
        staticPool = new boolean[rows][columns];
        this.rows = rows;
        this.columns = columns;
    }

    private static int hashCode(boolean @NotNull [] a) {
        int result = 1;
        for (var element : a)
            result = 31 * result + (element ? 2 : 3);

        return result;
    }

    private static int hashCode(boolean @NotNull [] a, boolean b) {
        int result = hashCode(a);
        return 31 * result + (b ? 2 : 3);
    }

    public boolean @NotNull [] @NotNull [] needle() {
        if (needle == null) {
            assert needleHashes == null;
            assert needleIndices == null;

            needleHashes = new int[rows];
            needle = new boolean[rows][];
            needleIndices = new int[rows];

            for (int i = 0; i < rows; i++)
                needleHashes[i] = hashCode(staticPool[i]);

            for (int i = 0; i < rows; i++) {
                var hash = needleHashes[i];
                var array = staticPool[i];
                for (int j = i; j < rows; j++) {
                    if (needle[j] != null)
                        continue;

                    var otherHash = needleHashes[j];
                    if (hash != otherHash)
                        continue;

                    var otherArray = staticPool[j];

                    if (j != i && !Arrays.equals(array, otherArray))
                        continue;

                    needle[j] = array;
                    needleIndices[j] = i;
                }
            }
        }

        return needle;
    }

    public boolean @NotNull [] allocateStatic(int index) {
        final var array = new boolean[columns];
        staticPool[index] = array;
        return array;
    }

    public boolean @NotNull [] append(boolean[] array, boolean value) {
        final var oldLength = array.length;
        final var newLength = oldLength + 1;

        assert newLength <= columns;
        if (newLength == columns) {
            final var newHash = hashCode(array, value);

            for (int i = 0; i < rows; i++) {
                final var hash = needleHashes[i];
                if (hash != newHash)
                    continue;

                final var row = staticPool[i];
                if (row[oldLength] != value)
                    continue;

                if (Arrays.equals(row, 0, oldLength, array, 0, oldLength))
                    return row;
            }

            throw new IllegalStateException("Unexpected trie word of max length missing from static pool.");
        } else {
            var result = new boolean[newLength];

            System.arraycopy(array, 0, result, 0, oldLength);
            result[oldLength] = value;

            return result;
        }
    }

    public boolean @NotNull [] consume(boolean[] array, int start) {
        final var oldLength = array.length;
        final var newLength = oldLength - start;

        assert newLength < columns;

        var result = new boolean[newLength];

        System.arraycopy(array, start, result, 0, newLength);

        return result;
    }

    public int indexOf(boolean @NotNull [] array) {
        var hash = hashCode(array);
        var result = OptionalInt.empty();

        for (int i = 0; i < rows; i++) {
            if (needle[i] == array) {
                assert Arrays.equals(needle[i], array);
                assert needleHashes[i] == hash;
                if (result.isEmpty())
                    result = OptionalInt.of(i);
                // return i;
            } else {
                assert !Arrays.equals(needle[i], array);
                assert needleHashes[i] != hash;
            }
        }

        if (result.isPresent())
            return result.getAsInt();

        throw new IllegalStateException("Unknown word matched from a trie.");
    }

    public int[] needleIndices() {
        return this.needleIndices;
    }
}
