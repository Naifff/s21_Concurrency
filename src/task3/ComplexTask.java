package task3;

import java.util.Random;

public class ComplexTask {
	private final int taskId;
	private final Random random;

	public ComplexTask(int taskId) {
		this.taskId = taskId;
		this.random = new Random();
	}

	public int execute() {
		try {
			// Имитируем выполнение сложной работы
			int processingTime = 1000 + random.nextInt(2000);
			Thread.sleep(processingTime);

			// Генерируем некоторый результат работы
			int result = random.nextInt(100);
			System.out.printf("Task %d completed with result: %d (took %d ms)%n",
					taskId, result, processingTime);

			return result;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return -1;
		}
	}
}