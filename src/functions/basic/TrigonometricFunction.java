package functions.basic;

import functions.Function;

// Базовый класс для тригонометрических функций
public abstract class TrigonometricFunction implements Function {

    @Override
    public double getLeftDomainBorder() {
        // Все тригонометрические функции определены на всей числовой прямой
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    // Абстрактный метод для вычисления значения
    public abstract double getFunctionValue(double x);
}