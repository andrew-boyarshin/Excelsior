package sh.now.andrew_boyarshin.excelsior.rgb;

import java.util.List;

public record GameSolution(int score, long remainingBalls, List<GameMove> moves) {
}
