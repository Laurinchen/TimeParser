package eu.laurinchen.timeparser;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TimeParserTest {

    @Test
    void testParseSingle() {
        assertEquals(Duration.ofSeconds(5123L*7*24*60*60), TimeParser.parseSingle('w', "5123"));
        assertNotEquals(Duration.ofSeconds(34879L*60),TimeParser.parseSingle('M', "34879"));
        assertNotEquals(Duration.ofSeconds(1234L*24*60*60), TimeParser.parseSingle('d', "4321") );
        assertThrows(NumberFormatException.class, () ->TimeParser.parseSingle('m', "4321Nope"));
        assertEquals(Duration.ofSeconds(60L*60), TimeParser.parseSingle('h', ""));
        assertThrows(IllegalArgumentException.class, () -> TimeParser.parseSingle('H', "123"));
    }

    @Test
    void testParse() {
        assertEquals(Duration.ofSeconds(123), TimeParser.parse("123s"));
        assertEquals(Duration.ofSeconds(256*60*60*24*7), TimeParser.parse("256w"));
        assertEquals(Duration.ofSeconds(2*60*60*24 + 5*60), TimeParser.parse("2d5m"));
        assertEquals(Duration.ofSeconds(2*60*60*24 + 5*60*60*24*30), TimeParser.parse("2d5M"));
        assertEquals(Duration.ofSeconds(365*60*60*24 + 60*60), TimeParser.parse("yh"));
        assertEquals(Duration.ofSeconds(365*60*60*24 + 60*60 + 5*24*60*60), TimeParser.parse("yh5d"));
        assertThrows(IllegalArgumentException.class, () -> TimeParser.parse("123"));
        assertEquals(Duration.ofSeconds(123*60), TimeParser.parseWithDefault("123", 'm'));
        assertThrows(IllegalArgumentException.class, () -> TimeParser.parse("5d3"));
        assertThrows(IllegalArgumentException.class,
                () -> TimeParser.parseWithDefault("5d123", 'm')
        );
        assertThrows(IllegalArgumentException.class, () ->TimeParser.parse("5x"));
        assertThrows(IllegalArgumentException.class,
                () -> TimeParser.parseWithDefault("123w", 'x')
        );
        assertEquals(Duration.ofSeconds(
                3*365*24*60*60+
                7*30*24*60*60+
                3*7*24*60*60+
                50*24*60*60+
                2*60*60+
                7*60+
                100
        ) ,TimeParser.parse("3y7M3w50d2h7m100s"));
    }
}