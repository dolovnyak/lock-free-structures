public interface LockFreeQueue<T> {
    void push(T value);

    T pop();

    boolean empty();
}
