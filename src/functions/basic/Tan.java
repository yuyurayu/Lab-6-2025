package functions.basic;

// Класс для вычисления тангенса
public class Tan extends TrigonometricFunction {

    @Override
    public double getLeftDomainBorder() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        // Тангенс не определен при cos(x) = 0
        double cosX = Math.cos(x);
        if (Math.abs(cosX) < 1e-10) { // Используем машинный эпсилон
            return Double.NaN;
        }
        return Math.tan(x);
    }
}