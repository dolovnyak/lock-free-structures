import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MSQueueTest {
    private LockFreeQueue<Integer> initQueue(List<Integer> values) {
        LockFreeQueue<Integer> queue = new MichaelScottQueueImpl<>();
        for (Integer value : values) {
            queue.push(value);
        }
        return queue;
    }

    @Test
    public void push_pop() {
        LockFreeQueue<Integer> queue = new MichaelScottQueueImpl<>();

        queue.push(2);
        queue.push(9);
        queue.push(5);
        queue.push(1);

        assertEquals(2, queue.pop());
        assertEquals(9, queue.pop());
        assertEquals(5, queue.pop());
        assertEquals(1, queue.pop());
    }

    @Test
    public void empty() {
        LockFreeQueue<Integer> queue = new MichaelScottQueueImpl<>();
        assertTrue(queue.empty());

        queue = initQueue(Arrays.asList(2,9,5,1));
        assertFalse(queue.empty());

        Integer v = queue.pop();
        assertFalse(queue.empty());
        v = queue.pop();
        assertFalse(queue.empty());
        v = queue.pop();
        assertFalse(queue.empty());
        v = queue.pop();

        assertTrue(queue.empty());
    }
}