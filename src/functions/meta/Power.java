package functions.meta;

import functions.Function;

// Класс для возведения функции в степень: f(x) = (g(x))^power
public class Power implements Function {
    private Function f;
    private double power;

    public Power(Function f, double power) {
        this.f = f;
        this.power = power;
    }

    @Override
    public double getLeftDomainBorder() {
        // Область определения совпадает с областью определения f
        return f.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return f.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        double value = f.getFunctionValue(x);
        if (Double.isNaN(value)) {
            return Double.NaN;
        }
        return Math.pow(value, power);
    }

    public Function getF() {
        return f;
    }

    public double getPower() {
        return power;
    }
}