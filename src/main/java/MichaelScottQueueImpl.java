import java.util.concurrent.atomic.AtomicReference;

public class MichaelScottQueueImpl<T> implements LockFreeQueue<T> {
    private class Node {
        Node(AtomicReference<Node> next, T value) {
            this.next = next;
            this.value = value;
        }

        AtomicReference<Node> next;
        T value;
    }

    private final AtomicReference<Node> _headRef = new AtomicReference<>(new Node(new AtomicReference<>(null), null));
    private final AtomicReference<Node> _tailRef = new AtomicReference<>(_headRef.get());

    @Override
    public void push(T value) {
        Node newNode = new Node(new AtomicReference<>(null), value);

        while (true) {
            Node tail = _tailRef.get();
            Node tailNext = tail.next.get();

            if (tailNext != null) {
                _tailRef.compareAndSet(tail, tailNext);
            } else if (tail.next.compareAndSet(null, newNode)) {
                _tailRef.compareAndSet(tail, newNode);
                return;
            }
        }
    }

    @Override
    public T pop() {
        while (true) {
            Node head = _headRef.get();
            Node tail = _tailRef.get();
            Node headNext = head.next.get();

            if (head == tail) {
                if (headNext == null) {
                    return null;
                }
                _tailRef.compareAndSet(tail, headNext);
            } else {
                if (_headRef.compareAndSet(head, headNext)) {
                    return headNext.value;
                }
            }
        }
    }

    @Override
    public boolean empty() {
        Node head = _headRef.get();
        Node headNext = head.next.get();
        Node tail = _tailRef.get();
        Node tailNext = tail.next.get();

        if (head == tail) {
            if (headNext == null) {
                return false;
            }
            _tailRef.compareAndSet(tail, headNext);
        }
        else if (tailNext != null) {
            _tailRef.compareAndSet(tail, tailNext);
        }
        return true;
    }
}
