package sh.now.andrew_boyarshin.excelsior.pizza;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public final class Utilities {
    public static final int[] NORTH_DIFFERENCE = {0, 1};
    public static final int[] EAST_DIFFERENCE = {1, 0};
    public static final int[] SOUTH_DIFFERENCE = {0, -1};
    public static final int[] WEST_DIFFERENCE = {-1, 0};

    @Contract(pure = true)
    public static int @NotNull [] addPosition(int x, int y, Direction direction, int distance) {
        return addPosition(x, y, directionToPositionDifference(direction), distance);
    }

    @Contract(pure = true)
    public static int @NotNull [] addPosition(int x, int y, int[] positionDifference, int distance) {
        assert positionDifference != null && positionDifference.length == 2;
        assert distance > 0;

        var xDiff = positionDifference[0];
        var yDiff = positionDifference[1];
        assert xDiff == 0 || yDiff == 0;

        return new int[]{x + xDiff * distance, y + yDiff * distance};
    }

    @Contract(value = "_ -> new", pure = true)
    public static int @NotNull [] directionToPositionDifference(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH_DIFFERENCE;
            case EAST -> EAST_DIFFERENCE;
            case SOUTH -> SOUTH_DIFFERENCE;
            case WEST -> WEST_DIFFERENCE;
        };
    }

}
