package dev.mockboard.common.faker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateFakerProcessorTest {

    private final TemplateFakerProcessor engine = new TemplateFakerProcessor();

    @Test
    void replaceValidTokens() {
        var input = "{\"name\": \"{{user.fullName}}\", \"email\": \"{{user.email}}\"}";
        var result = requireString(engine.applyFaker(input));
        assertDoesNotContain(result, "{{user.fullName}}");
        assertDoesNotContain(result, "{{user.email}}");
        assertContains(result, "\"name\": \"");
        assertContains(result, "@");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{{user.fullName}}",
            "{{  user.fullName  }}",
            "{{\tuser.fullName\n}}",
            "{{       user.fullName       }}"
    })
    void handleWhitespaces(String token) {
        var result = requireString(engine.applyFaker(token));

        assertDoesNotContain(result, "{{");
        assertDoesNotContain(result, "unknown");
        assertTrue(!result.isEmpty());
    }

    @Test
    void preventNotPairMatching() {
        var input = """
            {
              "username": "{{ test",
              "address": " test 2}}"
            }
            """;

        var result = engine.applyFaker(input);
        assertThat(result).isEqualTo(input);
    }

    @Test
    void unknownTokens() {
        var input = "Hello {{non.existent}}";
        var result = engine.applyFaker(input);
        assertThat(result).isEqualTo("Hello [unknown: non.existent]");
    }

    @Test
    void edgeCases() {
        assertThat(engine.applyFaker(null)).isNull();
        assertThat(engine.applyFaker("")).isEmpty();
        assertThat(engine.applyFaker("   ")).isEqualTo("   ");
    }

    @Test
    void ignoreLongKeys() {
        var longKey = "z".repeat(50);
        var input = "{{" + longKey + "}}";

        var result = engine.applyFaker(input);
        assertThat(result).isEqualTo(input);
    }

    @Test
    void multipleIdenticalTokens() {
        var input = "{{user.fullName}}{{user.fullName}}";
        var result = requireString(engine.applyFaker(input));
        assertDoesNotContain(result, "{");
        assertDoesNotContain(result, "}");
        assertDoesNotContain(result, "unknown");
    }

    @Test
    void extraCurlyBraces() {
        var input = "{{{user.fullName}}}";
        var result = engine.applyFaker(input);
        assertThat(result)
                .isEqualTo(input);
    }

    @Test
    void systemTemplates() {
        var input = "{{system.int}}, {{system.long}}, {{system.double}}, {{system.bool}}, {{system.uuid}}";
        var result = requireString(engine.applyFaker(input));
        assertDoesNotContain(result, "{{");
        assertDoesNotContain(result, "}}");
        assertDoesNotContain(result, "unknown");

        var parts = result.split(",");
        assertThat(parts).hasSize(5);
        
        var intValue = Integer.parseInt(parts[0].trim());
        assertTrue(intValue >= 0 && intValue <= 10000);
        assertInstanceOf(Integer.class, intValue);
        assertInstanceOf(Long.class, Long.parseLong(parts[1].trim()));
        assertInstanceOf(Double.class, Double.parseDouble(parts[2].trim()));
        assertTrue(parts[3].trim().matches("true|false"));
        assertNotNull(UUID.fromString(parts[4].trim()));
    }

    private void assertContains(String actual, String expected) {
        assertTrue(actual.contains(expected));
    }

    private void assertDoesNotContain(String actual, String unexpected) {
        assertFalse(actual.contains(unexpected));
    }

    private String requireString(String actual) {
        return java.util.Objects.requireNonNull(actual);
    }
}
