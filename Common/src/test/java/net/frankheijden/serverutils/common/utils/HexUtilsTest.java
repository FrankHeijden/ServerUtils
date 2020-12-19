package net.frankheijden.serverutils.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class HexUtilsTest {

    @ParameterizedTest(name = "color = {0}, expected = {1}")
    @CsvSource({
            "0, &x&0",
            "10, &x&1&0",
            "789344, &x&7&8&9&3&4&4",
    })
    void convertHexColor(String color, String expected) {
        assertThat(HexUtils.convertHexColor(color)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "hexString = {0}, expected = {1}")
    @MethodSource("hexStringGenerator")
    void convertHexString(String hexString, String expected) {
        assertThat(HexUtils.convertHexString(hexString)).isEqualTo(expected);
    }

    private static Stream<Arguments> hexStringGenerator() {
        return Stream.of(
                of("<#FFFFFF>", "&x&F&F&F&F&F&F"),
                of("Hey <#FFFFFF>", "Hey &x&F&F&F&F&F&F"),
                of("Hey<#FFFFFF>Hey2<#AAAAAA>", "Hey&x&F&F&F&F&F&FHey2&x&A&A&A&A&A&A"),
                of("<#FFFFFF><#AAAAAA><#FFFFFF>", "&x&F&F&F&F&F&F&x&A&A&A&A&A&A&x&F&F&F&F&F&F"),
                of("<#FFFFFF>#FFFFFF", "&x&F&F&F&F&F&F#FFFFFF"),
                of("<#FFFFFF>FFFFFF", "&x&F&F&F&F&F&FFFFFFF"),
                of("<<#FFFFFF>>", "<&x&F&F&F&F&F&F>"),
                of("<#<#ABCDEF>>", "<#&x&A&B&C&D&E&F>")
        );
    }
}