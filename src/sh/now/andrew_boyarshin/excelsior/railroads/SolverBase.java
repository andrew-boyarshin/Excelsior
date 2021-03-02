package sh.now.andrew_boyarshin.excelsior.railroads;

public abstract class SolverBase implements ISolver {
    protected int n;
    protected int[] arr;

    @Override
    public final boolean add(int e) {
        if (hasSmallerElementsThan(e + 1, e))
            return false;

        assignRange(e, e);
        return true;
    }

    @Override
    public final void resetAndResize(int n) {
        this.n = n;
        reallocate();
    }

    public abstract void reallocate();

    public abstract boolean hasSmallerElementsThan(int start, int value);

    public abstract void assignRange(int start, int value);
}
