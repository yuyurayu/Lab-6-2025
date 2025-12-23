package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private Task task;
    private int integratedCount;

    public SimpleIntegrator(Task task) {
        this.task = task;
        this.integratedCount = 0;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();

        while (integratedCount < tasksCount) {
            synchronized (task) {
                while (!task.isReady() || task.isCompleted()) {
                    try {
                        task.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                try {
                    // Получаем данные через TaskData для безопасности
                    Task.TaskData data = task.getTask();
                    integratedCount++;

                    double result = Functions.integrate(
                            data.function,
                            data.left,
                            data.right,
                            data.step
                    );

                    System.out.printf("  SimpleIntegrator[%d]: Result %.4f %.4f %.4f %.10f\n",
                            integratedCount, data.left, data.right, data.step, result);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

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