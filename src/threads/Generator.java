package threads;

import functions.basic.Log;
import java.util.Random;

public class Generator extends Thread {
    private Task task;
    private Semaphore semaphore;
    private Random random;
    private int generatedCount;
    private volatile boolean running = true;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
        this.random = new Random();
        this.generatedCount = 0;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();

        try {
            while (generatedCount < tasksCount && running) {
                semaphore.beginWrite();

                // Создаем новое задание
                double base = 1 + random.nextDouble() * 9;
                Log logFunc = new Log(base);
                double left = random.nextDouble() * 100;
                double right = 100 + random.nextDouble() * 100;
                double step = random.nextDouble();

                // Устанавливаем задание
                task.setTask(logFunc, left, right, step);
                generatedCount++;

                // Выводим сообщение
                System.out.printf("Generator[%d]: Source %.4f %.4f %.4f%n",
                        generatedCount, left, right, step);

                semaphore.endWrite();

                // Небольшая задержка
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("Generator прерван");
        } finally {
            running = false;
        }
    }

    public void stopGenerator() {
        running = false;
        this.interrupt();
    }
}