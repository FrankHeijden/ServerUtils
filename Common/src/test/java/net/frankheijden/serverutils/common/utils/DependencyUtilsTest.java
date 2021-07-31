package net.frankheijden.serverutils.common.utils;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.params.provider.Arguments.of;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DependencyUtilsTest {

    @ParameterizedTest(name = "dependencyMap = {0}, expected = {1}")
    @MethodSource("dependencyGenerator")
    void determineOrderDependencies(
            Map<String, Set<String>> dependencyMap,
            List<String> expected
    ) {
        assertThat(DependencyUtils.determineOrder(dependencyMap)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "dependencyMap = {0}")
    @MethodSource("circularDependencyGenerator")
    void determineOrderCircularDependencies(
            Map<String, Set<String>> dependencyMap
    ) {
        assertThatIllegalStateException().isThrownBy(() -> DependencyUtils.determineOrder(dependencyMap));
    }

    private static Stream<Arguments> dependencyGenerator() {
        return Stream.of(
                of(
                        mapOf(
                                new Pair<>("B", asSet("A"))
                        ),
                        asList("A", "B")
                ),
                of(
                        mapOf(
                                new Pair<>("B", asSet("A")),
                                new Pair<>("C", asSet("A", "B"))
                        ),
                        asList("A", "B", "C")
                ),
                of(
                        mapOf(
                                new Pair<>("A", asSet("B")),
                                new Pair<>("B", asSet("C", "D")),
                                new Pair<>("C", asSet()),
                                new Pair<>("D", asSet("C", "E")),
                                new Pair<>("E", asSet("F")),
                                new Pair<>("F", asSet("C"))
                        ),
                        asList("C", "F", "E", "D", "B", "A")
                ),
                of(
                        mapOf(
                                new Pair<>("A", asSet()),
                                new Pair<>("B", asSet()),
                                new Pair<>("C", asSet()),
                                new Pair<>("D", asSet("C"))
                        ),
                        asList("A", "B", "C", "D")
                )
        );
    }

    private static Stream<Arguments> circularDependencyGenerator() {
        return Stream.of(
                of(
                        mapOf(
                                new Pair<>("A", asSet("A"))
                        )
                ),
                of(
                        mapOf(
                                new Pair<>("A", asSet("B")),
                                new Pair<>("B", asSet("A"))
                        )
                ),
                of(
                        mapOf(
                                new Pair<>("B", asSet("A")),
                                new Pair<>("C", asSet("A", "B")),
                                new Pair<>("A", asSet("C"))
                        )
                ),
                of(
                        mapOf(
                                new Pair<>("A", asSet("B")),
                                new Pair<>("B", asSet("C")),
                                new Pair<>("C", asSet("D")),
                                new Pair<>("D", asSet("A"))
                        )
                )
        );
    }

    private static <T> Set<T> asSet(T... elements) {
        return new HashSet<>(asList(elements));
    }

    private static <K, V> Map<K, V> mapOf(Pair<K, V>... pairs) {
        Map<K, V> map = new HashMap<>();
        for (Pair<K, V> pair : pairs) {
            map.put(pair.first, pair.second);
        }
        return map;
    }

    private static final class Pair<A, B> {
        private final A first;
        private final B second;

        private Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}
