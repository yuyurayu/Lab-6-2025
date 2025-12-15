package functions;

public interface Function {
    // Метод возвращает левую границу области определения
    double getLeftDomainBorder();

    // Метод возвращает правую границу области определения
    double getRightDomainBorder();

    // Метод вычисляет значение функции в точке x
    double getFunctionValue(double x);
}