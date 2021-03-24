package model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;
/**
 * Класс Pic - класс для изобрежения. На изображении хранятся все объекты.
 */
@XmlRootElement (name = "pic") // определение корневого элемента
@XmlType(propOrder = {"name", "width", "height", "shapeList"}) // определяем последовательность тегов в XML

public class Pic {

    private List<Shape> shapeList = new LinkedList<>(); // список (массив) из фигур, создается с помощью коллекции, чтобы
        // была возможность бесконечно записывать сюда объекты. Используется связный список (элементы связаны между собой).
    private String name; // имя изображения
    private int width; // ширина рисунка
    private int height; //высота рисунка

    public Pic(List<Shape> shapeList, String name, int width, int height) {
        this.shapeList = shapeList;
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public Pic() {
    }

    public List<Shape> getShapeList() {
        return shapeList;
    }

    public Pic setShapeList(List<Shape> shapeList) {
        this.shapeList = shapeList;
        return this;
    }

    public String getName() {
        return name;
    }

    public Pic setName(String name) {
        this.name = name;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Pic setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Pic setHeight(int height) {
        this.height = height;
        return this;
    }
}
