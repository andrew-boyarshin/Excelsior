package sh.now.andrew_boyarshin.excelsior.pizza;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public final class SolverUtilities {
    @Contract("_, _, _ -> new")
    private static Pizzeria @NotNull [] bounds(TreeMap<Integer, Pizzeria> pizzerias, int key, PizzeriaPositionFunction function) {
        if (pizzerias.isEmpty())
            return new Pizzeria[]{null, null};

        var lower = pizzerias.lowerEntry(key);
        var upper = pizzerias.ceilingEntry(key);

        @FunctionalInterface
        interface AssertHelper {
            boolean test(Map.Entry<Integer, Pizzeria> entry);
        }

        @FunctionalInterface
        interface AssertPredicate {
            boolean test(Map.Entry<Integer, Pizzeria> entry, Map.Entry<Integer, Pizzeria> boundary, AssertHelper entryHelper, AssertHelper boundaryHelper);
        }

        AssertHelper lowerAssertHelper = entry -> function.apply(entry.getValue()) < key;
        AssertHelper upperAssertHelper = entry -> function.apply(entry.getValue()) >= key;

        AssertPredicate assertPredicate = (entry, boundary, entryHelper, boundaryHelper) ->
                (entry == null && boundaryHelper.test(boundary)) || (entry != null && entryHelper.test(boundary));

        assert assertPredicate.test(lower, pizzerias.firstEntry(), lowerAssertHelper, upperAssertHelper);
        assert assertPredicate.test(upper, pizzerias.lastEntry(), upperAssertHelper, lowerAssertHelper);

        if (upper != null && upper.getKey() == key)
            return new Pizzeria[]{upper.getValue()};

        // 1[function(pizzerias[i-1])] < 2[key] < 4[function(pizzerias[i])]
        // 7[function(pizzerias[^1])] < 10[key]

        return new Pizzeria[]{
                lower != null ? lower.getValue() : null,
                upper != null ? upper.getValue() : null
        };
    }

    private static int distance(Block block, Pizzeria pizzeria) {
        assert !block.isPizzeriaItself();
        assert block.x == pizzeria.x || block.y == pizzeria.y;

        if (block.x != pizzeria.x)
            return Math.abs(pizzeria.x - block.x);
        if (block.y != pizzeria.y)
            return Math.abs(pizzeria.y - block.y);

        // should've asserted already
        throw new IllegalStateException();
    }

    private static BlockPizzeriaDirection pizzeriaDirection(Block block, Pizzeria pizzeria, Direction direction) {
        assert !block.isPizzeriaItself();

        if (pizzeria != null) {
            var distance = distance(block, pizzeria);
            if (distance <= pizzeria.capacity)
                return new BlockPizzeriaDirection(pizzeria, distance, direction);
        }

        return null;
    }

    static boolean blockAssignment(Block block, TreeMap<Integer, Pizzeria> pizzerias, int key, PizzeriaPositionFunction positionFunction, Direction lowerDirection, Direction upperDirection) {
        var bounds = bounds(pizzerias, key, positionFunction);

        if (bounds.length == 1) {
            block.setPizzeriaSelf(bounds[0]);
            return false;
        }

        assert bounds.length == 2;
        var lower = bounds[0];
        var upper = bounds[1];
        assert (pizzerias.size() == 0) || (lower != null || upper != null);

        block.setPizzeriaDirection(lowerDirection, pizzeriaDirection(block, lower, lowerDirection));
        block.setPizzeriaDirection(upperDirection, pizzeriaDirection(block, upper, upperDirection));

        return true;
    }

    @FunctionalInterface
    public interface PizzeriaPositionFunction {
        int apply(Pizzeria value);
    }
}
