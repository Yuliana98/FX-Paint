package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rectangle")
/**
 * Класс Rectangle - прямоугольник. Неявно наследует класс Dot, полями класса являются две точки: начальная и конечная с типом Dot.
 */
public class Rectangle extends Shape {

    private Dot dot1; // начальная точка
    private Dot dot2; // конечная точка

    public Rectangle(Dot dot1, Dot dot2) {
        this.dot1 = dot1;
        this.dot2 = dot2;
    }

    public Rectangle() {
    }

    public Dot getDot1() {
        return dot1;
    }

    public Rectangle setDot1(Dot dot1) {
        this.dot1 = dot1;
        return this;
    }

    public Dot getDot2() {
        return dot2;
    }

    public Rectangle setDot2(Dot dot2) {
        this.dot2 = dot2;
        return this;
    }
}
