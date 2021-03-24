package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dot")
/**
 * Класс Dot - вспомогательный класс, его неявно наследуют классы с объектами. Необходим, для сокращения количества
 * полей в классах объектов, чтобы не расписывать отдельно координаты для каждой точки.
 */
public class Dot extends Shape {

    private double x; // координата точки по оси Х
    private double y; // координата точки по оси У

    public Dot(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Dot() {
    }

    public double getX() {
        return x;
    }

    public Dot setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public Dot setY(double y) {
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object obj) { //указываю, что для этого класса этот метод работает так
        Dot dot = (Dot) obj; //

        return this.x == dot.getX() && this.y == dot.getY();
    }
}



