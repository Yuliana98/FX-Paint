package model;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "polygon")
@XmlAccessorType(XmlAccessType.FIELD) // чтобы смотрела на поле
/**
 * Класс Polygon - многоугольник (замкнутая полилиния). Реализуется как замкнутый массив из линий.
 */
public class Polygon extends Shape {

    @XmlElementWrapper(name = "dots")
    @XmlElement(name = "dot")
    private List<Dot> lineList = new LinkedList<>(); //массив из линий

    public Polygon(List<Dot> lineList) {
        this.lineList = lineList;
    }

    public Polygon() {
    }

    public List<Dot> getLineList() {
        return lineList;
    }

    public Polygon setLineList(List<Dot> lineList) {
        this.lineList = lineList;
        return this;
    }
}
