package sh.now.andrew_boyarshin.excelsior.pizza;

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pizzeria {
    public final int id, x, y, capacity;
    public int remainingCapacity;
    private int north = 0, east = 0, south = 0, west = 0, iterateConsumeCapacity = 0;
    private int northStaging = 0, eastStaging = 0, southStaging = 0, westStaging = 0;
    private CommitMode inTransaction = null;

    public Pizzeria(int id, int x, int y, int capacity) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.remainingCapacity = capacity;
    }

    public SolverPizzeriaResult toResult() {
        return new SolverPizzeriaResult(x, y, capacity, north, east, south, west);
    }

    // Transactional system to allow more efficient try..rollback pattern required for brute-forcing.
    // And yeah, I still miss C# ref fields.

    private boolean tryStageNorth(int north) {
        var diff = north - this.northStaging;

        if (diff <= 0)
            return true;

        if (diff > remainingCapacity)
            return false;

        this.northStaging += diff;

        return true;
    }

    private boolean tryStageEast(int east) {
        var diff = east - this.eastStaging;

        if (diff <= 0)
            return true;

        if (diff > remainingCapacity)
            return false;

        this.eastStaging += diff;

        return true;
    }

    private boolean tryStageSouth(int south) {
        var diff = south - this.southStaging;

        if (diff <= 0)
            return true;

        if (diff > remainingCapacity)
            return false;

        this.southStaging += diff;

        return true;
    }

    private boolean tryStageWest(int west) {
        var diff = west - this.westStaging;

        if (diff <= 0)
            return true;

        if (diff > remainingCapacity)
            return false;

        this.westStaging += diff;

        return true;
    }

    private void beginTransaction(@NotNull CommitMode mode) {
        assert inTransaction == null;

        northStaging = north;
        eastStaging = east;
        southStaging = south;
        westStaging = west;
        inTransaction = mode;
    }

    public void beginBruteForceTransaction() {
        beginTransaction(CommitMode.COMMIT_IF_CONSUMED);
    }

    /**
     * Coerce {@link CommitMode} to match the one stored in {@link Pizzeria#inTransaction} field.
     *
     * @param mode {@link CommitMode} passed to {@link Pizzeria#commit(CommitMode)}
     * @return coerced value that is supposed to match the value passed to {@link Pizzeria#beginTransaction(CommitMode)}
     */
    private CommitMode coerceCommitMode(@NotNull CommitMode mode) {
        return switch (mode) {
            case DROP_COMMIT -> CommitMode.COMMIT_IF_CONSUMED;
            default -> mode;
        };
    }

    private boolean commit(@NotNull CommitMode mode) {
        assert inTransaction != null && inTransaction == coerceCommitMode(mode);

        var northDiff = northStaging - north;
        var eastDiff = eastStaging - east;
        var southDiff = southStaging - south;
        var westDiff = westStaging - west;

        var diffs = Stream.of(northDiff, eastDiff, southDiff, westDiff).collect(Collectors.toList());

        assert diffs.stream().allMatch(x -> x >= 0);

        inTransaction = null;

        final var diffSum = diffs.stream().mapToInt(Integer::intValue).sum();

        switch (mode) {
            case SIMPLE_COMMIT -> {
                if (diffSum > remainingCapacity)
                    return false;
            }
            case COMMIT_IF_CONSUMED -> {
                if (diffSum != remainingCapacity)
                    return false;
            }
            case DROP_COMMIT -> {
                return false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        // Funny thing: PVS-Studio (for Java) fails to understand control flow in this function
        //              and assumes the following code unreachable. That's obviously false.
        //              If I had to guess, it's due to the switch expression with return statements.

        remainingCapacity -= diffSum;
        north = northStaging;
        east = eastStaging;
        south = southStaging;
        west = westStaging;

        return true;
    }

    public boolean commitBruteForceTransaction() {
        return commit(CommitMode.COMMIT_IF_CONSUMED);
    }

    public boolean dropBruteForceTransaction() {
        return commit(CommitMode.DROP_COMMIT);
    }

    private boolean tryStageDirection(Direction direction, int value) {
        assert inTransaction != null;

        return switch (direction) {
            case NORTH -> tryStageNorth(value);
            case EAST -> tryStageEast(value);
            case SOUTH -> tryStageSouth(value);
            case WEST -> tryStageWest(value);
        };
    }

    private int getDirection(@NotNull Direction direction) {
        assert inTransaction == null;

        return switch (direction) {
            case NORTH -> north;
            case EAST -> east;
            case SOUTH -> south;
            case WEST -> west;
        };
    }

    private void fakeFeedForDefiniteIteration(Direction direction, int value) {
        var diff = value - getDirection(direction);

        if (diff <= 0)
            return;

        this.iterateConsumeCapacity += diff;
    }

    private boolean tryFeedImpl(int x, int y) {
        var direction = relativeToThis(x, y);
        var distance = getDistance(x, y);

        return tryStageDirection(direction, distance);
    }

    private boolean tryFeed(int x, int y) {
        beginTransaction(CommitMode.SIMPLE_COMMIT);

        var result = tryFeedImpl(x, y);

        if (!commit(CommitMode.SIMPLE_COMMIT))
            result = false;

        return result;
    }

    private void fakeFeedForDefiniteIteration(int x, int y) {
        var direction = relativeToThis(x, y);
        var distance = getDistance(x, y);

        fakeFeedForDefiniteIteration(direction, distance);
    }

    public boolean tryFeed(Block block) {
        return tryFeed(block.x, block.y);
    }

    public boolean tryBruteForceFeed(Block block) {
        return tryFeedImpl(block.x, block.y);
    }

    public void fakeFeedForDefiniteIteration(Block block) {
        fakeFeedForDefiniteIteration(block.x, block.y);
    }

    public void resetFakeFeedForDefiniteIteration() {
        iterateConsumeCapacity = 0;
    }

    public boolean successFakeFeedForDefiniteIteration() {
        assert iterateConsumeCapacity >= remainingCapacity;

        return iterateConsumeCapacity == remainingCapacity;
    }

    private int getDistance(int x, int y) {
        assert (this.x == x) != (this.y == y);

        return Math.abs(this.x - x) + Math.abs(this.y - y);
    }

    private Direction relativeToThis(int x, int y) {
        assert (this.x == x) != (this.y == y);

        if (this.x != x)
            return x < this.x ? Direction.WEST : Direction.EAST;

        if (this.y != y)
            return y < this.y ? Direction.SOUTH : Direction.NORTH;

        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Pizzeria.class.getSimpleName() + "(" + x + "," + y + ")[", "]")
                .add("id=" + id)
                .add("capacity=" + remainingCapacity + "/" + capacity)
                .add("result=" + toResult().result())
                .toString();
    }

    private enum CommitMode {
        SIMPLE_COMMIT, COMMIT_IF_CONSUMED, DROP_COMMIT
    }
}
