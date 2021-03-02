package sh.now.andrew_boyarshin.excelsior.pizza;

import java.util.Scanner;
import java.util.stream.Collectors;

public class Pizza {
    public static void main(String[] args) {
        ISolver impl = new SolverImpl();
        try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                var n = scanner.nextInt();
                if (n == 0)
                    return;

                for (SolverPizzeriaResult result : solveMultiple(impl, scanner, n).pizzerias()) {
                    System.out.println(result.result());
                }

                System.out.println();
            }
        }
    }

    static SolverResult solveMultiple(ISolver impl, Scanner scanner, int n) {
        var m = scanner.nextInt();
        var k = scanner.nextInt();

        impl.reset(n, m, k);

        var totalCapacity = 0;

        for (int i = 0; i < k; i++) {
            var x = scanner.nextInt();
            var y = scanner.nextInt();
            var capacity = scanner.nextInt();
            assert 1 <= x && x <= n;
            assert 1 <= y && y <= m;
            totalCapacity += capacity;
            impl.addPizzeria(x, y, capacity);
        }

        assert n * m - k == totalCapacity;

        impl.solve();

        return new SolverResult(n, m, impl.getPizzerias().stream().map(Pizzeria::toResult).collect(Collectors.toList()));
    }
}
