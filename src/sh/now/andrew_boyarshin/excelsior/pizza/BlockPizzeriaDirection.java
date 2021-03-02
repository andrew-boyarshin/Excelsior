package sh.now.andrew_boyarshin.excelsior.pizza;

/**
 * Convenient data storage record for a single direction of a single {@link Block}.
 */
public record BlockPizzeriaDirection(Pizzeria pizzeria, int distance, Direction direction) {
}
