package functions.meta;

import functions.Function;

// Класс для масштабирования функции вдоль осей
// f(x) = scaleY * g(scaleX * x)
public class Scale implements Function {
    private Function f;
    private double scaleX;
    private double scaleY;

    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        // Масштабирование области определения вдоль оси X
        if (scaleX > 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getRightDomainBorder() / scaleX;
        } else {
            // При scaleX = 0 функция вырождается в константу
            return Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    public double getRightDomainBorder() {
        if (scaleX > 0) {
            return f.getRightDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public double getFunctionValue(double x) {
        // Масштабируем аргумент и результат
        double scaledX = scaleX * x;
        if (scaledX < f.getLeftDomainBorder() || scaledX > f.getRightDomainBorder()) {
            return Double.NaN;
        }
        return scaleY * f.getFunctionValue(scaledX);
    }

    public Function getF() {
        return f;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }
}