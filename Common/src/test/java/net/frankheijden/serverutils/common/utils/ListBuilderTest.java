package net.frankheijden.serverutils.common.utils;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ListBuilderTest {

    private final String seperator = ", ";
    private final String lastSeperator = " and ";

    @Test
    void testToStringOneElement() {
        String list = ListBuilder.createStrings(singletonList("Nice"))
                .seperator(seperator)
                .lastSeperator(lastSeperator)
                .toString();
        assertEquals("Nice", list);
    }

    @Test
    void testToStringTwoElements() {
        String list = ListBuilder.createStrings(asList("Nice", "List"))
                .seperator(seperator)
                .lastSeperator(lastSeperator)
                .toString();
        assertEquals("Nice and List", list);
    }

    @Test
    void testToStringMultipleElements() {
        String list = ListBuilder.createStrings(asList("Nice", "List", "You", "Having", "There"))
                .seperator(seperator)
                .lastSeperator(lastSeperator)
                .toString();
        assertEquals("Nice, List, You, Having and There", list);
    }

    @Test
    void testToStringCustomFormat() {
        List<TestObject> objects = asList(
                new TestObject("pre1", 2),
                new TestObject("pre2", 3),
                new TestObject("pre3", 4)
        );

        String list = ListBuilder.create(objects)
                .format(obj -> obj.prefix + "-" + obj.value)
                .seperator("; ")
                .lastSeperator(" and at last ")
                .toString();
        assertEquals("pre1-2; pre2-3 and at last pre3-4", list);
    }

    private static class TestObject {
        private final String prefix;
        private final int value;

        public TestObject(String prefix, int value) {
            this.prefix = prefix;
            this.value = value;
        }
    }
}