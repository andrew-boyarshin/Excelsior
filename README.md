# Directory structure

IntelliJ IDEA project files.

`misc/` folder contains prepared input files from task PDFs. Used only to verify console frontend for each task.

`test/` folder is a set of JUnit 5 tests for each task.

`src/` folder are actual solution sources. They have no interdependencies or dependencies on anything but JetBrains annotations (NotNull, Contract, etc).

`lib/` folder (only on Git, not in ZIP) contains JUnit binaries from Maven Central.

# Generic comments on tasks

I used Java 15 (with _records_ as preview feature).

Each solution has 1 or 2 implementations of `ISolver` interface, specific for each task.
Actual algorithm is there.
The implementations of these interfaces are used both in console frontend and in unit tests. 

## Digital lab

A variation of Aho-Corasick on a flattened `boolean[]` of haystack rows.
Trie data structure with well-defined children.
Patterns are highlighted on output (to console), not as part of a "problem to be solved".

## Pizza

Bruteforce with a lot of optimizations before the actual bruteforce iteration to reduce state space.
On given samples the number of cells requiring bruteforce is 2, each having only 2 variants.

The only task where unit tests don't have correct answers and check the solution validity instead.

## Railroads
Pseudocode:
```python
E = Element[i]:
foreach n > E:
    if min[n] is None:
        continue

    assert(E < min[n])
    min[n] = E
```

Naive implementation follows this pretty closely.
Segment tree impl is obviously faster.

Segment tree impl is actually pretty standard (<https://e-maxx.ru>), with delayed range operations.
`push` is the most important bit, where we need to preserve MAX_VALUE when pushing values down to leaves, since we fail assert only when min has already been assigned to the range.

## RGB game

One more semi-bruteforce (semi-BFS) solution.
I initially planned smart `Cluster` retainment, but this was abandoned due to lack of time.
Too many cases to handle, clusters can become disjointed after removal and collapse.
BFS reclustering on each move.

## Treasure hunt

I didn't publish my work on this task, since I'm having trouble getting it to work.
I did implement Bentley-Ottmann algorithm for finding all line intersections and then all middlepoints, but then I'm having trouble associating them with respective polygons (to use only valid paths).
Algebra always appealed more to me than geometry. Or maybe it's a task on a knowledge of a specific algorithm I haven't heard about.

It's also the 1st task I've worked on, so I'm a bit disappointed this time could've been used to improve other tasks instead.

# Miscellaneous comments

Besides traditional means (like debugger), I've used profiler to diagnose issues in implementation.
Most non-algorithmic optimizations are driven by its results.

I love asserts. They help A LOT during development.
I try to put as many asserts as I can. They also help self-document the code.
They do incur significant performance overhead though, so if one wants to benchmark the code, no *javac* `-ea` switch should be specified.
Plus, IntelliJ IDEA adds its own instrumentation for nullability annotations, which is controlled separately.
Some debug code parts are still executed even with assertions disabled.
If Java had preprocessor (like C/C++/C# do), debug code overhead could be lowered to 0 in Release builds.

I didn't code in Java for ~5 years, so I might be a bit rusty.
I also don't remember the majority of Java code style guidelines, except for naming. 

One of the things I miss the most from [my favorite language](https://github.com/andrew-boyarshin?tab=overview#js-contribution-activity) C# are proper value types.
2nd place takes LINQ, Java Streams feel too bare-bones for my taste.
One place in Pizza code could've used `ref` parameters.

Found a bug in PVS-Studio Control-Flow analysis. See `Pizzeria` class.

When writing public APIs I prefer exposing immutable views when possible (to reduce chance of making a mistake by improperly mutating internal data structures), but this time I didn't try to follow this self-imposed code-style rule since performance matters in tasks like these. 

Any additional comments can be requested in [GitHub issues](https://github.com/andrew-boyarshin/Excelsior) if repo is still public.
Or at _andrew.boyarshin_ at _gmail.com_. 
