package functions;

import java.io.Serializable;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable, Cloneable {
    private static final long serialVersionUID = 3L;

    private static class FunctionNode implements Serializable {
        private static final long serialVersionUID = 4L;
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private FunctionNode head;
    private int pointsCount;
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;

    // Конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointsCount = 0;
        this.lastAccessedNode = null;
        this.lastAccessedIndex = -1;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail(new FunctionPoint(x, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this(leftX, rightX, values.length);
        FunctionNode current = head.next;
        for (int i = 0; i < values.length; i++) {
            current.point.setY(values[i]);
            current = current.next;
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Необходимо минимум 2 точки");
        }

        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i - 1].getX()) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию X");
            }
        }

        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        this.pointsCount = 0;
        this.lastAccessedNode = null;
        this.lastAccessedIndex = -1;

        for (FunctionPoint point : points) {
            addNodeToTail(new FunctionPoint(point));
        }
    }

    // Вспомогательные методы
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if (lastAccessedNode != null && lastAccessedIndex != -1) {
            if (index == lastAccessedIndex) {
                return lastAccessedNode;
            } else if (index == lastAccessedIndex + 1 && index < pointsCount) {
                lastAccessedNode = lastAccessedNode.next;
                lastAccessedIndex++;
                return lastAccessedNode;
            } else if (index == lastAccessedIndex - 1 && index >= 0) {
                lastAccessedNode = lastAccessedNode.prev;
                lastAccessedIndex--;
                return lastAccessedNode;
            }
        }

        FunctionNode current;
        if (index < pointsCount / 2) {
            current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = pointsCount - 1; i > index; i--) {
                current = current.prev;
            }
        }

        lastAccessedNode = current;
        lastAccessedIndex = index;
        return current;
    }

    private FunctionNode addNodeToTail(FunctionPoint point) {
        FunctionNode newNode = new FunctionNode(new FunctionPoint(point));
        FunctionNode tail = head.prev;

        newNode.prev = tail;
        newNode.next = head;
        tail.next = newNode;
        head.prev = newNode;

        pointsCount++;
        lastAccessedNode = newNode;
        lastAccessedIndex = pointsCount - 1;

        return newNode;
    }

    private FunctionNode addNodeByIndex(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if (index == pointsCount) {
            return addNodeToTail(point);
        }

        FunctionNode nodeAtIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(new FunctionPoint(point));

        newNode.prev = nodeAtIndex.prev;
        newNode.next = nodeAtIndex;
        nodeAtIndex.prev.next = newNode;
        nodeAtIndex.prev = newNode;

        pointsCount++;
        lastAccessedNode = newNode;
        lastAccessedIndex = index;

        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        if (pointsCount < 3) {
            throw new IllegalStateException("Невозможно удалить точку: должно остаться минимум 2 точки");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);
        FunctionNode prevNode = nodeToDelete.prev;
        FunctionNode nextNode = nodeToDelete.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        pointsCount--;
        if (lastAccessedNode == nodeToDelete) {
            lastAccessedNode = null;
            lastAccessedIndex = -1;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }
        return nodeToDelete;
    }

    // Методы интерфейса Function
    @Override
    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    @Override
    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        FunctionNode current = head.next;
        while (current.next != head && x > current.next.point.getX()) {
            current = current.next;
        }

        if (Math.abs(x - current.point.getX()) < 1e-10) {
            return current.point.getY();
        }

        if (current.next != head && Math.abs(x - current.next.point.getX()) < 1e-10) {
            return current.next.point.getY();
        }

        double x1 = current.point.getX();
        double y1 = current.point.getY();
        double x2 = current.next.point.getX();
        double y2 = current.next.point.getY();

        double k = (y2 - y1) / (x2 - x1);
        double b = y1 - k * x1;
        return k * x + b;
    }

    // Методы интерфейса TabulatedFunction
    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double newX = point.getX();

        if ((index > 0 && newX <= getNodeByIndex(index - 1).point.getX()) ||
                (index < pointsCount - 1 && newX >= getNodeByIndex(index + 1).point.getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        node.point = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        if ((index > 0 && x <= getNodeByIndex(index - 1).point.getX()) ||
                (index < pointsCount - 1 && x >= getNodeByIndex(index + 1).point.getX())) {
            throw new InappropriateFunctionPointException("Нарушение порядка точек по X");
        }

        node.point.setX(x);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int i = 0;
        FunctionNode current = head.next;
        while (current != head && point.getX() > current.point.getX()) {
            current = current.next;
            i++;
        }

        if (current != head && Math.abs(point.getX() - current.point.getX()) < 1e-10) {
            throw new InappropriateFunctionPointException("Точка с таким X уже существует");
        }

        addNodeByIndex(i, point);
    }

    // Переопределённые методы Object
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        FunctionNode current = head.next;
        while (current != head) {
            sb.append(current.point.toString());
            if (current.next != head) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;
        TabulatedFunction that = (TabulatedFunction) o;

        if (this.getPointsCount() != that.getPointsCount()) return false;

        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;
            FunctionNode thisNode = this.head.next;
            FunctionNode otherNode = other.head.next;

            while (thisNode != this.head) {
                if (!thisNode.point.equals(otherNode.point)) return false;
                thisNode = thisNode.next;
                otherNode = otherNode.next;
            }
        } else {
            for (int i = 0; i < pointsCount; i++) {
                if (!this.getPoint(i).equals(that.getPoint(i))) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        FunctionNode current = head.next;
        while (current != head) {
            result ^= current.point.hashCode();
            current = current.next;
        }
        result ^= pointsCount;
        return result;
    }

    @Override
    public Object clone() {
        LinkedListTabulatedFunction cloned = new LinkedListTabulatedFunction();
        cloned.head = new FunctionNode(null);
        cloned.head.next = cloned.head;
        cloned.head.prev = cloned.head;
        cloned.pointsCount = 0;
        cloned.lastAccessedNode = null;
        cloned.lastAccessedIndex = -1;

        FunctionNode current = this.head.next;
        FunctionNode lastNode = cloned.head;

        while (current != this.head) {
            FunctionNode newNode = new FunctionNode((FunctionPoint) current.point.clone());

            newNode.prev = lastNode;
            newNode.next = cloned.head;
            lastNode.next = newNode;
            cloned.head.prev = newNode;

            lastNode = newNode;
            cloned.pointsCount++;
            current = current.next;
        }

        return cloned;
    }

    private LinkedListTabulatedFunction() {
        // Приватный конструктор для клонирования
    }
}
