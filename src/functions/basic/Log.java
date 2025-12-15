package functions.basic;

import functions.Function;

// Класс для вычисления логарифма с заданным основанием
public class Log implements Function {
    private double base; // Основание логарифма

    // Конструктор принимает основание логарифма
    public Log(double base) {
        if (base <= 0 || base == 1) {
            throw new IllegalArgumentException("Основание логарифма должно быть > 0 и ≠ 1");
        }
        this.base = base;
    }

    @Override
    public double getLeftDomainBorder() {
        // Логарифм определен при x > 0
        return 0 + Double.MIN_VALUE; // Чуть больше 0
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN; // Не определено
        }
        // Используем формулу log_base(x) = ln(x) / ln(base)
        return Math.log(x) / Math.log(base);
    }

    // Геттер для основания
    public double getBase() {
        return base;
    }
}