package functions.meta;

import functions.Function;

// Класс для суммы двух функций: f(x) = f1(x) + f2(x)
public class Sum implements Function {
    private Function f1;
    private Function f2;

    // Конструктор принимает две функции
    public Sum(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    @Override
    public double getLeftDomainBorder() {
        // Область определения - пересечение областей
        return Math.max(f1.getLeftDomainBorder(), f2.getLeftDomainBorder());
    }

    @Override
    public double getRightDomainBorder() {
        return Math.min(f1.getRightDomainBorder(), f2.getRightDomainBorder());
    }

    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что x принадлежит области определения
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        return f1.getFunctionValue(x) + f2.getFunctionValue(x);
    }

    // Геттеры для функций
    public Function getF1() {
        return f1;
    }

    public Function getF2() {
        return f2;
    }
}