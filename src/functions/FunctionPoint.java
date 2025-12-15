package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;

    // Конструкторы
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point) {
        this(point.x, point.y);
    }

    public FunctionPoint() {
        this(0, 0);
    }

    // Геттеры и сеттеры
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    // 1. Метод toString()
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    // 2. Метод equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionPoint that = (FunctionPoint) o;
        return Math.abs(x - that.x) < 1e-10 && Math.abs(y - that.y) < 1e-10;
    }

    // 3. Метод hashCode()
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        int x1 = (int)(xBits & 0xFFFFFFFF);
        int x2 = (int)(xBits >>> 32);
        int y1 = (int)(yBits & 0xFFFFFFFF);
        int y2 = (int)(yBits >>> 32);
        return x1 ^ x2 ^ y1 ^ y2;
    }

    // 4. Метод clone()
    @Override
    public Object clone() {
        try {
            return super.clone(); // Простое клонирование
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Клонирование не поддерживается", e);
        }
    }
}