package net.frankheijden.serverutils.common.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MessageKeyTest {

    @ParameterizedTest(name = "key = {0}")
    @MethodSource("messageKeyGenerator")
    void testMessageKeyConsistency(MessageKey key) {
        assertThatCode(() -> MessageKey.fromPath(key.getPath())).doesNotThrowAnyException();
        assertThat(MessageKey.fromPath(key.getPath())).isEqualTo(key);
    }

    private static Stream<Arguments> messageKeyGenerator() {
        return Arrays.stream(MessageKey.values())
                .map(Arguments::of);
    }
}
