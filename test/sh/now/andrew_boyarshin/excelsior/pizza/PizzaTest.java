package sh.now.andrew_boyarshin.excelsior.pizza;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaTest {
    private static final ISolver NAIVE = new SolverImpl();
    private static final ISolver[] SOLVERS = {NAIVE};

    private static SolverResult solve(ISolver impl, String cases) {
        try (var scanner = new Scanner(cases)) {
            var n = scanner.nextInt();
            return Pizza.solveMultiple(impl, scanner, n);
        }
    }

    private static void assertResult(SolverResult result) {
        var map = new boolean[result.m() + 1][result.n() + 1];

        @FunctionalInterface
        interface Marker {
            void accept(int x, int y);
        }

        @FunctionalInterface
        interface FillDirection {
            void accept(SolverPizzeriaResult result, Direction direction, int span);
        }

        Marker mark = (int x, int y) -> {
            assertNotEquals(0, x);
            assertNotEquals(0, y);
            assertFalse(map[y][x]);
            map[y][x] = true;
        };

        FillDirection fillDirection = (SolverPizzeriaResult pizzeriaResult, Direction direction, int span) -> {
            var x = pizzeriaResult.x();
            var y = pizzeriaResult.y();
            for (int i = 1; i <= span; i++) {
                var coordinates = Utilities.addPosition(x, y, direction, i);
                mark.accept(coordinates[0], coordinates[1]);
            }
        };

        for (SolverPizzeriaResult pizzeriaResult : result.pizzerias()) {
            mark.accept(pizzeriaResult.x(), pizzeriaResult.y());
            fillDirection.accept(pizzeriaResult, Direction.NORTH, pizzeriaResult.north());
            fillDirection.accept(pizzeriaResult, Direction.SOUTH, pizzeriaResult.south());
            fillDirection.accept(pizzeriaResult, Direction.WEST, pizzeriaResult.west());
            fillDirection.accept(pizzeriaResult, Direction.EAST, pizzeriaResult.east());
            assertEquals(pizzeriaResult.capacity(), pizzeriaResult.north() + pizzeriaResult.east() + pizzeriaResult.south() + pizzeriaResult.west());
        }

        for (int i = 0, rowsLength = map.length; i < rowsLength; i++) {
            boolean[] row = map[i];
            for (int j = 0, columnsLength = row.length; j < columnsLength; j++) {
                boolean item = row[j];
                if (i != 0 && j != 0)
                    assertTrue(item);
                else
                    assertFalse(item);
            }
        }
    }

    private static void solve(String cases) {
        var solverResults = Arrays.stream(SOLVERS).map(x -> solve(x, cases)).collect(Collectors.toList());
        solverResults.forEach(PizzaTest::assertResult);
    }

    @Test
    void sample_1() {
        solve(
                """
                        2 2 2
                        1 1 1
                        2 2 1"""
        );
    }

    @Test
    void sample_2() {
        solve(
                """
                        5 5 6
                        1 3 2
                        2 1 4
                        2 5 4
                        3 3 5
                        5 2 2
                        5 4 2"""
        );
    }

    @Test
    void sample_3() {
        solve(
                """
                        2 2 2
                        1 1 1
                        1 2 1"""
        );
    }

    @Test
    void sample_4() {
        solve(
                """
                        1 4 2
                        1 2 1
                        1 3 1"""
        );
    }

    @Test
    void sample_5() {
        solve(
                """
                        1 4 2
                        1 2 1
                        1 4 1"""
        );
    }

    @Test
    void sample_6() {
        solve(
                """
                        1 5 2
                        1 2 2
                        1 4 1"""
        );
    }
}
