package sh.now.andrew_boyarshin.excelsior.railroads;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Railroads {
    public static void main(String[] args) {
        ISolver impl = new SegmentTreeSolverImpl();
        try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                var n = scanner.nextInt();
                if (n == 0)
                    return;

                for (Boolean result : solveMultiple(impl, scanner, n)) {
                    System.out.println(result ? "Yes" : "No");
                }

                System.out.println();
            }
        }
    }

    private static boolean solve(ISolver impl, Scanner scanner, int n, int element) {
        impl.reset();

        var success = true;

        for (var i = 0; i < n; i++) {
            if (i != 0) {
                element = scanner.nextInt();
            }

            assert 1 <= element && element <= n;

            if (!success)
                continue;

            if (!impl.add(element))
                success = false;
        }

        return success;
    }

    static List<Boolean> solveMultiple(ISolver impl, Scanner scanner, int n) {
        var result = new ArrayList<Boolean>();

        impl.resetAndResize(n);

        while (scanner.hasNext()) {
            var element = scanner.nextInt();

            assert 0 <= element && element <= n;

            if (element == 0)
                break;

            boolean success = solve(impl, scanner, n, element);

            result.add(success);
        }

        return result;
    }
}
