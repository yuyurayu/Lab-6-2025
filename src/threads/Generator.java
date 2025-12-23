package threads;

import functions.basic.Log;
import java.util.Random;

public class Generator extends Thread {
    private Task task;
    private Semaphore semaphore;
    private Random random;
    private volatile boolean running = true;
    private int generatedCount = 0;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
        this.random = new Random();
        setName("Generator");
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();

        try {
            while (generatedCount < tasksCount && running) {
                // Используем семафор для записи
                semaphore.beginWrite();

                try {
                    // Генерируем новое задание
                    double base = 1 + random.nextDouble() * 9;
                    Log logFunc = new Log(base);
                    double left = random.nextDouble() * 100;
                    double right = 100 + random.nextDouble() * 100;
                    double step = random.nextDouble();

                    // Устанавливаем задание с проверкой пропуска
                    task.setTask(logFunc, left, right, step);
                    generatedCount++;

                    // Выводим сообщение
                    System.out.printf("  Generator[%d]: Source %.4f %.4f %.4f (base=%.4f)\n",
                            generatedCount, left, right, step, base);

                } finally {
                    semaphore.endWrite();
                }

                // Небольшая задержка для наглядности
                if (running) {
                    Thread.sleep(10);
                }
            }

            System.out.println("  Generator: Завершил генерацию " + generatedCount + " заданий");

        } catch (InterruptedException e) {
            System.out.println("  Generator: Поток прерван (сгенерировано: " + generatedCount + ")");
        } finally {
            running = false;
            task.stop();
        }
    }

    public void stopGenerator() {
        running = false;
        this.interrupt();
    }

    public int getGeneratedCount() {
        return generatedCount;
    }
}