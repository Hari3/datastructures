package datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MinDequeTest {

    @Test
    void elementShouldBeReturnedWhenPopIsCalledAfterPush() {
        MinDeque<Integer> q = new MinDeque<>();

        int expected = 20;

        q.push(20);
        int actual = q.pop();

        assertEquals(expected, actual);
    }

    @Test
    void popShouldFollowFIFOOrdering() {
        MinDeque<Integer> q = new MinDeque<>();

        int[] input = {12, 5, 10, 7, 11, 19};

        for (int element : input) {
            q.push(element);
        }

        for (int element : input) {
            assertEquals(element, q.pop());
        }

    }

    @Test
    void minimumValueShouldBeReturnedWhenQueueHasOneElement() {
        MinDeque<Integer> q = new MinDeque<>();

        int expected = 20;

        q.push(20);
        int actual = q.getMin();

        assertEquals(expected, actual);
    }

    @Test
    void gettingMinimumValueShouldNotAffectTheQueue() {
        MinDeque<Integer> q = new MinDeque<>();

        int expected = 20;

        q.push(20);
        q.getMin();
        int actual = q.pop();

        assertEquals(expected, actual);
    }

    @Test
    void poppingEmptyQueueShouldReturnNull() {
        MinDeque<Integer> q = new MinDeque<>();

        Integer actual = q.pop();

        assertNull(actual);
    }

    @Test
    void minimumValueOfAnEmptyQueueShouldBeNull() {
        MinDeque<Integer> q = new MinDeque<>();

        Integer actual = q.getMin();

        assertNull(actual);
    }


    @Test
    void getMinShouldAlwaysReturnTheMinimumValueInTheQueueWhenPushing() {
        MinDeque<Integer> q = new MinDeque<>();

        int[] input = {12, 5, 10, 7, 11, 19};
        int min = Integer.MAX_VALUE;

        for (int element : input) {
            if (element < min) {
                min = element;
            }
            q.push(element);
            assertEquals(min, q.getMin());
        }


    }

    @Test
    void getMinShouldAlwaysReturnTheMinimumValueInTheQueueWhenPopping() {
        MinDeque<Integer> q = new MinDeque<>();

        int[] input = {12, 5, 10, 7, 11, 19};
        int[] minValues = {5, 7, 11, 19};
        int minIndex = 0;

        for (int element : input) {
            q.push(element);
        }

        for (int i = 0; i < input.length - 1; i++) {
            int element = q.pop();
            if (element == minValues[minIndex])
                minIndex++;

            assertEquals(minValues[minIndex], q.getMin());

        }

        q.pop();
        assertNull(q.getMin());

    }


    @Test
    void queueSizeShouldBeEqualToNumberOfElementsInQueue() {
        MinDeque<Integer> q = new MinDeque<>();

        int[] input = {12, 5, 10, 7, 11, 19};

        for (int i = 0; i < input.length; i++) {
            q.push(input[i]);
            assertEquals(i + 1, q.size());
        }

        for (int i = input.length; i > 0; i--) {
            q.pop();
            assertEquals(i - 1, q.size());
        }

    }
}
