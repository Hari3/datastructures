package datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimedMapSpec {

    private static TimedMap<Integer, Integer, Integer> timedMap;

    @BeforeEach
    void setup() {
        timedMap = new TimedMap<>();
    }

    @Test
    void testingForKeysOnOrAfterTime() {
        timedMap.set(1, 1, 0);
        timedMap.set(1, 2, 2);

        assertEquals(1, timedMap.get(1, 1));
        assertEquals(2, timedMap.get(1, 3));

    }

    @Test
    void testingForKeysBeforeAndAfterTime() {
        timedMap.set(1, 1, 5);

        assertNull(timedMap.get(1, 0));
        assertEquals(1, timedMap.get(1, 10));

    }

    @Test
    void testingUpdatingValueForKeyAndTime() {
        timedMap.set(1, 1, 0);
        timedMap.set(1, 2, 0);

        assertEquals(2, timedMap.get(1, 0));

    }


}
