package sh.now.andrew_boyarshin.excelsior.pizza;

import java.util.List;

public interface ISolver {
    void reset(int n, int m, int k);
    void addPizzeria(int x, int y, int capacity);
    List<Pizzeria> getPizzerias();
    void solve();
}
