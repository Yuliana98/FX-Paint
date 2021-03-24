package model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement(name = "ellipse")
/**
 * Класс Ellipse - эллипс. Неявно наследует класс Dot, полями класса являются: радиусы по оси X и оси Y,
 * а также центральная точка с типом Dot.
 */
public class Ellipse extends Shape {

    private double rx; // радиус по оси X
    private double ry; // радиус по оси Y
    private Dot center; //центральная точка

    public Ellipse(double rx, double ry, Dot center) {
        this.rx = rx;
        this.ry = ry;
        this.center = center;
    }

    public Ellipse() {
    }

    public double getRx() {
        return rx;
    }

    public Ellipse setRx(double rx) {
        this.rx = rx;
        return this;
    }

    public double getRy() {
        return ry;
    }

    public Ellipse setRy(double ry) {
        this.ry = ry;
        return this;
    }

    public Dot getCenter() {
        return center;
    }

    public Ellipse setCenter(Dot center) {
        this.center = center;
        return this;
    }
}
