package functions;

import functions.meta.*;

// Вспомогательный класс со статическими методами для создания комбинаций функций
// Конструктор приватный, чтобы нельзя было создать экземпляр класса
public class Functions {

    // Приватный конструктор предотвращает создание экземпляров
    private Functions() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр класса Functions");
    }

    // Метод для создания сдвинутой функции
    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    // Метод для создания масштабированной функции
    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    // Метод для создания функции в степени
    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    // Метод для создания суммы двух функций
    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    // Метод для создания произведения двух функций
    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    // Метод для создания композиции двух функций
    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }


    // 1. Метод интегрирования по методу трапеций
    public static double integrate(Function f, double left, double right, double step) {
        if (left < f.getLeftDomainBorder() || right > f.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал интегрирования выходит за границы области определения функции");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг интегрирования должен быть положительным");
        }

        double integral = 0.0;
        double x = left;

        while (x < right) {
            double nextX = Math.min(x + step, right);
            double y1 = f.getFunctionValue(x);
            double y2 = f.getFunctionValue(nextX);

            // Проверка на NaN
            if (Double.isNaN(y1) || Double.isNaN(y2)) {
                return Double.NaN;
            }

            integral += (y1 + y2) * (nextX - x) / 2.0;
            x = nextX;
        }

        return integral;
    }

    // 2. Метод для определения оптимального шага (для тестирования)
    public static double findOptimalStep(Function f, double left, double right, double targetError) {
        double theoretical = Math.exp(1) - 1; // Для exp: ∫₀¹ e^x dx = e - 1
        double step = 0.1;
        double error = Double.MAX_VALUE;

        while (error > targetError) {
            double numerical = integrate(f, left, right, step);
            error = Math.abs(theoretical - numerical);
            step /= 2;
            if (step < 1e-10) break;
        }

        return step * 2; // Возвращаем последний успешный шаг
    }
}