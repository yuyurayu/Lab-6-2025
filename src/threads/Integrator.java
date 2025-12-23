package threads;

import functions.Functions;

public class Integrator extends Thread {
    private Task task;
    private Semaphore semaphore;
    private volatile boolean running = true;
    private int integratedCount = 0;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
        setName("Integrator");
    }

    @Override
    public void run() {
        try {
            while (running) {
                // Используем семафор для чтения
                semaphore.beginRead();

                try {
                    // Получаем задание с гарантией, что оно не пропущено
                    Task.TaskData data = task.getTask();
                    integratedCount++;

                    // Вычисляем интеграл
                    double result = Functions.integrate(
                            data.function,
                            data.left,
                            data.right,
                            data.step
                    );

                    // Выводим результат
                    System.out.printf("  Integrator[%d]: Result %.4f %.4f %.4f %.10f\n",
                            integratedCount, data.left, data.right, data.step, result);

                } finally {
                    semaphore.endRead();
                }

                // Проверяем, все ли задания обработаны
                if (integratedCount >= task.getTasksCount()) {
                    break;
                }

                // Небольшая задержка
                if (running) {
                    Thread.sleep(10);
                }
            }

            System.out.println("  Integrator: Обработал " + integratedCount + " заданий");

        } catch (InterruptedException e) {
            System.out.println("  Integrator: Поток прерван (обработано: " + integratedCount + ")");
        } finally {
            running = false;
        }
    }

    public void stopIntegrator() {
        running = false;
        this.interrupt();
    }

    public int getIntegratedCount() {
        return integratedCount;
    }
}