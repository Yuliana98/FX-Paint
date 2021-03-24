package model;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * Класс Line - линия. Неявно наследует класс Dot, полями класса являются две точки: начальная и конечная с типом Dot.
 */
@XmlRootElement(name = "line") // определение наназвания корневого элемента

public class Line extends Shape {

    private Dot dot1; // начальная точка
    private Dot dot2; // конечная точка

    public Line(Dot dot1, Dot dot2) {
        this.dot1 = dot1;
        this.dot2 = dot2;
    }

    public Line() {
    }

    public Dot getDot1() {
        return dot1;
    }

    public Line setDot1(Dot dot1) {
        this.dot1 = dot1;
        return this;
    }

    public Dot getDot2() {
        return dot2;
    }

    public Line setDot2(Dot dot2) {
        this.dot2 = dot2;
        return this;
    }
}
