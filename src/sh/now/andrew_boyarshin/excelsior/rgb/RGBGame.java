package sh.now.andrew_boyarshin.excelsior.rgb;

import java.util.Scanner;

public class RGBGame {
    public static void main(String[] args) {
        ISolver impl = new SolverImpl();
        try (var scanner = new Scanner(System.in)) {
            var n = scanner.nextInt();

            for (int i = 0; i < n; i++) {
                System.out.println("Game " + (i + 1) + ":");

                final var solution = solve(impl, scanner);

                for (int j = 0; j < solution.moves().size(); j++) {
                    final var move = solution.moves().get(j);

                    System.out.println("Move " + (j + 1) + " at " + move.point() + ": removed " + move.count() + " balls of color " + move.color() + ", got " + Utilities.ballCountToScore(move.count()) + " points.");
                }

                System.out.println("Final score: " + solution.score() + ", with " + solution.remainingBalls() + " balls remaining.");
                System.out.println();
            }
        }
    }

    static GameSolution solve(ISolver impl, Scanner scanner) {
        var field = new BallColor[10][];

        for (int i = 0; i < 10; i++) {
            field[i] = scanner.next().chars().mapToObj(BallColor::parse).toArray(BallColor[]::new);
        }

        return impl.solve(field);
    }
}
