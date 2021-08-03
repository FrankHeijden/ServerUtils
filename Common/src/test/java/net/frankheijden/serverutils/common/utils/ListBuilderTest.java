package net.frankheijden.serverutils.common.utils;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

class ListBuilderTest {

    private static final ComponentSerializer<Component, TextComponent, String> plainTextComponentSerializer
            = PlainTextComponentSerializer.plainText();
    private final Component separator = Component.text(", ");
    private final Component lastSeparator = Component.text(" and ");

    @Test
    void testToStringOneElement() {
        Component component = ListComponentBuilder.create(singletonList("Nice"))
                .format(Component::text)
                .separator(separator)
                .lastSeparator(lastSeparator)
                .build();
        assertEquals("Nice", plainTextComponentSerializer.serialize(component));
    }

    @Test
    void testToStringTwoElements() {
        Component component = ListComponentBuilder.create(asList("Nice", "List"))
                .format(Component::text)
                .separator(separator)
                .lastSeparator(lastSeparator)
                .build();
        assertEquals("Nice and List", plainTextComponentSerializer.serialize(component));
    }

    @Test
    void testToStringMultipleElements() {
        Component component = ListComponentBuilder.create(asList("Nice", "List", "You", "Having", "There"))
                .format(Component::text)
                .separator(separator)
                .lastSeparator(lastSeparator)
                .build();
        assertEquals("Nice, List, You, Having and There", plainTextComponentSerializer.serialize(component));
    }

    @Test
    void testToStringCustomFormat() {
        List<TestObject> objects = asList(
                new TestObject("pre1", 2),
                new TestObject("pre2", 3),
                new TestObject("pre3", 4)
        );

        Component component = ListComponentBuilder.create(objects)
                .format(obj -> Component.text(obj.prefix + "-" + obj.value))
                .separator(Component.text("; "))
                .lastSeparator(Component.text(" and at last "))
                .build();
        assertEquals("pre1-2; pre2-3 and at last pre3-4", plainTextComponentSerializer.serialize(component));
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