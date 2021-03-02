package sh.now.andrew_boyarshin.excelsior.pizza;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolverImpl implements ISolver {
    List<Pizzeria> pizzerias = new ArrayList<>();
    List<TreeMap<Integer, Pizzeria>> pizzeriasByRows, pizzeriasByColumns;
    List<List<Block>> blocks;

    @Nullable
    private static List<Block> createBlockRow(int m, int i) {
        if (i == 0)
            return null;

        return IntStream.range(0, m + 1)
                .mapToObj(j -> j != 0 ? new Block(j, i) : null)
                .collect(Collectors.toList());
    }

    @Override
    public void reset(int n, int m, int k) {
        assert n >= 1 && m >= 1;

        pizzerias.clear();
        pizzeriasByRows = IntStream.range(0, m + 1)
                .mapToObj(i -> new TreeMap<Integer, Pizzeria>())
                .collect(Collectors.toList());
        pizzeriasByColumns = IntStream.range(0, n + 1)
                .mapToObj(i -> new TreeMap<Integer, Pizzeria>())
                .collect(Collectors.toList());
        blocks = IntStream.range(0, m + 1)
                .mapToObj(i -> createBlockRow(n, i))
                .collect(Collectors.toList());
    }

    @Override
    public void addPizzeria(int x, int y, int capacity) {
        assert 0 < x && x <= maxX();
        assert 0 < y && y <= maxY();

        final var pizzeria = new Pizzeria(pizzerias.size(), x, y, capacity);
        pizzerias.add(pizzeria);
        pizzeriasByRows.get(y).put(x, pizzeria);
        pizzeriasByColumns.get(x).put(y, pizzeria);
    }

    @Override
    public List<Pizzeria> getPizzerias() {
        return pizzerias;
    }

    @Contract(pure = true)
    private int maxX() {
        return this.blocks.get(1).size() - 1;
    }

    @Contract(pure = true)
    private int maxY() {
        return this.blocks.size() - 1;
    }

    @NotNull
    @Contract(pure = true)
    private Block getBlockAt(int x, int y) {
        assert 0 < x && x <= maxX();
        assert 0 < y && y <= maxY();

        return this.blocks.get(y).get(x);
    }

    @Nullable
    @Contract(pure = true)
    private Block getOptionalBlockAt(int x, int y) {
        if (0 < y && y <= maxY()) {
            final var row = this.blocks.get(y);
            if (0 < x && x <= maxX())
                return row.get(x);
        }

        return null;
    }

    @Nullable
    @Contract(pure = true)
    private Block getBlockInDirection(Block block, int[] difference, int distance) {
        var position = Utilities.addPosition(block.x, block.y, difference, distance);

        assert position.length == 2;

        return getOptionalBlockAt(position[0], position[1]);
    }

    @Override
    public void solve() {
        assert !pizzeriasByColumns.isEmpty();
        assert !pizzeriasByRows.isEmpty();

        final var mandatoryBlocks = assignMandatory();

        pizzeriasByRows.clear();
        pizzeriasByColumns.clear();
        pizzeriasByRows = null;
        pizzeriasByColumns = null;

        for (Block block : mandatoryBlocks)
            propagateMandatory(block);

        // Greatly reduce the state space by handling cases not requiring brute-force.
        iterateOnDefiniteCases();

        bruteForce();
    }

    private void bruteForce() {
        var freePizzerias = getFreePizzerias();
        var freeBlocks = getFreeBlocks();

        assert freePizzerias.isEmpty() == freeBlocks.isEmpty();

        if (freeBlocks.isEmpty())
            return;

        var sets = new ArrayList<List<Direction>>();

        for (Block freeBlock : freeBlocks) {
            assert !freeBlock.hasMandatoryPizzeriaDirection();

            var connections = freeBlock.getConnectedPizzerias();
            assert connections.size() > 1;

            final var set = connections.stream()
                    .map(BlockPizzeriaDirection::direction)
                    .collect(Collectors.toUnmodifiableList());

            sets.add(set);
        }

        assert sets.stream().allMatch(x -> x.size() > 1);

        var cartesianSet = new CartesianProduct(sets);

        for (int i = 0; i < cartesianSet.size(); i++) {
            for (Pizzeria freePizzeria : freePizzerias)
                freePizzeria.beginBruteForceTransaction();
            for (Block freeBlock : freeBlocks)
                freeBlock.setBruteForcePizzeriaDirection(null);

            for (int j = 0; j < freeBlocks.size(); j++) {
                var freeBlock = freeBlocks.get(j);
                var direction = cartesianSet.getAt(i, j);

                assert !freeBlock.hasMandatoryPizzeriaDirection();

                if (!assignBruteForcePizzeria(freeBlock, direction, true))
                    break;

                propagateBruteForcePizzeria(freeBlock);
            }

            if (freeBlocks.stream().allMatch(Block::hasFinalPizzeriaDirection)) {
                // N.B.: collection is mandatory to enforce committing all transactions.
                var results = freePizzerias.stream()
                        .map(Pizzeria::commitBruteForceTransaction)
                        .collect(Collectors.toUnmodifiableSet());

                if (results.stream().allMatch(Boolean::booleanValue))
                    return;
            } else {
                if (freePizzerias.stream().anyMatch(Pizzeria::dropBruteForceTransaction))
                    throw new AssertionError();
            }
        }
    }

    private boolean assignBruteForcePizzeria(Block block, Direction direction, boolean root) {
        assert !block.isPizzeriaItself();

        final var connection = block.getDirection(direction);
        assert connection != null;
        assert connection.direction() == direction;

        if (!root) {
            if (block.getFinalPizzeriaDirection() != direction)
                return false;
        }

        block.setBruteForcePizzeriaDirection(direction);

        return connection.pizzeria().tryBruteForceFeed(block);
    }

    private void propagateBruteForcePizzeria(Block bruteForceBlock) {
        assert !bruteForceBlock.hasMandatoryPizzeriaDirection();

        final var finalPizzeria = bruteForceBlock.getFinalPizzeria();

        assert finalPizzeria != null;

        final var pizzeria = finalPizzeria.pizzeria();
        final var distance = finalPizzeria.distance();
        final var direction = finalPizzeria.direction();
        final var difference = Utilities.directionToPositionDifference(direction);

        for (int i = 1; i < distance; i++) {
            var block = getIntermediateBlockForPropagation(bruteForceBlock, pizzeria, direction, difference, i);

            assignBruteForcePizzeria(block, direction, false);
        }

        verifyPizzeriaOnPropagation(bruteForceBlock, pizzeria, difference, distance);
    }

    private void iterateOnDefiniteCases() {
        var freePizzerias = getFreePizzerias();

        if (freePizzerias.isEmpty())
            return;

        var freeBlocks = getFreeBlocks();

        if (freeBlocks.isEmpty())
            return;

        var changed = false;

        do {
            // N.B.: Optimization for first iteration
            if (changed) {
                // filter out newly assigned blocks and pizzerias

                freePizzerias = freePizzerias.stream()
                        .filter(x -> x.remainingCapacity > 0)
                        .collect(Collectors.toUnmodifiableList());

                freeBlocks = freeBlocks.stream()
                        .filter(x -> !x.hasMandatoryPizzeriaDirection())
                        .collect(Collectors.toUnmodifiableList());
            }

            changed = false;

            for (Pizzeria pizzeria : freePizzerias)
                pizzeria.resetFakeFeedForDefiniteIteration();

            for (Block freeBlock : freeBlocks) {
                var removedConnections = false;

                for (BlockPizzeriaDirection pizzeria : freeBlock.getConnectedPizzerias()) {
                    if (pizzeria.pizzeria().remainingCapacity < 1) {
                        freeBlock.setPizzeriaDirection(pizzeria.direction(), null);
                        removedConnections = true;
                        continue;
                    }

                    pizzeria.pizzeria().fakeFeedForDefiniteIteration(freeBlock);
                }

                if (removedConnections) {
                    changed = true;

                    if (blockInferMandatory(freeBlock))
                        propagateMandatory(freeBlock);
                }
            }

            if (changed)
                continue;

            // N.B.: lazy-evaluated
            List<Block> finalFreeBlocks = null;

            // Looks horrible when Stream-ified.
            for (var pizzeria : freePizzerias) {
                if (!pizzeria.successFakeFeedForDefiniteIteration())
                    continue;

                if (finalFreeBlocks == null)
                    finalFreeBlocks = freeBlocks.stream()
                            .filter(x -> !x.hasMandatoryPizzeriaDirection())
                            .collect(Collectors.toUnmodifiableList());

                for (var freeBlock : finalFreeBlocks) {
                    var entry = freeBlock.getConnectedPizzerias()
                            .stream()
                            .filter(p -> p.pizzeria() == pizzeria)
                            .findFirst();

                    if (entry.isEmpty())
                        continue;

                    assignMandatoryPizzeria(freeBlock, entry.get());
                    propagateMandatory(freeBlock);
                    changed = true;
                }
            }
        }
        while (changed);
    }

    @NotNull
    @Contract(pure = true)
    private List<Block> getFreeBlocks() {
        return this.blocks.stream()
                .skip(1)
                .flatMap(row -> row.stream().skip(1))
                .filter(x -> !x.isPizzeriaItself() && !x.hasMandatoryPizzeriaDirection())
                .collect(Collectors.toUnmodifiableList());
    }

    @NotNull
    @Contract(pure = true)
    private List<Pizzeria> getFreePizzerias() {
        return this.pizzerias.stream()
                .filter(x -> x.remainingCapacity > 0)
                .collect(Collectors.toUnmodifiableList());
    }

    @NotNull
    private List<Block> assignMandatory() {
        var blocksWithMandatory = new ArrayList<Block>();

        for (int y = 1; y <= maxY(); y++) {
            final var pizzeriasInRow = pizzeriasByRows.get(y);

            for (int x = 1; x <= maxX(); x++) {
                var block = getBlockAt(x, y);
                assert x == block.x && y == block.y;

                final var pizzeriasInColumn = pizzeriasByColumns.get(x);

                var pizzeria1 = SolverUtilities.blockAssignment(block, pizzeriasInColumn, y, p -> p.y, Direction.SOUTH, Direction.NORTH);
                var pizzeria2 = SolverUtilities.blockAssignment(block, pizzeriasInRow, x, p -> p.x, Direction.WEST, Direction.EAST);
                assert pizzeria1 == pizzeria2;

                if (pizzeria1) {
                    if (blockInferMandatory(block)) {
                        // N.B.: schedule mandatory block propagation until after we've marked all pizzerias,
                        //       that is, when this nested loop is finished.

                        blocksWithMandatory.add(block);
                    }
                } else {
                    assert block.isPizzeriaItself();
                }
            }
        }

        return blocksWithMandatory;
    }

    private void assignMandatoryPizzeria(Block block, Direction direction, Pizzeria pizzeria) {
        block.setMandatoryPizzeriaDirection(direction);
        if (!pizzeria.tryFeed(block))
            throw new AssertionError();
    }

    private void assignMandatoryPizzeria(Block block, BlockPizzeriaDirection direction) {
        assignMandatoryPizzeria(block, direction.direction(), direction.pizzeria());
    }

    private void propagateMandatory(Block mandatoryBlock) {
        final var mandatoryPizzeria = mandatoryBlock.getMandatoryPizzeria();

        assert mandatoryPizzeria != null;

        final var pizzeria = mandatoryPizzeria.pizzeria();
        final var distance = mandatoryPizzeria.distance();
        final var direction = mandatoryPizzeria.direction();
        final var difference = Utilities.directionToPositionDifference(direction);

        for (int i = 1; i < distance; i++) {
            var block = getIntermediateBlockForPropagation(mandatoryBlock, pizzeria, direction, difference, i);

            assignMandatoryPizzeria(block, direction, pizzeria);
        }

        verifyPizzeriaOnPropagation(mandatoryBlock, pizzeria, difference, distance);
    }

    @NotNull
    private Block getIntermediateBlockForPropagation(Block mandatoryBlock, Pizzeria pizzeria, Direction direction, int[] difference, int distance) {
        var block = getBlockInDirection(mandatoryBlock, difference, distance);

        assert block != null;
        assert !block.isPizzeriaItself();
        assert block.getDirection(direction).pizzeria() == pizzeria;

        return block;
    }

    private void verifyPizzeriaOnPropagation(Block pizzeriaBlock, Pizzeria pizzeria, int[] difference, int distance) {
        var block = getBlockInDirection(pizzeriaBlock, difference, distance);

        assert block != null;
        assert block.isPizzeriaItself();
        assert block.getPizzeriaSelf() == pizzeria;
    }

    private boolean blockInferMandatory(Block block) {
        var connected = block.getConnectedPizzerias();

        if (connected.size() != 1)
            return false;

        var connection = connected.get(0);

        assignMandatoryPizzeria(block, connection);

        return true;
    }
}
