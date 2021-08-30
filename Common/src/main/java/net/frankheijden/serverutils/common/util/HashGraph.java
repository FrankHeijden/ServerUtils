package net.frankheijden.serverutils.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashGraph<T> {

    private final Set<T> nodes;
    private final Map<T, Set<T>> successors;
    private final Map<T, Set<T>> predecessors;

    public HashGraph() {
        this(16);
    }

    /**
     * Constructs a new hash-based graph.
     */
    public HashGraph(int initialCapacity) {
        this.nodes = new HashSet<>(initialCapacity);
        this.successors = new HashMap<>(initialCapacity);
        this.predecessors = new HashMap<>(initialCapacity);
    }

    public void addNode(T node) {
        this.nodes.add(node);
    }

    public void putEdge(T from, T to) {
        this.successors.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        this.predecessors.computeIfAbsent(to, k -> new HashSet<>()).add(from);
    }

    public Set<T> nodes() {
        return this.nodes;
    }

    public Set<T> successors(T node) {
        return this.successors.getOrDefault(node, Collections.emptySet());
    }

    public Set<T> predecessors(T node) {
        return this.predecessors.getOrDefault(node, Collections.emptySet());
    }
}
