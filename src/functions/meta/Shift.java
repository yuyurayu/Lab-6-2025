package functions.meta;

import functions.Function;

// Класс для сдвига функции вдоль осей
// f(x) = shiftY + g(x - shiftX)
public class Shift implements Function {
    private Function f;
    private double shiftX;
    private double shiftY;

    public Shift(Function f, double shiftX, double shiftY) {
        this.f = f;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public double getLeftDomainBorder() {
        // Сдвигаем область определения
        return f.getLeftDomainBorder() + shiftX;
    }

    @Override
    public double getRightDomainBorder() {
        return f.getRightDomainBorder() + shiftX;
    }

    @Override
    public double getFunctionValue(double x) {
        double shiftedX = x - shiftX;
        if (shiftedX < f.getLeftDomainBorder() || shiftedX > f.getRightDomainBorder()) {
            return Double.NaN;
        }
        return shiftY + f.getFunctionValue(shiftedX);
    }

    public Function getF() {
        return f;
    }

    public double getShiftX() {
        return shiftX;
    }

    public double getShiftY() {
        return shiftY;
    }
}