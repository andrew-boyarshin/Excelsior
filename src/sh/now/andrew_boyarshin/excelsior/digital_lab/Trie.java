package sh.now.andrew_boyarshin.excelsior.digital_lab;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Trie {
    // The word prefix this node represents.
    public final boolean[] word;
    // The parent node.
    private final Trie parent;
    // A value indicating whether this instance represents a word in the dictionary.
    private boolean isWord;
    // The child nodes.
    private Trie nextTrue, nextFalse;
    // The failure node.
    private Trie fail;

    private Trie(@Nullable Trie parent, boolean @NotNull [] word) {
        this.parent = parent;
        this.word = word;
    }

    public static Trie create() {
        return new Trie(null, PooledRowMatrix.empty);
    }

    // Adds the specified word to the trie.
    @NotNull
    public Trie add(boolean @NotNull [] word) {
        var c = word[0];

        var node = nextWithCreate(c);

        if (word.length > 1)
            return node.add(PooledRowMatrix.instance.consume(word, 1));

        node.isWord = true;

        return node;
    }

    @NotNull
    private Trie nextWithCreate(boolean c) {
        var node = next(c);
        if (node != null)
            return node;

        node = new Trie(this, PooledRowMatrix.instance.append(word, c));

        if (c)
            nextTrue = node;
        else
            nextFalse = node;

        return node;
    }

    @Nullable Trie next(boolean c) {
        return c ? nextTrue : nextFalse;
    }

    public void runOnNext(@NotNull Consumer<Trie> consumer) {
        if (nextTrue != null)
            consumer.accept(nextTrue);
        if (nextFalse != null)
            consumer.accept(nextFalse);
    }

    // Finds the failure node for a specified suffix.
    @Nullable
    public Trie exploreFailureLink(boolean @NotNull [] word) {
        var node = this;

        for (var c : word) {
            node = node.next(c);
            if (node == null)
                return null;
        }

        return node;
    }

    public Trie fail() {
        return fail;
    }

    public void setFail(Trie fail) {
        this.fail = fail;
    }

    public boolean isWord() {
        return isWord;
    }

    public Trie parent() {
        return parent;
    }
}
