package sh.now.andrew_boyarshin.excelsior.digital_lab;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class DigitalLab {
    public static void main(String[] args) {
        ISolver impl = new AhoCorasickSolverImpl();
        try (var scanner = new Scanner(System.in)) {
            var needle = readMatrix2D(scanner);
            var haystack = readMatrixPlain(scanner);
            var solution = impl.solve(haystack, needle);

            final var rows = haystack.rows();
            final var columns = haystack.columns();
            final var data = haystack.data();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    final var item = data[i * columns + j];

                    if (j != 0)
                        System.out.print(' ');

                    var finalI = i;
                    var finalJ = j;
                    final var match = solution.stream().anyMatch(x -> x.inside(finalI, finalJ));
                    final var c = item ? match ? '2' : '1' : match ? '*' : '0';
                    System.out.print(c);
                }

                System.out.println();
            }
        }
    }

    @NotNull
    private static FlatMatrix readMatrixPlain(Scanner scanner) {
        var rows = scanner.nextInt();
        var columns = scanner.nextInt();

        var data = new boolean[rows * columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i * columns + j] = scanner.nextInt() == 1;
            }
        }

        return new FlatMatrix(rows, columns, data);
    }

    @NotNull
    private static PooledRowMatrix readMatrix2D(Scanner scanner) {
        var rows = scanner.nextInt();
        var columns = scanner.nextInt();

        final var instance = new PooledRowMatrix(rows, columns);

        PooledRowMatrix.instance = instance;

        for (int i = 0; i < rows; i++) {
            var row = instance.allocateStatic(i);
            for (int j = 0; j < columns; j++) {
                row[j] = scanner.nextInt() == 1;
            }
        }

        return instance;
    }
}
