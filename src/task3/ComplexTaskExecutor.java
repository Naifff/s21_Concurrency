package task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ComplexTaskExecutor {
	private final int numberOfThreads;

	public ComplexTaskExecutor(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public void executeTasks(int numberOfTasks) {
		// Создаем отдельный ExecutorService для этого набора задач
		try (ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads)) {
			List<Integer> results = new ArrayList<>();
			Object resultsLock = new Object();

			try {
				// Создаем защелку для ожидания завершения всех задач
				CountDownLatch completionLatch = new CountDownLatch(1);

				// Создаем барьер для синхронизации завершения задач
				CyclicBarrier barrier = new CyclicBarrier(numberOfTasks, () -> {
					System.out.println("\nAll tasks in current set completed!");
					synchronized (resultsLock) {
						int totalResult = results.stream().mapToInt(Integer::intValue).sum();
						System.out.printf("Total result for current set: %d%n", totalResult);
					}
					completionLatch.countDown(); // Сигнализируем о завершении всех задач
				});

				// Создаем и запускаем задачи
				List<Future<?>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfTasks; i++) {
					final int taskId = i;
					Future<?> future = executorService.submit(() -> {
						try {
							ComplexTask task = new ComplexTask(taskId);
							int result = task.execute();

							synchronized (resultsLock) {
								results.add(result);
							}

							System.out.printf("Task %d waiting at barrier%n", taskId);
							barrier.await();
						} catch (InterruptedException | BrokenBarrierException e) {
							Thread.currentThread().interrupt();
						}
					});
					futures.add(future);
				}

				// Ждем завершения всех задач
				completionLatch.await();

				// Отменяем все незавершенные задачи, если такие есть
				for (Future<?> future : futures) {
					if (!future.isDone()) {
						future.cancel(true);
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				// Всегда закрываем ExecutorService
				executorService.shutdown();
				try {
					// Ждем завершения всех задач максимум 10 секунд
					if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
						executorService.shutdownNow();
					}
				} catch (InterruptedException e) {
					executorService.shutdownNow();
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}