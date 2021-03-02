package sh.now.andrew_boyarshin.excelsior.pizza;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Block {
    public final int x, y;

    /**
     * {@link Pizzeria} in this block if present, {@code null} otherwise.
     */
    private Pizzeria pizzeriaSelf;

    /**
     * {@link Direction} of selected {@link Pizzeria} for this block or {@code null}.
     * Mandatory assignments are the ones done before brute-forcing.
     * They are inferred from trivial but frequent cases.
     *
     * @implNote {@code null} if {@link Block#bruteForcePizzeriaDirection} is non-{@code null}
     */
    private Direction mandatoryPizzeriaDirection;

    /**
     * {@link Direction} of selected {@link Pizzeria} for this block or {@code null}.
     * Mandatory assignments are the ones done before brute-forcing.
     * They are inferred from trivial but frequent cases.
     *
     * @implNote {@code null} if {@link Block#mandatoryPizzeriaDirection} is non-{@code null}
     */
    private Direction bruteForcePizzeriaDirection;

    private BlockPizzeriaDirection northPizzeria, eastPizzeria, southPizzeria, westPizzeria;

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // I miss C# ref parameters. Well, at least Java now has switch expressions.

    private void setNorthPizzeria(BlockPizzeriaDirection northPizzeria) {
        assert (this.northPizzeria == null) || (northPizzeria == null);
        this.northPizzeria = northPizzeria;
    }

    private void setEastPizzeria(BlockPizzeriaDirection eastPizzeria) {
        assert (this.eastPizzeria == null) || (eastPizzeria == null);
        this.eastPizzeria = eastPizzeria;
    }

    private void setSouthPizzeria(BlockPizzeriaDirection southPizzeria) {
        assert (this.southPizzeria == null) || (southPizzeria == null);
        this.southPizzeria = southPizzeria;
    }

    private void setWestPizzeria(BlockPizzeriaDirection westPizzeria) {
        assert (this.westPizzeria == null) || (westPizzeria == null);
        this.westPizzeria = westPizzeria;
    }

    public void setPizzeriaDirection(Direction direction, BlockPizzeriaDirection value) {
        assert value == null || direction == value.direction();
        switch (direction) {
            case NORTH -> setNorthPizzeria(value);
            case EAST -> setEastPizzeria(value);
            case SOUTH -> setSouthPizzeria(value);
            case WEST -> setWestPizzeria(value);
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    public BlockPizzeriaDirection getDirection(@NotNull Direction direction) {
        return switch (direction) {
            case NORTH -> northPizzeria;
            case EAST -> eastPizzeria;
            case SOUTH -> southPizzeria;
            case WEST -> westPizzeria;
        };
    }

    @Contract("null -> null")
    private BlockPizzeriaDirection getPizzeriaDirection(Direction direction) {
        if (direction == null)
            return null;

        final var pizzeriaDirection = getDirection(direction);

        assert pizzeriaDirection != null;
        assert pizzeriaDirection.direction() == direction;

        return pizzeriaDirection;
    }

    public BlockPizzeriaDirection getMandatoryPizzeria() {
        var direction = getMandatoryPizzeriaDirection();

        return getPizzeriaDirection(direction);
    }

    public BlockPizzeriaDirection getFinalPizzeria() {
        var direction = getFinalPizzeriaDirection();

        return getPizzeriaDirection(direction);
    }

    @Contract(pure = true)
    private boolean assertPizzeriaCondition() {
        if (pizzeriaSelf == null)
            return true;

        return mandatoryPizzeriaDirection == null && bruteForcePizzeriaDirection == null;
    }

    public Direction getFinalPizzeriaDirection() {
        var mandatory = getMandatoryPizzeriaDirection();

        if (mandatory != null) {
            assert bruteForcePizzeriaDirection == null;
            return mandatory;
        }

        return bruteForcePizzeriaDirection;
    }

    public Direction getMandatoryPizzeriaDirection() {
        assert assertPizzeriaCondition();

        return this.mandatoryPizzeriaDirection;
    }

    public void setMandatoryPizzeriaDirection(@NotNull Direction mandatoryPizzeriaDirection) {
        assert !isPizzeriaItself();
        assert getMandatoryPizzeriaDirection() == null || getMandatoryPizzeria().direction() == mandatoryPizzeriaDirection;
        assert getDirection(mandatoryPizzeriaDirection) != null;
        assert bruteForcePizzeriaDirection == null;

        this.mandatoryPizzeriaDirection = mandatoryPizzeriaDirection;
    }

    public void setBruteForcePizzeriaDirection(@Nullable Direction bruteForcePizzeriaDirection) {
        assert !isPizzeriaItself();
        assert bruteForcePizzeriaDirection == null || getFinalPizzeriaDirection() == null || getFinalPizzeria().direction() == bruteForcePizzeriaDirection;
        assert bruteForcePizzeriaDirection == null || getDirection(bruteForcePizzeriaDirection) != null;
        assert !hasMandatoryPizzeriaDirection();

        this.bruteForcePizzeriaDirection = bruteForcePizzeriaDirection;
    }

    public boolean hasMandatoryPizzeriaDirection() {
        return getMandatoryPizzeriaDirection() != null;
    }

    public boolean hasFinalPizzeriaDirection() {
        return getFinalPizzeriaDirection() != null;
    }

    public Pizzeria getPizzeriaSelf() {
        assert assertPizzeriaCondition();

        return this.pizzeriaSelf;
    }

    public void setPizzeriaSelf(@NotNull Pizzeria pizzeriaSelf) {
        assert !hasMandatoryPizzeriaDirection();
        assert getPizzeriaSelf() == null || getPizzeriaSelf() == pizzeriaSelf;

        this.pizzeriaSelf = pizzeriaSelf;
    }

    public boolean isPizzeriaItself() {
        return getPizzeriaSelf() != null;
    }

    public List<BlockPizzeriaDirection> getConnectedPizzerias() {
        return Stream.of(northPizzeria, eastPizzeria, southPizzeria, westPizzeria)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        if (pizzeriaSelf != null)
            return Block.class.getSimpleName() + "[" + pizzeriaSelf + "]";

        return new StringJoiner(", ", Block.class.getSimpleName() + "[", "]")
                .add("hasMandatoryPizzeria=" + hasMandatoryPizzeriaDirection())
                .add("pizzeria=" + getFinalPizzeria())
                .add("connected=[" + getConnectedPizzerias() + "]")
                .toString();
    }

}
