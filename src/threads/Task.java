package threads;

import functions.Function;

public class Task {
    private Function function;
    private double left;
    private double right;
    private double step;
    private int tasksCount;
    private volatile boolean isReady = false;
    private volatile boolean isCompleted = false;

    // Конструктор
    public Task(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    // Геттеры и сеттеры
    public synchronized void setTask(Function function, double left, double right, double step) {
        this.function = function;
        this.left = left;
        this.right = right;
        this.step = step;
        isReady = true;
        isCompleted = false;
    }

    public synchronized void getTask() {
        isReady = false;
    }

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

    public int getTasksCount() {
        return tasksCount;
    }

    public boolean isReady() {
        return isReady;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}