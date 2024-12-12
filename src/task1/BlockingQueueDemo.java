package task1;

/**
 * Практическая задача - Concurrency - блокирующая очередь
 * Предположим, у вас есть пул потоков, и вы хотите реализовать блокирующую очередь для передачи задач между потоками.
 * Создайте класс task1.BlockingQueue, который будет обеспечивать безопасное добавление и извлечение элементов между
 * производителями и потребителями в контексте пула потоков.
 * Класс task1.BlockingQueue должен содержать методы enqueue() для добавления элемента в очередь и dequeue() для извлечения
 * элемента. Если очередь пуста, dequeue() должен блокировать вызывающий поток до появления нового элемента.
 * очередь должна иметь фиксированный размер.
 * Используйте механизмы wait() и notify() для координации между производителями и потребителями. Реализуйте метод
 * size(), который возвращает текущий размер очереди.
 */

public class BlockingQueueDemo {
    public static void main(String[] args) {
        // Создаем очередь на 5 элементов
        BlockingQueue<Task> queue = new BlockingQueue<>(5);

        // Создаем и запускаем первого производителя (генерирует задачи типа A)
        Thread producerA = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    Task task = new Task("A" + i, "Задача от производителя A");
                    queue.enqueue(task);
                    System.out.println(Thread.currentThread().getName() +
                            " создал задачу: " + task.id());
                    // Случайная задержка от 100 до 500 мс
                    Thread.sleep((long) (Math.random() * 400 + 100));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Производитель A был прерван");
            }
        }, "Производитель-A");

        // Создаем и запускаем второго производителя (генерирует задачи типа B)
        Thread producerB = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    Task task = new Task("B" + i, "Задача от производителя B");
                    queue.enqueue(task);
                    System.out.println(Thread.currentThread().getName() +
                            " создал задачу: " + task.id());
                    // Случайная задержка от 200 до 700 мс
                    Thread.sleep((long) (Math.random() * 500 + 200));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Производитель B был прерван");
            }
        }, "Производитель-B");

        // Создаем и запускаем потребителя
        Thread consumer = new Thread(() -> {
            try {
                // Обрабатываем 10 задач (по 5 от каждого производителя)
                for (int i = 0; i < 10; i++) {
                    Task task = queue.dequeue();
                    System.out.println(Thread.currentThread().getName() +
                            " обработал задачу: " + task.id() +
                            " [Размер очереди: " + queue.size() + "]");
                    // Симулируем обработку задачи
                    Thread.sleep((long) (Math.random() * 300 + 200));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Потребитель был прерван");
            }
        }, "Потребитель");

        // Вспомогательный поток для мониторинга состояния очереди
        Thread monitor = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    System.out.println("=== Состояние очереди: " + queue);
                    Thread.sleep(1000); // Мониторим каждую секунду
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Монитор");

        // Запускаем все потоки
        System.out.println("Запуск демонстрации работы task1.BlockingQueue...");
        producerA.start();
        producerB.start();
        consumer.start();
        monitor.start();

        // Ждем завершения основных потоков
        try {
            producerA.join();
            producerB.join();
            consumer.join();
            // Прерываем поток монитора после завершения основных потоков
            monitor.interrupt();
        } catch (InterruptedException e) {
            System.out.println("Основной поток был прерван");
        }

        System.out.println("Демонстрация завершена");
    }

    // Внутренний класс для представления задачи
    record Task(String id, String description) {

        @Override
        public String toString() {
            return "Task[" + id + "]";
        }
    }
}