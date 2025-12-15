package functions.basic;

// Класс для вычисления косинуса
public class Cos extends TrigonometricFunction {

    @Override
    public double getFunctionValue(double x) {
        return Math.cos(x);
    }
}