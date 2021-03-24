package util;

import model.*;
import model.Polygon;
import model.Rectangle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class FileUtil {

    private FileUtil() {
    }

    public static void save (Pic pic) throws JAXBException { // пересохранение
        save(pic, pic.getName());
    }

    public static void save (Pic pic, String filename) throws JAXBException { //сохранение

        JAXBContext jaxbContext = JAXBContext.newInstance(Pic.class, Dot.class, Ellipse.class, Line.class, Polyline.class,
                Polygon.class, Rectangle.class); // перечисление всех классов, которые контекст должен видеть,
                                 // необходимо, чтоюы получить рабочий объект Marshaller
        Marshaller marshaller = jaxbContext.createMarshaller(); // объект Marshaller,
                                                                // который будет преобразовывать объекты в XML
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // форматирование XML для удобного чтения
        marshaller.marshal(pic, new File(filename)); // преобразование в файл
    }

    public static Pic loading (String filename) throws JAXBException { // открытие файла

        JAXBContext jaxbContext = JAXBContext.newInstance(Pic.class, Dot.class, Ellipse.class, Line.class, Polyline.class,
                Polygon.class, Rectangle.class); // перечисление всех классов, которые контекст должен видеть,
                                 // необходимо, чтоюы получить рабочий объект Unmarshaller
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller(); // объект Unmarshall
        return  (Pic) unmarshaller.unmarshal(new File(filename)); // возвращаем результат из XML файла в класс
    }
}
