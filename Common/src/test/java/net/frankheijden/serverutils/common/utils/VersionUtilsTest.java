package net.frankheijden.serverutils.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VersionUtilsTest {

    @ParameterizedTest(name = "old = {0}, new = {1}, expected = {2}")
    @MethodSource("versionGenerator")
    void isNewVersion(String oldVersion, String newVersion, boolean expected) {
        assertThat(VersionUtils.isNewVersion(oldVersion, newVersion)).isEqualTo(expected);
    }

    private static Stream<Arguments> versionGenerator() {
        return Stream.of(
                of("0", "1", true),
                of("1", "0", false),
                of("9", "10", true),
                of("10", "9", false),
                of("-1", "5", true),
                of("5", "-1", false),
                of("10.1", "10.0", false),
                of("100.0", "120.0", true),
                of("1.0.0", "1.0.1", true),
                of("1.0.0", "1.1.0", true),
                of("1.0.0", "2.0.0", true),
                of("0.0.1", "0.0.1", false),
                of("0.0.1", "0.0.0", false),
                of("0.1.0", "0.0.1", false),
                of("1.0.0", "0.0.1", false),
                of("1.1.0", "0.1.1", false),
                of("1.0.0.0", "1.0.0.1", true),
                of("1.0.1-DEV", "1.0.2", true)
        );
    }
}