package threads;

import functions.Functions;

public class Integrator extends Thread {
    private Task task;
    private Semaphore semaphore;
    private int integratedCount;
    private volatile boolean running = true;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
        this.integratedCount = 0;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();

        try {
            while (integratedCount < tasksCount && running) {
                semaphore.beginRead();

                // Проверяем, готово ли задание
                if (task.isReady() && !task.isCompleted()) {
                    // Получаем задание
                    double left = task.getLeft();
                    double right = task.getRight();
                    double step = task.getStep();

                    // Вычисляем интеграл
                    double result = Functions.integrate(task.getFunction(), left, right, step);

                    // Отмечаем как выполненное
                    task.setCompleted(true);
                    integratedCount++;

                    // Выводим результат
                    System.out.printf("Integrator[%d]: Result %.4f %.4f %.4f %.10f%n",
                            integratedCount, left, right, step, result);
                }

                semaphore.endRead();

                // Небольшая задержка
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator прерван");
        } finally {
            running = false;
        }
    }

    public void stopIntegrator() {
        running = false;
        this.interrupt();
    }
}