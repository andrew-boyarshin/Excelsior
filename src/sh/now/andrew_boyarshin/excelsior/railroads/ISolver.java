package sh.now.andrew_boyarshin.excelsior.railroads;

public interface ISolver {
    boolean add(int e);
    void reset();
    void resetAndResize(int n);
}
