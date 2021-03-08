package sh.now.andrew_boyarshin.excelsior.rgb;

import org.jetbrains.annotations.Contract;

public class Utilities {
    @Contract(pure = true)
    private static int pow2(int x) {
        return x * x;
    }

    @Contract(pure = true)
    static int ballCountToScore(int x) {
        return pow2(x - 2);
    }

    @Contract(pure = true)
    public static boolean inRangeColumn(int column) {
        return 0 < column && column <= SolverImpl.COLUMN_COUNT;
    }

    @Contract(pure = true)
    public static boolean inRangeRow(int row) {
        return 0 < row && row <= SolverImpl.ROW_COUNT;
    }

    @Contract(pure = true)
    public static boolean inRange(int row, int column) {
        return inRangeRow(row) && inRangeColumn(column);
    }

    @Contract(pure = true)
    public static boolean inRange(Point point) {
        return inRange(point.row(), point.column());
    }
}
