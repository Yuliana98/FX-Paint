package util;

import javafx.scene.paint.Color;
import model.*;

public class ShapeUtil {

    private ShapeUtil() {
    }

    public static Color modelToFX (String rgb) {
        return Color.web(rgb);
    }

    public static javafx.scene.shape.Shape drawFromFile (Shape shape) {

        if (shape instanceof Line) {

            Line line = (Line) shape;
            javafx.scene.shape.Line lineFx = new javafx.scene.shape.Line(
                    line.getDot1().getX(), line.getDot1().getY(), line.getDot2().getX(), line.getDot2().getY());
            lineFx.setStroke(modelToFX(line.getStrokeColor()));
            lineFx.setStrokeWidth(line.getW());
            shape.setShapeFx(lineFx);

            return lineFx;
        }

        if (shape instanceof Rectangle) {

            Rectangle rectangle = (Rectangle) shape;
            javafx.scene.shape.Rectangle rectangleFx = new javafx.scene.shape.Rectangle(
                    Math.min(rectangle.getDot1().getX(), rectangle.getDot2().getX()),
                    Math.min(rectangle.getDot1().getY(), rectangle.getDot2().getY()),
                    Math.abs(rectangle.getDot2().getX() - rectangle.getDot1().getX()),
                    Math.abs(rectangle.getDot2().getY() - rectangle.getDot1().getY()));
            rectangleFx.setFill(modelToFX(rectangle.getFillColor()));
            rectangleFx.setStroke(modelToFX(rectangle.getStrokeColor()));
            rectangleFx.setStrokeWidth(rectangle.getW());
            shape.setShapeFx(rectangleFx);

            return rectangleFx;
        }

        if (shape instanceof Ellipse) {

            Ellipse ellipse = (Ellipse) shape;
            javafx.scene.shape.Ellipse ellipseFx = new javafx.scene.shape.Ellipse(
                    ellipse.getCenter().getX(), ellipse.getCenter().getY(), ellipse.getRx(), ellipse.getRy());
            ellipseFx.setFill(modelToFX(ellipse.getFillColor()));
            ellipseFx.setStroke(modelToFX(ellipse.getStrokeColor()));
            ellipseFx.setStrokeWidth(ellipse.getW());
            shape.setShapeFx(ellipseFx);

            return ellipseFx;
        }

        if (shape instanceof Polyline) {

            Polyline polyline = (Polyline) shape;
            javafx.scene.shape.Polyline polylineFx = new javafx.scene.shape.Polyline();
            polyline.getLineList().forEach(dot -> {
                polylineFx.getPoints().addAll(dot.getX(), dot.getY());
            });
            polylineFx.setStroke(modelToFX(polyline.getStrokeColor()));
            polylineFx.setStrokeWidth(polyline.getW());
            shape.setShapeFx(polylineFx);

            return polylineFx;
        }

        if (shape instanceof Polygon) {

            Polygon polygon = (Polygon) shape;
            javafx.scene.shape.Polygon polygonFx = new javafx.scene.shape.Polygon();
            polygon.getLineList().forEach(dot -> {
                polygonFx.getPoints().addAll(dot.getX(), dot.getY());
            });
            polygonFx.setStroke(modelToFX(polygon.getStrokeColor()));
            polygonFx.setFill(modelToFX(polygon.getFillColor()));
            polygonFx.setStrokeWidth(polygon.getW());
            shape.setShapeFx(polygonFx);

            return polygonFx;
        }

        return null;
    }

    public static void moveTo (Shape shape, double plusX, double plusY) {

        if (shape instanceof Rectangle) { // shape - экземляр класса Rectangle

            Rectangle rectangle = (Rectangle) shape; // преобразование shape в класс Rectangle
            rectangle.getDot1().setX(rectangle.getDot1().getX() + plusX).setY(rectangle.getDot1().getY() + plusY);
            rectangle.getDot2().setX(rectangle.getDot2().getX() + plusX).setY(rectangle.getDot2().getY() + plusY);
            // получение ссылки на уже созданный объект
            javafx.scene.shape.Rectangle rectangleShapeFx = (javafx.scene.shape.Rectangle) rectangle.getShapeFx();
            rectangleShapeFx.setX(rectangleShapeFx.getX() + plusX);
            rectangleShapeFx.setY(rectangleShapeFx.getY() + plusY);
        }

        if (shape instanceof Line) {

            Line line = (Line) shape;
            line.getDot1().setX(line.getDot1().getX() + plusX).setY(line.getDot1().getY() + plusY);
            line.getDot2().setX(line.getDot2().getX() + plusX).setY(line.getDot2().getY() + plusY);
            javafx.scene.shape.Line lineShapeFx = (javafx.scene.shape.Line) line.getShapeFx();
            lineShapeFx.setStartX(lineShapeFx.getStartX() + plusX);
            lineShapeFx.setStartY(lineShapeFx.getStartY() + plusY);
            lineShapeFx.setEndX(lineShapeFx.getEndX() + plusX);
            lineShapeFx.setEndY(lineShapeFx.getEndY() + plusY);
        }



        if (shape instanceof Ellipse) {

            Ellipse ellipse = (Ellipse) shape;
            ellipse.getCenter().setX(ellipse.getCenter().getX() + plusX).setY(ellipse.getCenter().getY() + plusY);
            javafx.scene.shape.Ellipse ellipseShapeFx = (javafx.scene.shape.Ellipse) ellipse.getShapeFx();
            ellipseShapeFx.setCenterX(ellipseShapeFx.getCenterX() + plusX);
            ellipseShapeFx.setCenterY(ellipseShapeFx.getCenterY() + plusY);
        }

        if (shape instanceof Polyline) {

            Polyline polyline = (Polyline) shape;
            polyline.getLineList().forEach(dot -> {
                dot.setX(dot.getX() + plusX).setY(dot.getY() + plusY);
            });
            javafx.scene.shape.Polyline polylineShapeFx = (javafx.scene.shape.Polyline) polyline.getShapeFx();
            polylineShapeFx.getPoints().clear();
            polyline.getLineList().forEach(dot -> {
                polylineShapeFx.getPoints().addAll(dot.getX(), dot.getY());
            });
        }

        if (shape instanceof Polygon) {

            Polygon polygon = (Polygon) shape;
            polygon.getLineList().forEach(dot -> {
                dot.setX(dot.getX() + plusX).setY(dot.getY() + plusY);
            });
            javafx.scene.shape.Polygon polygonShapeFx = (javafx.scene.shape.Polygon) polygon.getShapeFx();
            polygonShapeFx.getPoints().clear();
            polygon.getLineList().forEach(dot -> {
                polygonShapeFx.getPoints().addAll(dot.getX(), dot.getY());
            });
        }
    }
}
