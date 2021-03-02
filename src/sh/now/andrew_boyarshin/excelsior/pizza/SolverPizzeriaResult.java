package sh.now.andrew_boyarshin.excelsior.pizza;

import java.util.StringJoiner;

public record SolverPizzeriaResult(int x, int y, int capacity, int north, int east, int south, int west) {
    public String result() {
        return new StringJoiner(" ")
                .add(Integer.toString(north))
                .add(Integer.toString(east))
                .add(Integer.toString(south))
                .add(Integer.toString(west))
                .toString();
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")[" + capacity + "]: " + result();
    }
}
