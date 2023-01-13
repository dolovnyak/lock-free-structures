public interface LockFreeSet<T extends Comparable<T>> {
    boolean add(T value);

    boolean remove(T value);

    boolean contains(T value);

    boolean empty();

    java.util.Iterator<T> iterator();
}
