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
                // Ждем, пока появится задание
                while (!task.isReady() || task.isCompleted()) {
                    try {
                        task.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

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
                System.out.printf("Integrator: Result %.4f %.4f %.4f %.10f%n",
                        left, right, step, result);

                // Уведомляем генератор
                task.notifyAll();
            }

            // Небольшая задержка для наглядности
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}