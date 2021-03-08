package sh.now.andrew_boyarshin.excelsior.rgb;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class RgbTest {
    private static final ISolver IMPL = new SolverImpl();

    private static GameSolution solve(ISolver impl, String cases) {
        try (var scanner = new Scanner(cases)) {
            assertTrue(scanner.hasNext());
            return RGBGame.solve(impl, scanner);
        }
    }

    private static GameSolution solve(String cases) {
        return solve(IMPL, cases);
    }

    private static void assertSolution(String input, int expectedScore, int expectedRemainingBalls, GameMove... expectedMoves) {
        var actual = solve(input);
        assertNotNull(actual);
        assertEquals(expectedScore, actual.score());
        assertEquals(expectedRemainingBalls, actual.remainingBalls());
        assertIterableEquals(Arrays.stream(expectedMoves).collect(Collectors.toList()), actual.moves());
    }

    @Test
    void sample_1() {
        assertSolution(
                """
                        RGGBBGGRBRRGGBG
                        RBGRBGRBGRBGRBG
                        RRRRGBBBRGGRBBB
                        GGRGBGGBRRGGGBG
                        GBGGRRRRRBGGRRR
                        BBBBBBBBBBBBBBB
                        BBBBBBBBBBBBBBB
                        RRRRRRRRRRRRRRR
                        RRRRRRGGGGRRRRR
                        GGGGGGGGGGGGGGG""",
                3661, 1,
                new GameMove(4, 1, 32, BallColor.BLUE),
                new GameMove(2, 1, 39, BallColor.RED),
                new GameMove(1, 1, 37, BallColor.GREEN),
                new GameMove(3, 4, 11, BallColor.BLUE),
                new GameMove(1, 1, 8, BallColor.RED),
                new GameMove(2, 1, 6, BallColor.GREEN),
                new GameMove(1, 6, 6, BallColor.BLUE),
                new GameMove(1, 2, 5, BallColor.RED),
                new GameMove(1, 2, 5, BallColor.GREEN)
        );
    }

    @Test
    void sample_2() {
        assertSolution(
                """
                        RRRRRRRRRRRRRRR
                        RRRRRRRRRRRRRRR
                        GGGGGGGGGGGGGGG
                        GGGGGGGGGGGGGGG
                        BBBBBBBBBBBBBBB
                        BBBBBBBBBBBBBBB
                        RRRRRRRRRRRRRRR
                        RRRRRRRRRRRRRRR
                        GGGGGGGGGGGGGGG
                        GGGGGGGGGGGGGGG""",
                4920, 0,
                new GameMove(1, 1, 30, BallColor.GREEN),
                new GameMove(1, 1, 30, BallColor.RED),
                new GameMove(1, 1, 30, BallColor.BLUE),
                new GameMove(1, 1, 30, BallColor.GREEN),
                new GameMove(1, 1, 30, BallColor.RED)
        );
    }

    @Test
    void sample_3() {
        assertSolution(
                """
                        RBGRBGRBGRBGRBG
                        BGRBGRBGRBGRBGR
                        GRBGRBGRBGRBGRB
                        RBGRBGRBGRBGRBG
                        BGRBGRBGRBGRBGR
                        GRBGRBGRBGRBGRB
                        RBGRBGRBGRBGRBG
                        BGRBGRBGRBGRBGR
                        GRBGRBGRBGRBGRB
                        RBGRBGRBGRBGRBG""",
                0, 150
        );
    }
}
