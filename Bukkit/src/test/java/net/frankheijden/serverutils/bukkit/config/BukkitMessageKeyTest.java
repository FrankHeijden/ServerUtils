package net.frankheijden.serverutils.bukkit.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BukkitMessageKeyTest {

    @ParameterizedTest(name = "key = {0}")
    @MethodSource("bukkitMessageKeyGenerator")
    void testMessageKeyConsistency(BukkitMessageKey key) {
        assertThatCode(() -> BukkitMessageKey.fromPath(key.getPath())).doesNotThrowAnyException();
        assertThat(BukkitMessageKey.fromPath(key.getPath())).isEqualTo(key);
    }

    private static Stream<Arguments> bukkitMessageKeyGenerator() {
        return Arrays.stream(BukkitMessageKey.values())
                .map(Arguments::of);
    }
}
