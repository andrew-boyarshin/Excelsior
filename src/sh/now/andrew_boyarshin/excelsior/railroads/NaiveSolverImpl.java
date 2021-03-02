package sh.now.andrew_boyarshin.excelsior.railroads;

import java.util.Arrays;

public class NaiveSolverImpl extends SolverBase {
    @Override
    public void reallocate() {
        arr = new int[n];
    }

    @Override
    public void reset() {
        Arrays.fill(arr, 0);
    }

    @Override
    public boolean hasSmallerElementsThan(int start, int value) {
        return Arrays.stream(arr).skip(start - 1).filter(x -> x != 0).anyMatch(x -> x <= value);
    }

    public void assignRange(int start, int value) {
        arr[start - 1] = value;

        for (int i = start; i < arr.length; i++) {
            if (arr[i] == 0)
                continue;

            arr[i] = value;
        }
    }
}
