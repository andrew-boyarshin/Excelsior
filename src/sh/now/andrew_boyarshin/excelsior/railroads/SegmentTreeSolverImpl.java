package sh.now.andrew_boyarshin.excelsior.railroads;

import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SegmentTreeSolverImpl extends SolverBase {
    private boolean[] marked;

    @Override
    public void reset() {
        Arrays.fill(arr, Integer.MAX_VALUE);
        Arrays.fill(marked, false);
    }

    @Override
    public void reallocate() {
        arr = new int[4 * n];
        marked = new boolean[4 * n];

        Arrays.fill(arr, Integer.MAX_VALUE);
    }

    void push(int v) {
        if (!marked[v])
            return;

        if (arr[v * 2] != Integer.MAX_VALUE) {
            arr[v * 2] = arr[v];
            marked[v * 2] = true;
        }

        if (arr[v * 2 + 1] != Integer.MAX_VALUE) {
            arr[v * 2 + 1] = arr[v];
            marked[v * 2 + 1] = true;
        }

        marked[v] = false;
    }

    void update(int v, int tl, int tr, int l, int r, int new_val) {
        if (l > r)
            return;
        if (l == tl && tr == r) {
            if (arr[v] != Integer.MAX_VALUE || (l == new_val && r == new_val)) {
                arr[v] = new_val;
                marked[v] = true;
            }
        } else {
            push(v);
            int tm = (tl + tr) / 2;
            update(v * 2, tl, tm, l, min(r, tm), new_val);
            update(v * 2 + 1, tm + 1, tr, max(l, tm + 1), r, new_val);
            arr[v] = min(arr[v * 2], arr[v * 2 + 1]);
        }
    }

    void update(int l, int new_val) {
        update(1, 1, n, l, n, new_val);
    }

    int query(int v, int tl, int tr, int l, int r) {
        if (l > r)
            return Integer.MAX_VALUE;
        if (l <= tl && tr <= r)
            return arr[v];
        push(v);
        int tm = (tl + tr) / 2;
        return min(query(v * 2, tl, tm, l, min(r, tm)), query(v * 2 + 1, tm + 1, tr, max(l, tm + 1), r));
    }

    int minOnRange(int l) {
        return query(1, 1, n, l, n);
    }

    @Override
    public boolean hasSmallerElementsThan(int start, int value) {
        return minOnRange(start) <= value;
    }

    @Override
    public void assignRange(int start, int value) {
        update(start, value);
    }
}
