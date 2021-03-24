package fx;

import com.google.gson.Gson;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.*;
import util.FileUtil;
import util.ShapeUtil;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        double width = 99999;
        double height = 99999;
        StringBuilder eventButton = new StringBuilder();
        AtomicReference<Pic> picture = new AtomicReference<>(new Pic());
        AtomicReference<Shape> choosenShape = new AtomicReference<>();

        StringBuilder fillColor = new StringBuilder ("0xFFFFFF00");
        StringBuilder strokeColor = new StringBuilder("0x000000FF");
        int strokeWidth = 3;

        //Панели для кнопок, примитивов и рисования
        FlowPane buttonPane = new FlowPane();
        FlowPane shapePane = new FlowPane();
        Pane pane = new Pane();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(pane);
        scrollPane.setPannable(true);

        // Выставляем "предпочитаемые" границы, то есть стандартные при запуске программы
        pane.setPrefHeight(width);
        pane.setPrefWidth(height);
        buttonPane.setPrefWidth(width);
        buttonPane.setMinWidth(800);
        buttonPane.setMinHeight(43);
        shapePane.setPrefWidth(width);
        shapePane.setMinWidth(800);
        shapePane.setMinHeight(43);

        //Отрисовка рамки вокруг блоков, чтобы они визуально не сливались
        buttonPane.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        shapePane.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //Добавление фонового цвета на блоки с кнопками
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        buttonPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        shapePane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        pane.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        /**
         * Создание кнопок для объектов
         */

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml file", "*.xml"));

        //Список File
        ObservableList<String> fileParamsList = FXCollections.observableArrayList( "File", "Save", "Save as...", "Open file", "Clear");
        final ChoiceBox fileList = new ChoiceBox(fileParamsList);
        fileList.setMinSize(80,40);
        fileList.setMaxSize(80,40);
        fileList.setStyle("-fx-background-color: rgb(30,170,255);");
        fileList.setValue("File"); // устанавливаем выбранный элемент по умолчанию
        fileList.setOnAction(event -> {
            System.out.println(fileList.getValue());
            if (fileList.getValue().equals("File"))
                return;
            switch ((String)(fileList.getValue())) {
                case "Save as...":
                    try {
                        File file = fileChooser.showSaveDialog(primaryStage);
                        picture.get().setName(file.getAbsolutePath());
                        FileUtil.save(picture.get(), picture.get().getName());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "Save":
                    try {
                        FileUtil.save(picture.get(), picture.get().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "Open file":
                    try {
                        File file = fileChooser.showOpenDialog(primaryStage);
                        picture.set(FileUtil.loading(file.getAbsolutePath()));
                        pane.getChildren().clear();
                        picture.get().getShapeList().forEach(shape -> {
                            javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(shape);
                            shape.setShapeFx(shapeFx);
                            pane.getChildren().add(shapeFx);
                            shapeFx.setOnMouseClicked(shapeFxEvent -> {
                                choosenShape.set(shape);

                                FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                transition.setFromValue(0.5);
                                transition.setToValue(1);
                                transition.play();

                            });
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pane.toBack(); //белое полотно под кнопками, на нижнем слое
                    break;

                case "Clear":
                    pane.getChildren().clear();
                    break;
            }
            fileList.setValue(fileParamsList.get(0));
        });


        //Выбрать
        Button chooseButton = new Button("Choose");
        chooseButton.setMinSize(80,40);
        chooseButton.setMaxSize(80,40);
        chooseButton.setTooltip(new Tooltip("Object selection \n\n   Click on this button and then on the object you want to select."));
        chooseButton.setOnMouseClicked(event -> {
            eventButton.delete(0, eventButton.length());
            eventButton.append("choose");
        });

        //Удаление
        Button deleteButton = new Button("Delete");
        deleteButton.setMinSize(80,40);
        deleteButton.setMaxSize(80,40);
        deleteButton.setTooltip(new Tooltip("Delete selected item \n\n   Click on this button after you select an object."));
        deleteButton.setOnMouseClicked(event -> {
            if (choosenShape.get() != null) { // проверка на наличие ссылки на объект
                pane.getChildren().remove(choosenShape.get().getShapeFx()); // удаление JavaFX объекта
                picture.get().getShapeList().remove(choosenShape.get()); // удаление model объекта
                choosenShape.set(null); // очищение choosenShape
            }
        });

        //Выбор цвета границы в виде списка цветов RGB
        ColorPicker colorPickerStroke = new ColorPicker(ShapeUtil.modelToFX(strokeColor.toString()));
        colorPickerStroke.setMinSize(50,40);
        colorPickerStroke.setMaxSize(50,40);
        colorPickerStroke.setTooltip(new Tooltip("Stroke color \n\n   Click here and select a color in the color palette. \n " +
                "  This color will be used for the stroke to draw objects. \n" +
                "   Or select an object whose stroke color you want to change and choose a color."));
        colorPickerStroke.setValue(Color.BLACK);
        colorPickerStroke.setOnAction(new EventHandler() {
            public void handle(Event t) {
                strokeColor.delete(0, strokeColor.length()); // установка цвета
                strokeColor.append(colorPickerStroke.getValue().toString());

                // изменение цвета выбранного объекта
                if (choosenShape.get() != null) {
                    choosenShape.get().getShapeFx().setStroke(ShapeUtil.modelToFX(strokeColor.toString()));
                    choosenShape.get().setStrokeColor(strokeColor.toString());
                }
            }
        });

        //Выбор цвета заливки фигуры в виде списка цветов RGB
        ColorPicker colorPickerFill = new ColorPicker(ShapeUtil.modelToFX(fillColor.toString()));
        colorPickerFill.setMinSize(50,40);
        colorPickerFill.setMaxSize(50,40);
        colorPickerFill.setTooltip(new Tooltip("Fill color \n\n   Click here and select a color in the color palette. \n " +
                "  This color will be used for the fill to draw objects (rectangle, ellipse polygon). \n" +
                "   Or select an object whose fill color you want to change and choose a color."));
        colorPickerFill.setValue(Color.WHITE);
        colorPickerFill.setOnAction(new EventHandler() {
            public void handle(Event t) {
                fillColor.delete(0, fillColor.length());
                fillColor.append(colorPickerFill.getValue().toString());
                if (choosenShape.get() != null) {
                    choosenShape.get().getShapeFx().setFill(ShapeUtil.modelToFX(fillColor.toString()));
                    choosenShape.get().setFillColor(fillColor.toString());
                }
            }
        });

        //Кнопка "Line"
        Image imageLine = new Image(getClass().getResourceAsStream("res/lineButton.png")); // добавление картинки
        Button lineButton = new Button("", new ImageView(imageLine)); // создание кнопки с пустым названием и картинкой
        lineButton.setMinSize(40,40); // установка минимальных размеров
        lineButton.setMaxSize(80,40); //установка минимальных размеров
        lineButton.setTooltip(new Tooltip("Line")); // всплывающая подсказка
        lineButton.setOnMouseClicked(event -> { // событие по клику мыши на кнопку
            System.out.println("Выбрана кнопка: " + eventButton.toString());
            eventButton.delete(0, eventButton.length()); //очищение
            eventButton.append("line"); // запись "line" в строку
        });

        //Кнопка "Прямоугольник"
        Image imageRectangle = new Image(getClass().getResourceAsStream("res/rectangleButton.png"));
        Button rectangleButton = new Button("", new ImageView(imageRectangle));
        rectangleButton.setMinSize(40,40);
        rectangleButton.setMaxSize(80,40);
        rectangleButton.setTooltip(new Tooltip("Rectangle"));
        rectangleButton.setOnMouseClicked(event -> {
            eventButton.delete(0, eventButton.length());
            eventButton.append("rectangle");
            System.out.println("Выбрана кнопка: " + eventButton.toString());
        });

        //Кнопка "Эллипс"
        Image imageEllipse = new Image(getClass().getResourceAsStream("res/ellipseButton.png"));
        Button ellipseButton = new Button("", new ImageView(imageEllipse));
        ellipseButton.setMinSize(40,40);
        ellipseButton.setMaxSize(80,40);
        ellipseButton.setTooltip(new Tooltip("Ellipse"));
        ellipseButton.setOnMouseClicked(event -> {
            eventButton.delete(0, eventButton.length());
            eventButton.append("ellipse");
            System.out.println("Выбрана кнопка: " + eventButton.toString());
        });

        //Кнопка "текст"
        Image imageText = new Image(getClass().getResourceAsStream("res/textButton.png"));
        Button textButton = new Button("", new ImageView(imageText));
        textButton.setMinSize(40,40);
        textButton.setMaxSize(80,40);
        textButton.setTooltip(new Tooltip("Text \n\n   Insert text field into image."));
        textButton.setOnMouseClicked(event -> {
            eventButton.delete(0, eventButton.length());
            eventButton.append("text");
            System.out.println("Выбрана кнопка: " + eventButton.toString());
        });

        //Кнопка "Назад", чтобы убирать последний добавленный элемент
        Image imageCancel = new Image(getClass().getResourceAsStream("res/cancelButton.png"));
        Button cancelButton = new Button("", new ImageView(imageCancel));
        cancelButton.setMinSize(40,40);
        cancelButton.setMaxSize(50,40);
        cancelButton.setTooltip(new Tooltip("Cancel \n\n   Delete last item."));
        cancelButton.setOnMouseClicked(event -> {
            if (pane.getChildren().size() > 0) {
                pane.getChildren().remove(pane.getChildren().size() - 1);
                System.out.println("Отмена");
            }
        });


        TextField commandTextField = new TextField();

        commandTextField.setPromptText("Command line");
        commandTextField.setTooltip(new Tooltip("Command line \n\n   Enter a command for the selected object. \n " +
                "  To move the selected object, enter \" moveto: x;y \". \n" +
                "   To move the selected object, enter \" delete:\"."));

        commandTextField.setMinSize(350,40);
        commandTextField.setMaxSize(350,40);
        Button commandButton = new Button("ENTRY");
        commandButton.setTooltip(new Tooltip("Click to use the command"));
        commandButton.setMinSize(50,40);
        commandButton.setMaxSize(100,40);
        commandButton.setOnMouseClicked(event -> {
            String commandText = commandTextField.getText(); // получение команды из командной строки
            switch (commandText.substring(0, commandText.indexOf(":"))) {

                case "moveto":
                    try {
                        String[] paramsArray = commandText.substring(commandText.indexOf(":") + 1).trim().split(";");
                        ShapeUtil.moveTo(choosenShape.get(), Double.parseDouble(paramsArray[0]), Double.parseDouble(paramsArray[1]));
                    } catch (Exception e) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("ERROR");
                        errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                        errorAlert.showAndWait();
                        e.printStackTrace();
                    }
                    break;

                case "delete":
                    if (choosenShape.get() != null) {
                        try {
                            pane.getChildren().remove(choosenShape.get().getShapeFx());
                            picture.get().getShapeList().remove(choosenShape.get());
                            choosenShape.set(null);
                        } catch (Exception e) {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("ERROR");
                            errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                            errorAlert.showAndWait();
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        });


        //Список для рисования по координатам
        ObservableList<String> shapePaintList = FXCollections.observableArrayList("Line", "Rectangle", "Ellipse", "Triangle", "Polyline/Polygon", "Dot", "Command line for objects");
        final ComboBox paintList = new ComboBox(shapePaintList);
        paintList.setMinSize(100,40);
        paintList.setMaxSize(200,40);
        Button paramsButton = new Button("Input parameters");
        paramsButton.setMinSize(100,40);
        paramsButton.setMaxSize(200,40);
        paintList.setValue("Programming tools");
        paramsButton.setOnAction(event -> {
            switch ((String) (paintList.getValue())) {

                case "Command line for objects": {
                    Alert jsonAlert = new Alert(Alert.AlertType.CONFIRMATION); //диалоговое окно
                    jsonAlert.setTitle("Programming objects using the command Line ");
                    jsonAlert.setHeaderText(null);
                    GridPane jsonGridPane = new GridPane();
                    TextField jsonTextField = new TextField();
                    jsonGridPane.addRow(0, new Label("Enter the parameters in the format: Object name + Parameters"));
                    jsonGridPane.addRow(1, new Label("For example:"));
                    jsonGridPane.addRow(2, new Label("Line {\"dot1\":{\"x\":100,\"y\":100}, \"dot2\":{\"x\":200, \"y\":300}, \"w\":10, \"strokeColor\":\"#000000\"}"));
                    jsonGridPane.addRow(3, new Label("Rectangle {\"dot1\":{\"x\":\"100\",\"y\":\"150\"}, \"dot2\":{\"x\":\"200\",\"y\":\"250\"}, \"w\":\"5\", \"fillColor\":\"#123456\", \"strokeColor\":\"#abcdef\"}"));
                    jsonGridPane.addRow(4, jsonTextField);
                    jsonAlert.getDialogPane().setContent(jsonGridPane);
                    jsonAlert.showAndWait().ifPresent(action -> {
                        if (ButtonType.OK.equals(action)) {
                            try {
                                String json = jsonTextField.getText(); // получение текста из строки
                                Class aClass = Class.forName("model." + json.substring(0, json.indexOf(" "))); // берем название класса, определяем какого класса
                                                                                                                // должен быть объект
                                Gson gson = new Gson(); // получение объекта, который будет туда-сюда все перекидывать
                                Object shape = gson.fromJson(json.substring(json.indexOf("{")), aClass); // берет текстову строку json, перекидывает в определенный класс
                                                                                                        // и начинает с { брать параметры и он превращает это в конкретный объект
                                javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile((Shape) shape);
                                shapeFx.setOnMouseClicked(event1 -> {
                                    FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                    transition.setFromValue(0.5);
                                    transition.setToValue(1);
                                    transition.play();
                                    choosenShape.set((Shape) shape);
                                });
                                ((Shape) shape).setShapeFx(shapeFx);
                                pane.getChildren().add(shapeFx);
                                picture.get().getShapeList().add((Shape)shape);
                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("ERROR");
                                errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }

                        }
                    });
                }
                break;

                case "Line": {
                    Alert lineAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    lineAlert.setTitle("Drawing a line using coordinates and parameters");
                    lineAlert.setHeaderText(null);
                    GridPane lineGridPane = new GridPane();
                    TextField line1xTextField = new TextField();
                    TextField line1yTextField = new TextField();
                    TextField line2xTextField = new TextField();
                    TextField line2yTextField = new TextField();
                    TextField strokeColorTextField = new TextField();
                    strokeColorTextField.setText("#000000");
                    TextField lineWeightTextField = new TextField();
                    lineWeightTextField.setText("3");
                    lineGridPane.addRow(0, new Label("Enter the parameters:\n\n-coordinates\n-border color\n-fill color and border line width."));
                    lineGridPane.addRow(1, new Label("\nСolor examples:\n\n#000000 - BLACK\n#FFFFFF - WHITE\n#FF0000 - RED\n#008000 - GREEN\n#0000FF - BLUE"));
                    lineGridPane.addRow(2, new Label(""));
                    lineGridPane.addRow(3, new Label( "First dot: x"), new Label("First dot: y"));
                    lineGridPane.addRow(4, line1xTextField, line1yTextField);
                    lineGridPane.addRow(5, new Label("Second dot: x"), new Label("Second dot: y"));
                    lineGridPane.addRow(6, line2xTextField, line2yTextField);
                    lineGridPane.addRow(7, new Label("Stroke Color (RGB HEX)"), new Label("Border line width (px)"));
                    lineGridPane.addRow(8, strokeColorTextField, lineWeightTextField);
                    lineAlert.getDialogPane().setContent(lineGridPane);
                    lineAlert.showAndWait().ifPresent(action -> {
                        if (ButtonType.OK.equals(action)) {
                            try {
                                Line line = new Line();
                                line.setW(Integer.parseInt(lineWeightTextField.getText()));
                                line.setStrokeColor(strokeColorTextField.getText());
                                line.setDot1(new Dot(Double.parseDouble(line1xTextField.getText()), Double.parseDouble(line1yTextField.getText()))); // задаются и сохраняются модельные данные
                                line.setDot2(new Dot(Double.parseDouble(line2xTextField.getText()), Double.parseDouble(line2yTextField.getText()))); // необходим для хранения значений, так как нельзя просто взять и выгрузить фх
                                javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(line); // по модельным точкам создается объект FX, когда он кладется на pane он отображется
                                shapeFx.setOnMouseClicked(event1 -> {
                                    FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                    transition.setFromValue(0.5);
                                    transition.setToValue(1);
                                    transition.play();
                                    choosenShape.set(line);
                                });
                                line.setShapeFx(shapeFx);
                                pane.getChildren().add(shapeFx); // объект FX кладется на Pane
                                picture.get().getShapeList().add(line);
                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("ERROR");
                                errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;

                case "Rectangle": {
                    Alert rectangleAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    rectangleAlert.setTitle("Drawing a rectangle using coordinates and parameters");
                    rectangleAlert.setHeaderText(null);
                    GridPane rectangleGridPane = new GridPane();
                    TextField rectangle1xTextField = new TextField();
                    TextField rectangle1yTextField = new TextField();
                    TextField rectangle2xTextField = new TextField();
                    TextField rectangle2yTextField = new TextField();
                    TextField strokeColorTextField = new TextField();
                    TextField fillColorTextField = new TextField();
                    strokeColorTextField.setText("#000000");
                    fillColorTextField.setText("#FFFFFF");
                    TextField lineWeightTextField = new TextField();
                    lineWeightTextField.setText("3");
                    rectangleGridPane.addRow(0, new Label("Enter the parameters:\n\n-coordinates\n-border color\n-fill color and border line width."));
                    rectangleGridPane.addRow(1, new Label("\nСolor examples:\n\n#000000 - BLACK\n#FFFFFF - WHITE\n#FF0000 - RED\n#008000 - GREEN\n#0000FF - BLUE"));
                    rectangleGridPane.addRow(2, new Label(""));
                    rectangleGridPane.addRow(3, new Label("First dot: x"), new Label("First dot: y"));
                    rectangleGridPane.addRow(4, rectangle1xTextField, rectangle1yTextField);
                    rectangleGridPane.addRow(5, new Label("Second dot: x"), new Label("Second dot: y"));
                    rectangleGridPane.addRow(6, rectangle2xTextField, rectangle2yTextField);
                    rectangleGridPane.addRow(7, new Label("Stroke Color (RGB HEX)"), new Label("Fill Color (RGB HEX)"));
                    rectangleGridPane.addRow(8, strokeColorTextField, fillColorTextField);
                    rectangleGridPane.addRow(9, new Label("Border line width (px)"));
                    rectangleGridPane.addRow(10, lineWeightTextField);
                    rectangleAlert.getDialogPane().setContent(rectangleGridPane);
                    rectangleAlert.showAndWait().ifPresent(action -> {
                        if (ButtonType.OK.equals(action)) {
                            try {
                                Rectangle rectangle = new Rectangle();
                                rectangle.setW(Integer.parseInt(lineWeightTextField.getText()));
                                rectangle.setStrokeColor(strokeColorTextField.getText());
                                rectangle.setFillColor(fillColorTextField.getText());
                                rectangle.setDot1(new Dot(Double.parseDouble(rectangle1xTextField.getText()), Double.parseDouble(rectangle1yTextField.getText())));
                                rectangle.setDot2(new Dot(Double.parseDouble(rectangle2xTextField.getText()), Double.parseDouble(rectangle2yTextField.getText())));
                                javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(rectangle);
                                shapeFx.setOnMouseClicked(event1 -> {
                                    FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                    transition.setFromValue(0.5);
                                    transition.setToValue(1);
                                    transition.play();
                                    choosenShape.set(rectangle);
                                });
                                rectangle.setShapeFx(shapeFx);
                                pane.getChildren().add(shapeFx);
                                picture.get().getShapeList().add(rectangle);
                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("ERROR");
                                errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;

                case "Ellipse": {
                    Alert ellipseAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    ellipseAlert.setTitle("Drawing an ellipse using coordinates and parameters");
                    ellipseAlert.setHeaderText(null);
                    GridPane ellipseGridPane = new GridPane();
                    TextField ellipse1xTextField = new TextField();
                    TextField ellipse1yTextField = new TextField();
                    TextField ellipseRxTextField = new TextField();
                    TextField ellipseRyTextField = new TextField();
                    TextField strokeColorTextField = new TextField();
                    TextField fillColorTextField = new TextField();
                    strokeColorTextField.setText("#000000");
                    fillColorTextField.setText("#FFFFFF");
                    TextField lineWeightTextField = new TextField();
                    lineWeightTextField.setText("3");
                    ellipseGridPane.addRow(0, new Label("Enter the parameters:\n\n-coordinates of center dot\n-radius X and Y border color\n-fill color and border line width."));
                    ellipseGridPane.addRow(1, new Label("\nСolor examples:\n\n#000000 - BLACK\n#FFFFFF - WHITE\n#FF0000 - RED\n#008000 - GREEN\n#0000FF - BLUE"));
                    ellipseGridPane.addRow(2, new Label(""));
                    ellipseGridPane.addRow(3, new Label("Center: x"), new Label("Center: y"));
                    ellipseGridPane.addRow(4, ellipse1xTextField, ellipse1yTextField);
                    ellipseGridPane.addRow(5, new Label("X axis radius"), new Label("Y axis radius"));
                    ellipseGridPane.addRow(6, ellipseRxTextField, ellipseRyTextField);
                    ellipseGridPane.addRow(7, new Label("Stroke Color (RGB HEX)"), new Label("Fill Color (RGB HEX)"));
                    ellipseGridPane.addRow(8, strokeColorTextField, fillColorTextField);
                    ellipseGridPane.addRow(9, new Label("Border line width (px)"));
                    ellipseGridPane.addRow(10, lineWeightTextField);
//                    ellipseGridPane.addRow(9, new Label("Border line width (px)"));
//                    ellipseGridPane.addRow(10, lineWeightTextField);
                    ellipseAlert.getDialogPane().setContent(ellipseGridPane);
                    ellipseAlert.showAndWait().ifPresent(action -> {
                        if (ButtonType.OK.equals(action)) {
                            try {
                                Ellipse ellipse = new Ellipse();
                                ellipse.setW(Integer.parseInt(lineWeightTextField.getText()));
                                ellipse.setStrokeColor(strokeColorTextField.getText());
                                ellipse.setFillColor(fillColorTextField.getText());
                                ellipse.setCenter(new Dot(Double.parseDouble(ellipse1xTextField.getText()), Double.parseDouble(ellipse1xTextField.getText())));
                                ellipse.setRx(Double.parseDouble(ellipseRxTextField.getText()));
                                ellipse.setRy(Double.parseDouble(ellipseRyTextField.getText()));
                                javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(ellipse);
                                shapeFx.setOnMouseClicked(event1 -> {
                                    FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                    transition.setFromValue(0.5);
                                    transition.setToValue(1);
                                    transition.play();
                                    choosenShape.set(ellipse);
                                });
                                ellipse.setShapeFx(shapeFx);
                                pane.getChildren().add(shapeFx);
                                picture.get().getShapeList().add(ellipse);
                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("ERROR");
                                errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;

                case "Dot": {
                    Alert ellipseAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    ellipseAlert.setTitle("Drawing a dot using coordinates and parameters");
                    ellipseAlert.setHeaderText(null);
                    GridPane ellipseGridPane = new GridPane();
                    TextField ellipse1xTextField = new TextField();
                    TextField ellipse1yTextField = new TextField();
                    TextField lineWeightTextField = new TextField();
                    lineWeightTextField.setText("3");
                    TextField fillColorTextField = new TextField();
                    fillColorTextField.setText("#000000");
                    ellipseGridPane.addRow(0, new Label("Enter the parameters:\n\n-coordinates\n-fill color and border line width."));
                    ellipseGridPane.addRow(1, new Label("\nСolor examples:\n\n#000000 - BLACK\n#FFFFFF - WHITE\n#FF0000 - RED\n#008000 - GREEN\n#0000FF - BLUE"));
                    ellipseGridPane.addRow(2, new Label(""));
                    ellipseGridPane.addRow(3, new Label("X coordinate:"), new Label("Y coordinate:"));
                    ellipseGridPane.addRow(4, ellipse1xTextField, ellipse1yTextField);
                    ellipseGridPane.addRow(5, new Label("Fill Color (RGB HEX)"), new Label("Width (px)"));
                    ellipseGridPane.addRow(6, fillColorTextField, lineWeightTextField);
                    ellipseAlert.getDialogPane().setContent(ellipseGridPane);
                    ellipseAlert.showAndWait().ifPresent(action -> {
                        if (ButtonType.OK.equals(action)) {
                            try {
                                Ellipse ellipse = new Ellipse();
                                ellipse.setW(0);
                                ellipse.setStrokeColor("#FFFFFFFF");
                                ellipse.setFillColor(fillColorTextField.getText());
                                ellipse.setCenter(new Dot(Double.parseDouble(ellipse1xTextField.getText()), Double.parseDouble(ellipse1xTextField.getText())));
                                ellipse.setRx(Double.parseDouble(lineWeightTextField.getText()));
                                ellipse.setRy(Double.parseDouble(lineWeightTextField.getText()));
                                javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(ellipse);
                                shapeFx.setOnMouseClicked(event1 -> {
                                    FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                    transition.setFromValue(0.5);
                                    transition.setToValue(1);
                                    transition.play();
                                    choosenShape.set(ellipse);
                                });
                                ellipse.setShapeFx(shapeFx);
                                pane.getChildren().add(shapeFx);
                                picture.get().getShapeList().add(ellipse);
                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("ERROR");
                                errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;

                case "Polyline/Polygon": {
                    Alert polylineAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    polylineAlert.setTitle("Drawing a polyline/polygon using coordinates and parameters");
                    polylineAlert.setHeaderText(null);
                    GridPane polylineGridPane = new GridPane();
                    TableView<Dot> polylineTable = new TableView<>();
                    polylineTable.setEditable(true);
                    TableColumn tableColumnDot = new TableColumn("Dots");
                    //TableColumn tableColumnDotNum = new TableColumn("Dot Num");
                    polylineTable.getColumns().addAll(tableColumnDot);
                    TableColumn tableColumnX = new TableColumn("x");
                    TableColumn tableColumnY = new TableColumn("y");
                    tableColumnX.setCellValueFactory(
                            new PropertyValueFactory<Dot, String>("x"));
                    tableColumnY.setCellValueFactory(
                            new PropertyValueFactory<Dot, String>("y"));
                    tableColumnDot.getColumns().addAll(tableColumnX, tableColumnY);
                    TextField dotX = new TextField();
                    TextField dotY = new TextField();
                    TextField strokeColorTextField = new TextField();
                    strokeColorTextField.setText("#000000");
                    TextField fillColorTextField = new TextField();
                    fillColorTextField.setText("#FFFFFF");
                    //fillColorTextField.setVisible(false);
                    TextField lineWeightTextField = new TextField();
                    lineWeightTextField.setText("3");

                    final ObservableList<Dot> data =
                            FXCollections.observableArrayList();

                    polylineTable.setItems(data);

                    Button addButton = new Button("add");
                    addButton.setOnMouseClicked(event1 -> {
                        data.add(new Dot().setX(Double.parseDouble(dotX.getText())).setY(Double.parseDouble(dotY.getText())));
                        dotX.clear();
                        dotY.clear();
                    });

                    CheckBox checkBox = new CheckBox("Close polyline?");

                    /*checkBox.setOnAction(event1 -> {
                        if (checkBox.isSelected()){
                            fillColorTextField.setVisible(true);
                        } else {
                            fillColorTextField.setVisible(false);
                        }
                    });*/

                    polylineGridPane.addRow(0, polylineTable);
                    polylineGridPane.addRow(1, new Label("X coordinate:"), new Label("Y coordinate:"));
                    polylineGridPane.addRow(2, dotX, dotY, addButton);
                    polylineGridPane.addRow(3, new Label("Stroke Color (RGB HEX)"), new Label("Border line width (px)"));
                    polylineGridPane.addRow(4, strokeColorTextField, lineWeightTextField);
                    polylineGridPane.addRow(5, new Label("Fill Color (RGB HEX)"));
                    polylineGridPane.addRow(6, fillColorTextField, checkBox);
                    polylineGridPane.addRow(7, new Label(""));
                    polylineGridPane.addRow(8, new Label("      Enter the parameters:\n\n      -coordinates\n      -border color\n      -fill color and border line width.", new Label("Сolor examples:\n\n#000000 - BLACK\n#FFFFFF - WHITE\n#FF0000 - RED\n#008000 - GREEN\n#0000FF - BLUE")));

                    polylineAlert.getDialogPane().setContent(polylineGridPane);

                    polylineAlert.showAndWait().ifPresent(action -> {
                        if (ButtonType.OK.equals(action)) {
                            try {

                                Dot dot0 = data.get(0);
                                Dot dotLast = data.get(data.size() - 1);
                                boolean isClosed = dot0.equals(dotLast); // этот метод переписан в Dot

                                if (checkBox.isSelected() || isClosed) {

                                    Polygon polygon = new Polygon();
                                    polygon.setW(Integer.parseInt(lineWeightTextField.getText()));
                                    polygon.setStrokeColor(strokeColorTextField.getText());
                                    polygon.setFillColor(fillColorTextField.getText());
                                    polygon.getLineList().addAll(data);
                                    javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(polygon);
                                    shapeFx.setOnMouseClicked(event1 -> {
                                        FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                        transition.setFromValue(0.5);
                                        transition.setToValue(1);
                                        transition.play();
                                        choosenShape.set(polygon);
                                    });
                                    polygon.setShapeFx(shapeFx);
                                    pane.getChildren().add(shapeFx);
                                    picture.get().getShapeList().add(polygon);

                                } else {

                                    Polyline polyline = new Polyline();
                                    polyline.setW(Integer.parseInt(lineWeightTextField.getText()));
                                    polyline.setStrokeColor(strokeColorTextField.getText());
                                    polyline.getLineList().addAll(data);
                                    javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(polyline);
                                    shapeFx.setOnMouseClicked(event1 -> {
                                        FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                        transition.setFromValue(0.5);
                                        transition.setToValue(1);
                                        transition.play();
                                        choosenShape.set(polyline);
                                    });
                                    polyline.setShapeFx(shapeFx);
                                    pane.getChildren().add(shapeFx);
                                    picture.get().getShapeList().add(polyline);
                                }

                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("ERROR");
                                errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;

                case "Triangle": {

                    Alert triangleAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    triangleAlert.setTitle("Drawing a triangle using coordinates and parameters");
                    triangleAlert.setHeaderText(null);
                    GridPane triangleGridPane = new GridPane();
                    TextField triangle1xTextField = new TextField();
                    TextField triangle1yTextField = new TextField();
                    TextField triangle2xTextField = new TextField();
                    TextField triangle2yTextField = new TextField();
                    TextField triangle3xTextField = new TextField();
                    TextField triangle3yTextField = new TextField();
                    TextField strokeColorTextField = new TextField();
                    strokeColorTextField.setText("#000000");
                    TextField fillColorTextField = new TextField();
                    fillColorTextField.setText("#FFFFFF");
                    TextField lineWeightTextField = new TextField();
                    lineWeightTextField.setText("3");
                    triangleGridPane.addRow(0, new Label("Enter the parameters:\n\n-coordinates\n-border color\n-fill color and border line width."));
                    triangleGridPane.addRow(1, new Label("\nСolor examples:\n\n#000000 - BLACK\n#FFFFFF - WHITE\n#FF0000 - RED\n#008000 - GREEN\n#0000FF - BLUE"));
                    triangleGridPane.addRow(2, new Label(""));
                    triangleGridPane.addRow(3, new Label("First dot: x"), new Label("First dot: y"));
                    triangleGridPane.addRow(4, triangle1xTextField, triangle1yTextField);
                    triangleGridPane.addRow(5, new Label("Second dot: x"), new Label("Second dot: y"));
                    triangleGridPane.addRow(6, triangle2xTextField, triangle2yTextField);
                    triangleGridPane.addRow(7, new Label("Third dot: x"), new Label("Third dot: y"));
                    triangleGridPane.addRow(8, triangle3xTextField, triangle3yTextField);
                    triangleGridPane.addRow(9, new Label("Stroke Color (RGB HEX)"), new Label("Fill Color (RGB HEX)"), new Label("Border line width (px)"));
                    triangleGridPane.addRow(10, strokeColorTextField, fillColorTextField, lineWeightTextField);
                    triangleAlert.getDialogPane().setContent(triangleGridPane);

                    triangleAlert.showAndWait().ifPresent(action -> {
                        if (ButtonType.OK.equals(action)) {
                            try {

                                Polygon polygon = new Polygon();
                                polygon.setW(Integer.parseInt(lineWeightTextField.getText()));
                                polygon.setStrokeColor(strokeColorTextField.getText());
                                polygon.setFillColor(fillColorTextField.getText());
                                polygon.getLineList().add(new Dot().setX(Double.parseDouble(triangle1xTextField.getText())).setY(Double.parseDouble(triangle1yTextField.getText())));
                                polygon.getLineList().add(new Dot().setX(Double.parseDouble(triangle2xTextField.getText())).setY(Double.parseDouble(triangle2yTextField.getText())));
                                polygon.getLineList().add(new Dot().setX(Double.parseDouble(triangle3xTextField.getText())).setY(Double.parseDouble(triangle3yTextField.getText())));
                                javafx.scene.shape.Shape shapeFx = ShapeUtil.drawFromFile(polygon);
                                shapeFx.setOnMouseClicked(event1 -> {
                                    FadeTransition transition = new FadeTransition(new Duration(500), shapeFx);
                                    transition.setFromValue(0.5);
                                    transition.setToValue(1);
                                    transition.play();
                                    choosenShape.set(polygon);
                                });
                                polygon.setShapeFx(shapeFx);
                                pane.getChildren().add(shapeFx);
                                picture.get().getShapeList().add(polygon);

                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("ERROR");
                                errorAlert.setHeaderText("Incorrect entry! Re-enter following the example.");
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            pane.toBack();
        });


        Label lastStep = new Label("");

        buttonPane.getChildren().addAll(fileList, cancelButton, chooseButton, deleteButton,
                new Label(" Painting "), lineButton, rectangleButton, ellipseButton, textButton,
                new Label(" Stroke color "), colorPickerStroke, new Label(" Fill color "), colorPickerFill);
        shapePane.getChildren().addAll(new Label(" Programming "), paintList, paramsButton, commandTextField, commandButton);
        //labelPane.getChildren().addAll();

        /**
         * События отрисовки
         * События вешаются не на canvas, а на рабочую область, то есть на объект Pane
         */

        /**
         * События нажатия кнопки мыши
         * Используется переключатель switch по выбранному кейсу. Выбирается по содержимому eventButton
         */

        pane.setOnMousePressed(event -> {
            switch (eventButton.toString()) {

                case "text":
                    Text text = new Text();
                    picture.get().getShapeList().add(text);
                    text.setDotText(new Dot().setX(event.getX()).setY(event.getY()));
                    javafx.scene.text.Text textFx = new javafx.scene.text.Text(event.getX(), event.getY(), "");
                    textFx.setFont(new Font(20));
                    textFx.setWrappingWidth(200);
                    textFx.setTextAlignment(TextAlignment.JUSTIFY);
                    textFx.setText("To change the text, press the button Choose, then double-click in the area.");
                    pane.getChildren().add(textFx);
                    text.setShapeFx(textFx);

                    textFx.setOnMouseClicked(textEvent -> {
                        if (textEvent.getClickCount() == 2) {
                            Alert textAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            textAlert.setTitle("Text");
                            textAlert.setHeaderText(null);
                            GridPane textGridPane = new GridPane();
                            TextArea textArea = new TextArea(textFx.getText());

                            ObservableList<Double> sizeList = FXCollections.observableArrayList(8., 9., 10., 11., 12., 14., 16., 18., 20.);
                            final ComboBox sizeBox = new ComboBox(sizeList);
                            sizeBox.setConverter(new StringConverter<Double>() {
                                @Override
                                public String toString(Double object) {
                                    return object.toString().substring(0,object.toString().indexOf("."));
                                }

                                @Override
                                public Double fromString(String string) {
                                    return Double.parseDouble(string);
                                }
                            });

//                            ObservableList<TextAlignment> alignmentList = FXCollections.observableArrayList(TextAlignment.values());
//                            final ComboBox alignmentBox = new ComboBox(alignmentList);
//                            alignmentBox.setConverter(new StringConverter<TextAlignment>() {
//                                @Override
//                                public String toString(TextAlignment object) {
//                                    switch (object) {
//
//                                        case LEFT:
//
//                                            return "Выровнять по левому краю";
//                                        case CENTER:
//
//                                            return "Выровнять по центру";
//                                        case RIGHT:
//
//                                            return "Выровнять по правому краю";
//                                        case JUSTIFY:
//
//                                            return "Выровнять по ширине";
//                                    }
//                                    return "";
//                                }
//
//                                @Override
//                                public TextAlignment fromString(String string) {
//                                    switch (string) {
//                                        case "Выровнять по левому краю":
//                                            return TextAlignment.LEFT;
//
//                                        case "Выровнять по центру":
//                                            return TextAlignment.CENTER;
//
//                                        case "Выровнять по правому краю":
//                                            return TextAlignment.RIGHT;
//
//                                        case "Выровнять по ширине":
//                                            return TextAlignment.JUSTIFY;
//
//                                    }
//                                    return null;
//                                }
//                            });


                            textGridPane.addRow(0, new Label("Введите текст"));
                            textGridPane.addRow(1, textArea);
                            textGridPane.addRow(2, sizeBox);
                            textAlert.getDialogPane().setContent(textGridPane);

                            textAlert.showAndWait().ifPresent(textResult -> {
                                if (textResult == ButtonType.OK) {
                                    textFx.setText(textArea.getText());
                                    textFx.setFont(new Font((Double) sizeBox.getValue()));
                                }
                            });
                        } else {
                            FadeTransition transition = new FadeTransition(new Duration(500), textFx);
                            transition.setFromValue(0.5);
                            transition.setToValue(1);
                            transition.play();
                            choosenShape.set(text);
                        }
                    });
                    break;

                case "dot":
                    Dot dot = new Dot();
                    picture.get().getShapeList().add(dot);
                    dot.setX(event.getX());
                    dot.setW(strokeWidth);
                    dot.setStrokeColor(strokeColor.toString());
                    System.out.println(event.getX() + " " + event.getY());
                    break;


                case "line":
                    Line line = new Line();
                    picture.get().getShapeList().add(line);
                    line.setDot1(new Dot().setX(event.getX()).setY(event.getY()));
                    //Сохранение в фигуру цветов, которыми она должна быть нарисована
                    line.setW(strokeWidth);
                    line.setStrokeColor(strokeColor.toString());
                    System.out.println(event.getX() + " " + event.getY());
                    break;

                case "rectangle":
                    Rectangle rectangle = new Rectangle();
                    picture.get().getShapeList().add(rectangle);
                    rectangle.setDot1(new Dot().setX(event.getX()).setY(event.getY()));
                    rectangle.setW(strokeWidth);
                    rectangle.setStrokeColor(strokeColor.toString());
                    rectangle.setFillColor(fillColor.toString());
                    System.out.println(event.getX() + " " + event.getY());
                    break;

                case "ellipse":
                    Ellipse ellipse = new Ellipse();
                    picture.get().getShapeList().add(ellipse);
                    ellipse.setCenter(new Dot().setX(event.getX()).setY(event.getY()));
                    ellipse.setW(strokeWidth);
                    ellipse.setStrokeColor(strokeColor.toString());
                    ellipse.setFillColor(fillColor.toString());
                    System.out.println(event.getX() + " " + event.getY());
                    break;
            }
            pane.toBack();
        });

        /**
         * События нажатой кнопки мыши
         * Также используется переключатель switch по выбранному кейсу. Выбирается по содержимому eventButton
         */

        pane.setOnMouseDragged (event -> {

            if (!event.getButton().equals(MouseButton.SECONDARY)) {
                event.consume();
            } else {
                return;
            }

            switch (eventButton.toString()){

                case "line":
                    Line line = (Line) picture.get().getShapeList().get(picture.get().getShapeList().size() - 1);
                    if (line.getDot2() == null) {
                        line.setDot2(new Dot().setX(event.getX()).setY(event.getY()));
                        javafx.scene.shape.Line lineFx = new javafx.scene.shape.Line(
                                line.getDot1().getX(), line.getDot1().getY(), line.getDot2().getX(), line.getDot2().getY());
                        lineFx.setOnMouseClicked(event1 -> {
                            FadeTransition transition = new FadeTransition(new Duration(500), lineFx);
                            transition.setFromValue(0.5);
                            transition.setToValue(1);
                            transition.play();
                            choosenShape.set(line);
                        });
                        line.setShapeFx(lineFx);
                        lineFx.setStroke(ShapeUtil.modelToFX(line.getStrokeColor()));
                        lineFx.setStrokeWidth(line.getW());
                        pane.getChildren().add(lineFx);
                    }

                    else {
                        line.setDot2(new Dot().setX(event.getX()).setY(event.getY()));
                        javafx.scene.shape.Line lineFx = (javafx.scene.shape.Line)
                                pane.getChildren().get(pane.getChildren().size() - 1);
                        lineFx.setEndX(event.getX());
                        lineFx.setEndY(event.getY());

                    }
                    break;

                /*case "rectangle":
                    Rectangle rectangle = (Rectangle) picture.get().getShapeList().get(picture.get().getShapeList().size() - 1);
                    if (rectangle.getDot2() == null) {
                        rectangle.setDot2(new Dot().setX(event.getX()).setY(event.getY()));
                        javafx.scene.shape.Rectangle rectangleFx = new javafx.scene.shape.Rectangle(
                                Math.min(rectangle.getDot1().getX(), rectangle.getDot2().getX()),
                                Math.min(rectangle.getDot1().getY(), rectangle.getDot2().getY()),
                                Math.abs(rectangle.getDot2().getX() - rectangle.getDot1().getX()),
                                Math.abs(rectangle.getDot2().getY() - rectangle.getDot1().getY()));
                        rectangleFx.setOnMouseClicked(event1 -> {
                            FadeTransition transition = new FadeTransition(new Duration(500), rectangleFx);
                            transition.setFromValue(0.5);
                            transition.setToValue(1);
                            transition.play();
                            choosenShape.set(rectangle);
                        });
                        rectangle.setShapeFx(rectangleFx);
                        rectangleFx.setFill(FileUtil.modelToFX(rectangle.getFillColor()));
                        rectangleFx.setStroke(FileUtil.modelToFX(rectangle.getStrokeColor()));
                        rectangleFx.setStrokeWidth(rectangle.getW());
                        pane.getChildren().add(rectangleFx);
                    }

                    else {
                        rectangle.setDot2(new Dot().setX(event.getX()).setY(event.getY()));
                        javafx.scene.shape.Rectangle rectangleFx = (javafx.scene.shape.Rectangle)
                                pane.getChildren().get(pane.getChildren().size() - 1);
                        rectangleFx.setWidth(Math.abs(rectangle.getDot2().getX() - rectangle.getDot1().getX()));
                        rectangleFx.setHeight(Math.abs(rectangle.getDot2().getY() - rectangle.getDot1().getY()));
                    }
                    break;*/

                case "rectangle":
                    Rectangle rectangle = (Rectangle) picture.get().getShapeList().get(picture.get().getShapeList().size() - 1);
                    if (rectangle.getDot2() == null) {
                        rectangle.setDot2(new Dot().setX(event.getX()).setY(event.getY()));

                        javafx.scene.shape.Rectangle rectangleFx = new javafx.scene.shape.Rectangle(
                                Math.min(rectangle.getDot1().getX(), rectangle.getDot2().getX()),
                                Math.min(rectangle.getDot1().getY(), rectangle.getDot2().getY()),
                                Math.abs(rectangle.getDot2().getX() - rectangle.getDot1().getX()),
                                Math.abs(rectangle.getDot2().getY() - rectangle.getDot1().getY()));
                        rectangleFx.setOnMouseClicked(event1 -> {
                            FadeTransition transition = new FadeTransition(new Duration(500), rectangleFx);
                            transition.setFromValue(0.5);
                            transition.setToValue(1);
                            transition.play();
                            choosenShape.set(rectangle);
                        });
                        rectangle.setShapeFx(rectangleFx);
                        rectangleFx.setFill(ShapeUtil.modelToFX(rectangle.getFillColor()));
                        rectangleFx.setStroke(ShapeUtil.modelToFX(rectangle.getStrokeColor()));
                        rectangleFx.setStrokeWidth(rectangle.getW());
                        pane.getChildren().add(rectangleFx);
                    }

                    else {
                        rectangle.setDot2(new Dot().setX(event.getX()).setY(event.getY()));

                        javafx.scene.shape.Rectangle rectangleFx = (javafx.scene.shape.Rectangle)
                                pane.getChildren().get(pane.getChildren().size() - 1);
                        rectangleFx.setX(Math.min(rectangle.getDot1().getX(), rectangle.getDot2().getX()));
                        rectangleFx.setY(Math.min(rectangle.getDot1().getY(), rectangle.getDot2().getY()));
                        rectangleFx.setWidth(Math.abs(rectangle.getDot2().getX() - rectangle.getDot1().getX()));
                        rectangleFx.setHeight(Math.abs(rectangle.getDot2().getY() - rectangle.getDot1().getY()));
                    }
                    break;

                case "ellipse":
                    Ellipse ellipse = (Ellipse) picture.get().getShapeList().get(picture.get().getShapeList().size() - 1);
                    if ((ellipse.getRx() < 1) && (ellipse.getRy() < 1)) {
                        ellipse.setRx(Math.abs(event.getX() - ellipse.getCenter().getX()));
                        ellipse.setRy(Math.abs(event.getY() - ellipse.getCenter().getY()));
                        javafx.scene.shape.Ellipse ellipseFx = new javafx.scene.shape.Ellipse(
                                ellipse.getCenter().getX(), ellipse.getCenter().getY(), ellipse.getRx(), ellipse.getRy());
                        ellipseFx.setOnMouseClicked(event1 -> {
                            FadeTransition transition = new FadeTransition(new Duration(500), ellipseFx);
                            transition.setFromValue(0.5);
                            transition.setToValue(1);
                            transition.play();
                            choosenShape.set(ellipse);
                        });
                        ellipse.setShapeFx(ellipseFx);
                        ellipseFx.setFill(ShapeUtil.modelToFX(ellipse.getFillColor()));
                        ellipseFx.setStroke(ShapeUtil.modelToFX(ellipse.getStrokeColor()));
                        ellipseFx.setStrokeWidth(ellipse.getW());
                        pane.getChildren().add(ellipseFx);
                    }
                    else {
                        ellipse.setRx(Math.abs(event.getX() - ellipse.getCenter().getX()));
                        ellipse.setRy(Math.abs(event.getY() - ellipse.getCenter().getY()));
                        javafx.scene.shape.Ellipse ellipseFx = (javafx.scene.shape.Ellipse)
                                pane.getChildren().get(pane.getChildren().size() - 1);
                        ellipseFx.setRadiusX(ellipse.getRx());
                        ellipseFx.setRadiusY(ellipse.getRy());
                    }
                    break;
            }
            pane.toBack();
        });


        GridPane root = new GridPane();
        root.addRow(0, buttonPane);
        root.addRow(1, shapePane);
        root.addRow(2, scrollPane);
        Scene scene = new Scene(root, 1600, 900);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(200);
        primaryStage.setTitle("Education system");
        //primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

