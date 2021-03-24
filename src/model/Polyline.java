package model;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
/**
 * Класс Polyline - полилиния. Реализуется как массив из линий.
 */
@XmlRootElement(name = "polyline") // определяем корневой элемент
@XmlAccessorType(XmlAccessType.FIELD) // Так как может не знать куда смотреть, необходимо указать, чтобы смотрела именно на поле.
                                    // Каждое нестатическое, не транзиентное (непереходное) поле в классе,
                                    // связанном с JAXB, будет автоматически связано с XML, если не указано иное
public class Polyline extends Shape {
    /**
     * Cледующие аннотации необходимы для форматирования и удобного чтения,
     * без них точки будут идти друг за другом, что приведет к неудобному чтению файла.
     * Точки вкладываются в общий элемент dots, а dot вложенные элементы внутри него.
     */

    @XmlElementWrapper(name = "dots")
    @XmlElement(name = "dot")
    private List<Dot> lineList = new LinkedList<>(); //массив из линий

    public Polyline(List<Dot> lineList) {
        this.lineList = lineList;
    }

    public Polyline() {
    }

    public List<Dot> getLineList() {
        return lineList;
    }

    public Polyline setLineList(List<Dot> lineList) {
        this.lineList = lineList;
        return this;
    }
}
