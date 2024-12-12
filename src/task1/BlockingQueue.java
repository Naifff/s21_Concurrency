package task1;

public class BlockingQueue<T> {
    private final T[] elements;
    private int size = 0;
    private int head = 0; // индекс для извлечения элементов
    private int tail = 0; // индекс для добавления элементов

    @SuppressWarnings("unchecked")
    public BlockingQueue(int capacity) {
        // Создаем массив заданной емкости
        elements = (T[]) new Object[capacity];
    }

    /**
     * Добавляет элемент в очередь.
     * Если очередь заполнена, блокирует поток до освобождения места.
     */
    public synchronized void enqueue(T element) throws InterruptedException {
        // Ждем, пока появится свободное место в очереди
        while (size == elements.length) {
            wait(); // Освобождаем монитор и ждем notify/notifyAll
        }

        elements[tail] = element;
        tail = (tail + 1) % elements.length; // Циклический перенос указателя
        size++;

        // Уведомляем ожидающие потоки о появлении элемента
        notifyAll();
    }

    /**
     * Извлекает элемент из очереди.
     * Если очередь пуста, блокирует поток до появления элемента.
     */
    public synchronized T dequeue() throws InterruptedException {
        // Ждем, пока в очереди появится хотя бы один элемент
        while (size == 0) {
            wait(); // Освобождаем монитор и ждем notify/notifyAll
        }

        T element = elements[head];
        elements[head] = null; // Помогаем сборщику мусора
        head = (head + 1) % elements.length; // Циклический перенос указателя
        size--;

        // Уведомляем ожидающие потоки об освобождении места
        notifyAll();
        return element;
    }

    @Override
    public String toString() {
        return size + "/" + elements.length;
    }

    /**
     * Возвращает текущее количество элементов в очереди
     */
    public synchronized int size() {
        return size;
    }
}
