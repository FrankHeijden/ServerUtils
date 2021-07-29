package net.frankheijden.serverutils.common.utils;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencyUtils {

    private DependencyUtils() {}

    /**
     * Determines the topological order of a dependency map.
     * Adapted from https://github.com/VelocityPowered/Velocity.
     * @throws IllegalStateException Iff circular dependency.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static <T> List<T> determineOrder(Map<T, Set<T>> dependencyMap) throws IllegalStateException {
        MutableGraph<T> dependencyGraph = GraphBuilder.directed().allowsSelfLoops(true).build();
        for (T node : dependencyMap.keySet()) {
            dependencyGraph.addNode(node);
        }

        for (Map.Entry<T, Set<T>> entry : dependencyMap.entrySet()) {
            for (T dependency : entry.getValue()) {
                dependencyGraph.putEdge(entry.getKey(), dependency);
            }
        }

        List<T> orderedList = new ArrayList<>(dependencyMap.size());
        Map<T, Mark> marks = new HashMap<>(dependencyMap.size());

        for (T node : dependencyGraph.nodes()) {
            visitNode(dependencyGraph, node, marks, orderedList, new LinkedList<>());
        }

        return orderedList;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static <T> void visitNode(
            Graph<T> dependencyGraph,
            T node,
            Map<T, Mark> marks,
            List<T> orderedList,
            Deque<T> currentIteration
    ) throws IllegalStateException {
        Mark mark = marks.getOrDefault(node, Mark.NOT_VISITED);
        if (mark == Mark.PERMANENT) {
            return;
        } else if (mark == Mark.TEMPORARY) {
            currentIteration.addLast(node);

            StringBuilder sb = new StringBuilder();
            for (T currentNode : currentIteration) {
                sb.append(" -> ").append(currentNode);
            }

            throw new IllegalStateException("Circular dependency detected: " + sb.substring(4));
        }

        currentIteration.addLast(node);
        marks.put(node, Mark.TEMPORARY);

        for (T successorNode : dependencyGraph.successors(node)) {
            visitNode(dependencyGraph, successorNode, marks, orderedList, currentIteration);
        }

        marks.put(node, Mark.PERMANENT);
        currentIteration.removeLast();
        orderedList.add(node);
    }

    private enum Mark {
        NOT_VISITED,
        TEMPORARY,
        PERMANENT
    }
}
































