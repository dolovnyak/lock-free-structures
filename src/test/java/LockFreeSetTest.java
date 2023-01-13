import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LockFreeSetTest {
    private LockFreeSet<Integer> initSet(List<Integer> values) {
        LockFreeSet<Integer> set = new LockFreeSetImpl<>();
        for (Integer value : values) {
            set.add(value);
        }
        return set;
    }

    @Test
    public void add() {
        LockFreeSet<Integer> testSet = new LockFreeSetImpl<>();

        assertTrue(testSet.add(2));
        assertTrue(testSet.add(9));
        assertFalse(testSet.add(2));
        assertTrue(testSet.add(5));
        assertFalse(testSet.add(9));
        assertTrue(testSet.add(1));

    }

    @Test
    public void remove() {
        LockFreeSet<Integer> testSet = initSet(Arrays.asList(2,9,5,1));

        assertTrue(testSet.remove(2));
        assertTrue(testSet.remove(9));
        assertFalse(testSet.remove(2));
        assertTrue(testSet.remove(1));
        assertTrue(testSet.remove(5));
        assertFalse(testSet.remove(1));
    }

    @Test
    public void contains() {
        LockFreeSet<Integer> testSet = initSet(Arrays.asList(2,9,5,1));

        assertTrue(testSet.contains(2));
        assertTrue(testSet.contains(9));
        assertTrue(testSet.contains(5));
        assertTrue(testSet.contains(1));
        assertFalse(testSet.contains(15));
        assertFalse(testSet.contains(6));
    }

    @Test
    public void isEmpty() {
        LockFreeSet<Integer> testSet = new LockFreeSetImpl<>();
        assertTrue(testSet.empty());

        testSet = initSet(Arrays.asList(2,9,5,1));

        assertFalse(testSet.empty());
        testSet.remove(2);
        assertFalse(testSet.empty());
        testSet.remove(9);
        assertFalse(testSet.empty());
        testSet.remove(5);
        assertFalse(testSet.empty());
        testSet.remove(1);

        assertTrue(testSet.empty());
    }

    @Test
    public void iterator() {
        LockFreeSet<Integer> testSet = initSet(Arrays.asList(2,9,5,1));
        Iterator<Integer> it = testSet.iterator();

        testSet.remove(2);
        testSet.remove(9);

        assertEquals(2, it.next());
        assertEquals(9, it.next());
        assertEquals(5, it.next());
        assertEquals(1, it.next());

        assertThrows(UnsupportedOperationException.class, it::remove);
    }
}