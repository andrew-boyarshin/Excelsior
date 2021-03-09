package sh.now.andrew_boyarshin.excelsior.digital_lab;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AhoCorasickSolverImpl implements ISolver {
    private Trie trie;

    @Override
    public @NotNull Collection<Match> solve(@NotNull FlatMatrix haystack, @NotNull PooledRowMatrix needlePool) {
        final var needleRows = needlePool.rows;
        final var needleColumns = needlePool.columns;
        final var haystackColumns = haystack.columns();

        // fast path
        if (needleRows > haystack.rows() || needleColumns > haystackColumns)
            return Collections.emptyList();

        trie = Trie.create();

        final var needle = needlePool.needle();

        for (var row : needle) {
            trie.add(row);
        }

        buildFailureNodes(null);

        var matches = IntStream.range(0, needleRows)
                .mapToObj(x -> new ArrayList<Integer>())
                .collect(Collectors.toUnmodifiableList());

        ahoCorasick(haystack.data(), (index, word) -> {
            assert word.length == needleColumns;
            final var row = needlePool.indexOf(word);
            assert 0 <= row && row < needleRows;
            matches.get(row).add(index);
        });

        assert matches.stream().allMatch(x -> Utilities.isInStrictOrder(x, Comparator.naturalOrder()));

        final var resultMatches = new ArrayList<Integer>();
        final var fixedIndices = needlePool.needleIndices();

        assert !matches.get(0).isEmpty() || matches.stream().skip(1).allMatch(ArrayList::isEmpty);

        for (var match : matches.get(0)) {
            int position = match;
            var success = true;

            for (int i = 1; i < needleRows; i++) {
                position += haystackColumns;
                if (Collections.binarySearch(matches.get(fixedIndices[i]), position) < 0) {
                    success = false;
                    break;
                }
            }

            if (!success)
                continue;

            resultMatches.add(match);
        }

        return resultMatches.stream()
                .sorted()
                .map(x -> new Match(x, haystack, needlePool))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Builds the failure nodes necessary to perform search.
     *
     * @param node The start node.
     */
    public void buildFailureNodes(Trie node) {
        final var instance = PooledRowMatrix.instance;

        node = node != null ? node : trie;

        var word = node.word;
        for (int i = 1; i < word.length && node.fail() == null; i++)
            node.setFail(trie.exploreFailureLink(instance.consume(word, i)));

        node.runOnNext(this::buildFailureNodes);
    }

    /// Searches for words in the specified span.
    public void ahoCorasick(boolean @NotNull [] span, SearchCallback callback) {
        var current = trie;

        for (int i = 0; i < span.length; i++) {
            var c = span[i];

            while (current != null && current.next(c) == null)
                current = current.fail();

            if (current == null) current = trie;

            if ((current = current.next(c)) != null) {
                var node = current;

                while (node != null) {
                    if (node.isWord()) {
                        var word = node.word;
                        var offset = i + 1 - word.length;
                        callback.accept(offset, word);
                    }

                    node = node.fail();
                }
            }
        }
    }

    @FunctionalInterface
    public interface SearchCallback {
        void accept(int index, boolean[] word);
    }
}
