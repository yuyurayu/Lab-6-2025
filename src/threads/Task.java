package threads;

import functions.Function;

public class Task {
    private Function function;
    private double left;
    private double right;
    private double step;
    private int tasksCount;

    // Два состояния для предотвращения пропуска данных
    private volatile boolean dataReadyForReading = false;
    private volatile boolean dataReadyForWriting = true;
    private volatile boolean isRunning = true;

    // Счетчики для контроля
    private int generatedCount = 0;
    private int processedCount = 0;

    public Task(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    // Синхронизированный метод установки задания для Generator
    public synchronized void setTask(Function function, double left, double right, double step)
            throws InterruptedException {
        // Ждем, пока предыдущее задание не будет обработано
        while (!dataReadyForWriting && isRunning) {
            wait();
        }

        if (!isRunning) {
            throw new InterruptedException("Task stopped");
        }

        this.function = function;
        this.left = left;
        this.right = right;
        this.step = step;
        generatedCount++;

        dataReadyForWriting = false;
        dataReadyForReading = true;
        notifyAll();
    }

    // Синхронизированный метод получения задания для Integrator
    public synchronized TaskData getTask() throws InterruptedException {
        // Ждем, пока задание не будет готово
        while (!dataReadyForReading && isRunning) {
            wait();
        }

        if (!isRunning) {
            throw new InterruptedException("Task stopped");
        }

        TaskData data = new TaskData(function, left, right, step);
        processedCount++;

        dataReadyForReading = false;
        dataReadyForWriting = true;
        notifyAll();

        return data;
    }

    public void stop() {
        isRunning = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public int getTasksCount() {
        return tasksCount;
    }

    // Вспомогательные методы для SimpleGenerator/SimpleIntegrator
    public Function getFunction() {
        return function;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public double getStep() {
        return step;
    }

    public boolean isReady() {
        return dataReadyForReading;
    }

    public boolean isCompleted() {
        return !dataReadyForReading && dataReadyForWriting;
    }

    // Внутренний класс для безопасной передачи данных
    public static class TaskData {
        public final Function function;
        public final double left;
        public final double right;
        public final double step;

        public TaskData(Function function, double left, double right, double step) {
            this.function = function;
            this.left = left;
            this.right = right;
            this.step = step;
        }
    }
}