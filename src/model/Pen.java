package model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "pen")
public class Pen extends Shape {

    private List<Dot> dotList = new LinkedList<>(); //массив из точек (координат линии)

    public Pen(List<Dot> dotList) {
        this.dotList = dotList;
    }

    public Pen() {
    }

    public List<Dot> getDotList() {
        return dotList;
    }

    public Pen setDotList(List<Dot> dotList) {
        this.dotList = dotList;
        return this;
    }

}
