package dev.mockboard.common.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateFakerEngineTest {

    private final TemplateFakerEngine engine = new TemplateFakerEngine();

    @Test
    void replaceValidTokens() {
        var input = "{\"name\": \"{{user.fullName}}\", \"email\": \"{{user.email}}\"}";
        var result = engine.applyFaker(input);
        assertThat(result)
                .doesNotContain("{{user.fullName}}")
                .doesNotContain("{{user.email}}")
                .contains("\"name\": \"")
                .contains("@");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{{user.fullName}}",
            "{{  user.fullName  }}",
            "{{\tuser.fullName\n}}",
            "{{       user.fullName       }}"
    })
    void handleWhitespaces(String token) {
        var result = engine.applyFaker(token);

        assertThat(result)
                .doesNotContain("{{")
                .doesNotContain("unknown")
                .isNotEmpty();
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
        var result = engine.applyFaker(input);
        assertThat(result)
                .doesNotContain("{")
                .doesNotContain("}")
                .doesNotContain("unknown");
    }

    @Test
    void extraCurlyBraces() {
        var input = "{{{user.fullName}}}";
        var result = engine.applyFaker(input);
        assertThat(result)
                .isEqualTo(input);
    }
}