package io.github.boredmathematician.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimedHashMapTest {

    private static TimedHashMap<Integer, Integer, Integer> timedHashMap;

    @BeforeEach
    void setup() {
        timedHashMap = new TimedHashMap<>();
    }

    @Test
    void testingForKeysOnOrAfterTime() {
        timedHashMap.set(1, 1, 0);
        timedHashMap.set(1, 2, 2);

        assertEquals(1, timedHashMap.get(1, 1));
        assertEquals(2, timedHashMap.get(1, 3));

    }

    @Test
    void testingForKeysBeforeAndAfterTime() {
        timedHashMap.set(1, 1, 5);

        assertNull(timedHashMap.get(1, 0));
        assertEquals(1, timedHashMap.get(1, 10));

    }

    @Test
    void testingUpdatingValueForKeyAndTime() {
        timedHashMap.set(1, 1, 0);
        timedHashMap.set(1, 2, 0);

        assertEquals(2, timedHashMap.get(1, 0));

    }


}
