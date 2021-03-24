package model;

import javafx.scene.paint.Paint;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Класс Shape является абстрактным. В абстрактном классе Shape также можно определить поля и методы, но объект
 * или экземпляр абстрактного класса создать нельзя, это просто описание/обобщение. Данный класс предоставляет
 * базовый функционал для классов с примитивами (классы-наследники), они и реализуют функционал.
 */
public abstract class Shape implements Comparable<Shape> {

    int w; // толщина линий (контура)
    String fillColor; // цвет заливки фигуры
    String strokeColor; // цвет контура фигуры
    transient javafx.scene.shape.Shape shapeFx; // transient нужен, чтобы GSON пропускал это поле при работе.
                    // Нет необходимости хранить ссылку на fx объект, потому что ссылки каждый раз генерируются заново.


    public Shape(int w, String fillColor, String strokeColor) {
        this.w = w;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
    }

    public Shape() {
    }

    public int getW() {
        return w;
    }

    public Shape setW(int w) {
        this.w = w;
        return this;
    }

    public String getFillColor() {
        return fillColor;
    }

    public Shape setFillColor(String fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public Shape setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
        return this;
    }

    @XmlTransient //чтобы не хранилось в xml файле
    public javafx.scene.shape.Shape getShapeFx() {
        return shapeFx;
    }


    public Shape setShapeFx(javafx.scene.shape.Shape shapeFx) {
        this.shapeFx = shapeFx;
        return this;
    }

    @Override
    public int compareTo(Shape o) {
        return -1; //
    }
}
