package threads;

import functions.basic.Log;
import java.util.Random;

public class SimpleGenerator implements Runnable {
    private Task task;
    private Random random;
    private int generatedCount;

    public SimpleGenerator(Task task) {
        this.task = task;
        this.random = new Random();
        this.generatedCount = 0;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();

        while (generatedCount < tasksCount) {
            synchronized (task) {
                // Используем wait/notify для синхронизации
                while (task.isReady() && !task.isCompleted()) {
                    try {
                        task.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Создаем новое задание
                double base = 1 + random.nextDouble() * 9;
                Log logFunc = new Log(base);
                double left = random.nextDouble() * 100;
                double right = 100 + random.nextDouble() * 100;
                double step = random.nextDouble();

                try {
                    task.setTask(logFunc, left, right, step);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                generatedCount++;

                System.out.printf("  SimpleGenerator[%d]: Source %.4f %.4f %.4f (base=%.4f)\n",
                        generatedCount, left, right, step, base);

                task.notifyAll();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}