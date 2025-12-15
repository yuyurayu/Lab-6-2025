package functions.meta;

import functions.Function;

// Класс для композиции двух функций: f(x) = g(h(x))
public class Composition implements Function {
    private Function outer;
    private Function inner;

    public Composition(Function outer, Function inner) {
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    public double getLeftDomainBorder() {
        // Область определения - область определения inner
        return inner.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return inner.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        // Вычисляем h(x), затем g(h(x))
        double innerValue = inner.getFunctionValue(x);
        if (Double.isNaN(innerValue)) {
            return Double.NaN;
        }
        return outer.getFunctionValue(innerValue);
    }

    public Function getOuter() {
        return outer;
    }

    public Function getInner() {
        return inner;
    }
}