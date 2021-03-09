package sh.now.andrew_boyarshin.excelsior.digital_lab;

public record Match(int startRowInclusive, int endRowExclusive, int startColumnInclusive, int endColumnExclusive) {
    public Match(int startRowInclusive, int startColumnInclusive, PooledRowMatrix needlePool) {
        this(
                startRowInclusive,
                startRowInclusive + needlePool.rows,
                startColumnInclusive,
                startColumnInclusive + needlePool.columns
        );
    }

    public Match(int position, FlatMatrix haystack, PooledRowMatrix needlePool) {
        this(
                position / haystack.columns(),
                position % haystack.columns(),
                needlePool
        );
    }

    public boolean inside(int row, int column) {
        return startRowInclusive <= row && row < endRowExclusive
                && startColumnInclusive <= column && column < endColumnExclusive;
    }
}
