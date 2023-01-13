import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeSetImpl<T extends Comparable<T>> implements LockFreeSet<T> {
    @Override
    public boolean add(T value) {
        while (true) {
            FindResult findResult = find(value);
            if (findResult.foundedNode != null) {
                return false;
            }

            Node newNode = new Node(new AtomicMarkableReference<>(null, false), value);

            if (findResult.prev.nextAndFlag.compareAndSet(null, newNode, false, false)) {
                return true;
            }
        }
    }

    @Override
    public boolean remove(T value) {
        while (true) {
            FindResult findResult = find(value);
            if (findResult.foundedNode == null) {
                return false;
            }

            if (findResult.foundedNode.nextAndFlag.compareAndSet(findResult.foundedNode.nextAndFlag.getReference(), findResult.foundedNode.nextAndFlag.getReference(), false, true)) {
                /// We never change foundedNode.nextAndFlag if it marked as obsolete.
                /// We either delete this element in current thread or delete it during find in other threads.
                findResult.prev.nextAndFlag.compareAndSet(findResult.foundedNode, findResult.foundedNode.nextAndFlag.getReference(), false, false);
                return true;
            }
        }
    }

    @Override
    public boolean contains(T value) {
        return find(value).foundedNode != null;
    }

    @Override
    public boolean empty() {
        Node cur = head.nextAndFlag.getReference();

        while (cur != null) {
            if (!cur.nextAndFlag.isMarked()) {
                return false;
            }
            cur = cur.nextAndFlag.getReference();
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        while (true) {
            ArrayList<T> snapshot1 = takeSnapshot();
            ArrayList<T> snapshot2 = takeSnapshot();
            if (snapshot1.equals(snapshot2)) {
                return new SetIterator<>(snapshot1.iterator());
            }
        }
    }

    private static class SetIterator<T> implements Iterator<T> {
        public SetIterator(Iterator<T> iterator) {
            _iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return _iterator.hasNext();
        }

        @Override
        public T next() {
            return _iterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private final Iterator<T> _iterator;
    }

    private class Node {
        Node(AtomicMarkableReference<Node> nextAndFlag, T value) {
            this.nextAndFlag = nextAndFlag;
            this.value = value;
        }

        AtomicMarkableReference<Node> nextAndFlag;
        T value;
    }

    private class FindResult {
        FindResult(Node prev, Node foundedNode) {
            this.prev = prev;
            this.foundedNode = foundedNode;
        }

        Node prev;
        Node foundedNode;
    }

    private final Node head = new Node(new AtomicMarkableReference<>(null, false), null);

    private FindResult find(T value) {
        Node prev = head;
        Node current = head.nextAndFlag.getReference(); /// head node always exist

        while (current != null) {
            /// delete obsolete node on meeting.
            if (current.nextAndFlag.isMarked()) {
                if (prev.nextAndFlag.compareAndSet(current, current.nextAndFlag.getReference(), false, false)) {
                    current = current.nextAndFlag.getReference();
                }
                else {
                    current = prev.nextAndFlag.getReference();
                }
                continue;
            }


            if (value.compareTo(current.value) == 0) {
                return new FindResult(prev, current);
            }

            prev = current;
            current = current.nextAndFlag.getReference();
        }
        return new FindResult(prev, null);
    }

    private ArrayList<T> takeSnapshot() {
        ArrayList<T> snapshot = new ArrayList<>();
        Node current = head.nextAndFlag.getReference();

        while (current != null) {
            if (!current.nextAndFlag.isMarked()) {
                snapshot.add(current.value);
            }
            current = current.nextAndFlag.getReference();
        }
        return snapshot;
    }

}
