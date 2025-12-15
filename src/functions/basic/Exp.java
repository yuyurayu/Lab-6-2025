package functions.basic;

import functions.Function;

// Класс для вычисления экспоненты e^x
public class Exp implements Function {

    @Override
    public double getLeftDomainBorder() {
        // Комментарий: Экспонента определена на всей числовой прямой
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        // Используем стандартную функцию Math.exp
        return Math.exp(x);
    }
}